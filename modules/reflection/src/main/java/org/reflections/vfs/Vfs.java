/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.reflections.vfs;

import org.rapidpm.dependencies.core.logger.Logger;
import org.rapidpm.dependencies.core.logger.LoggingService;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.Utils;
import repacked.com.google.common.base.Predicate;
import repacked.com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;


public abstract class Vfs {
  private static List<UrlType> defaultUrlTypes = new ArrayList<>(Arrays.asList(DefaultUrlTypes.values()));


  public static List<UrlType> getDefaultUrlTypes() {
    return defaultUrlTypes;
  }


  public static void setDefaultURLTypes(final List<UrlType> urlTypes) {
    defaultUrlTypes = urlTypes;
  }


  public static void addDefaultURLTypes(UrlType urlType) {
    defaultUrlTypes.add(0 , urlType);
  }


  public static Dir fromURL(final URL url , final UrlType... urlTypes) {
    return fromURL(url , Arrays.asList(urlTypes));
  }


  public static Dir fromURL(final URL url , final List<UrlType> urlTypes) {
    for (UrlType type : urlTypes) {
      try {
        if (type.matches(url)) {
          Dir dir = type.createDir(url);
          if (dir != null) return dir;
        }
      } catch (Throwable e) {
        final LoggingService log = Logger.getLogger(Reflections.class);
        if (log != null) {
          log.warning("could not create Dir using " + type + " from url " + url.toExternalForm() + ". skipping." , e);
        }
      }
    }

    throw new ReflectionsException("could not create Vfs.Dir from url, no matching UrlType was found [" + url.toExternalForm() + "]\n" +
                                   "either use fromURL(final URL url, final List<UrlType> urlTypes) or " +
                                   "use the static setDefaultURLTypes(final List<UrlType> urlTypes) or addDefaultURLTypes(UrlType urlType) " +
                                   "with your specialized UrlType.");
  }


  public static Iterable<File> findFiles(final Collection<URL> inUrls , final String packagePrefix , final Predicate<String> nameFilter) {
    Predicate<File> fileNamePredicate = file -> {
      String path = file.getRelativePath();
      if (path.startsWith(packagePrefix)) {
        String filename = path.substring(path.indexOf(packagePrefix) + packagePrefix.length());
        return ! Utils.isEmpty(filename) && nameFilter.apply(filename.substring(1));
      } else {
        return false;
      }
    };

    return findFiles(inUrls , fileNamePredicate);
  }


  public static Iterable<File> findFiles(final Collection<URL> inUrls , final Predicate<File> filePredicate) {
    Iterable<File> result = new ArrayList<>();

    for (final URL url : inUrls) {
      try {
        result = Iterables
            .concat(result ,
                    Iterables.filter(() -> fromURL(url).getFiles().iterator() , filePredicate)
            );
      } catch (Throwable e) {
        final LoggingService log = Logger.getLogger(Reflections.class);
        if (log != null) {
          log.warning("could not findFiles for url. continuing. [" + url + "]" , e);
        }
      }
    }

    return result;
  }


  public static Dir fromURL(final URL url) {
    return fromURL(url , defaultUrlTypes);
  }


  @Nullable
  public static java.io.File getFile(URL url) {
    java.io.File file;
    String path;

    try {
      path = url.toURI().getSchemeSpecificPart();
      if ((file = new java.io.File(path)).exists()) return file;
    } catch (URISyntaxException e) {
    }

    try {
      path = URLDecoder.decode(url.getPath() , "UTF-8");
      if (path.contains(".jar!")) path = path.substring(0 , path.lastIndexOf(".jar!") + ".jar".length());
      if ((file = new java.io.File(path)).exists()) return file;

    } catch (UnsupportedEncodingException e) {
    }

    try {
      path = url.toExternalForm();
      if (path.startsWith("jar:")) path = path.substring("jar:".length());
      if (path.startsWith("wsjar:")) path = path.substring("wsjar:".length());
      if (path.startsWith("file:")) path = path.substring("file:".length());
      if (path.contains(".jar!")) path = path.substring(0 , path.indexOf(".jar!") + ".jar".length());
      if ((file = new java.io.File(path)).exists()) return file;

      path = path.replace("%20" , " ");
      if ((file = new java.io.File(path)).exists()) return file;

    } catch (Exception e) {
    }

    return null;
  }

  private static boolean hasJarFileInPath(URL url) {
    return url.toExternalForm().matches(".*\\.jar(\\!.*|$)");
  }


  public enum DefaultUrlTypes implements UrlType {
    jarFile {
      public boolean matches(URL url) {
        return url.getProtocol().equals("file") && hasJarFileInPath(url);
      }

      public Dir createDir(final URL url) throws Exception {
        return new ZipDir(new JarFile(getFile(url)));
      }
    },

    jarUrl {
      public boolean matches(URL url) {
        return "jar".equals(url.getProtocol()) || "zip".equals(url.getProtocol()) || "wsjar".equals(url.getProtocol());
      }

      public Dir createDir(URL url) throws Exception {
        try {
          URLConnection urlConnection = url.openConnection();
          if (urlConnection instanceof JarURLConnection) {
            return new ZipDir(((JarURLConnection) urlConnection).getJarFile());
          }
        } catch (Throwable e) { /*fallback*/ }
        java.io.File file = getFile(url);
        if (file != null) {
          return new ZipDir(new JarFile(file));
        }
        return null;
      }
    },

    directory {
      public boolean matches(URL url) {
        return url.getProtocol().equals("file") && ! hasJarFileInPath(url) &&
               getFile(url).isDirectory();
      }

      public Dir createDir(final URL url) throws Exception {
        return new SystemDir(getFile(url));
      }
    },

    jboss_vfs {
      public boolean matches(URL url) {
        return url.getProtocol().equals("vfs");
      }

      public Vfs.Dir createDir(URL url) throws Exception {
        Object content = url.openConnection().getContent();
        Class<?> virtualFile = ClasspathHelper.contextClassLoader().loadClass("org.jboss.vfs.VirtualFile");
        java.io.File physicalFile = (java.io.File) virtualFile.getMethod("getPhysicalFile").invoke(content);
        String name = (String) virtualFile.getMethod("getName").invoke(content);
        java.io.File file = new java.io.File(physicalFile.getParentFile() , name);
        if (! file.exists() || ! file.canRead()) file = physicalFile;
        return file.isDirectory() ? new SystemDir(file) : new ZipDir(new JarFile(file));
      }
    },

    jboss_vfsfile {
      public boolean matches(URL url) throws Exception {
        return "vfszip".equals(url.getProtocol()) || "vfsfile".equals(url.getProtocol());
      }

      public Dir createDir(URL url) throws Exception {
        return new UrlTypeVFS().createDir(url);
      }
    },

    bundle {
      public boolean matches(URL url) throws Exception {
        return url.getProtocol().startsWith("bundle");
      }

      public Dir createDir(URL url) throws Exception {
        return fromURL((URL) ClasspathHelper.contextClassLoader().
            loadClass("org.eclipse.core.runtime.FileLocator").getMethod("resolve" , URL.class).invoke(null , url));
      }
    },

    jarInputStream {
      public boolean matches(URL url) throws Exception {
        return url.toExternalForm().contains(".jar");
      }

      public Dir createDir(final URL url) throws Exception {
        return new JarInputDir(url);
      }
    }
  }


  public interface Dir {
    String getPath();

    Iterable<File> getFiles();

    void close();
  }


  public interface File {
    String getName();

    String getRelativePath();

    InputStream openInputStream() throws IOException;
  }

  public interface UrlType {
    boolean matches(URL url) throws Exception;

    Dir createDir(URL url) throws Exception;
  }
}
