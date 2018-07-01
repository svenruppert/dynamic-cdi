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
package org.reflections.util;

import org.rapidpm.dependencies.core.logger.Logger;
import org.rapidpm.dependencies.core.logger.LoggingService;
import org.reflections.Reflections;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Helper methods for working with the classpath.
 */
public abstract class ClasspathHelper {


  public static Collection<URL> forPackage(String name , ClassLoader... classLoaders) {
    return forResource(resourceName(name) , classLoaders);
  }


  public static Collection<URL> forResource(String resourceName , ClassLoader... classLoaders) {
    final List<URL> result = new ArrayList<>();
    final ClassLoader[] loaders = classLoaders(classLoaders);
    for (ClassLoader classLoader : loaders) {
      try {
        final Enumeration<URL> urls = classLoader.getResources(resourceName);
        while (urls.hasMoreElements()) {
          final URL url = urls.nextElement();
          int index = url.toExternalForm().lastIndexOf(resourceName);
          if (index != - 1) {
            // Add old url as contextUrl to support exotic url handlers
            result.add(new URL(url , url.toExternalForm().substring(0 , index)));
          } else {
            result.add(url);
          }
        }
      } catch (IOException e) {
        final LoggingService log = Logger.getLogger(Reflections.class);
        if (log != null) {
          log.warning("error getting resources for " + resourceName , e);
        }
      }
    }
    return distinctUrls(result);
  }

  private static String resourceName(String name) {
    if (name != null) {
      String resourceName = name.replace("." , "/");
      resourceName = resourceName.replace("\\" , "/");
      if (resourceName.startsWith("/")) {
        resourceName = resourceName.substring(1);
      }
      return resourceName;
    }
    return null;
  }


  public static ClassLoader[] classLoaders(ClassLoader... classLoaders) {
    if (classLoaders != null && classLoaders.length != 0) {
      return classLoaders;
    } else {
      ClassLoader contextClassLoader = contextClassLoader(), staticClassLoader = staticClassLoader();
      return contextClassLoader != null ?
             staticClassLoader != null && contextClassLoader != staticClassLoader ?
             new ClassLoader[]{contextClassLoader , staticClassLoader} :
             new ClassLoader[]{contextClassLoader} :
             new ClassLoader[]{};

    }
  }

  //http://michaelscharf.blogspot.co.il/2006/11/javaneturlequals-and-hashcode-make.html
  private static Collection<URL> distinctUrls(Collection<URL> urls) {
    Map<String, URL> distinct = new LinkedHashMap<>(urls.size());
    for (URL url : urls) {
      distinct.put(url.toExternalForm() , url);
    }
    return distinct.values();
  }


  public static ClassLoader contextClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }


  public static ClassLoader staticClassLoader() {
    return Reflections.class.getClassLoader();
  }


  public static URL forClass(Class<?> aClass , ClassLoader... classLoaders) {
    final ClassLoader[] loaders = classLoaders(classLoaders);
    final String resourceName = aClass.getName().replace("." , "/") + ".class";
    for (ClassLoader classLoader : loaders) {
      try {
        final URL url = classLoader.getResource(resourceName);
        if (url != null) {
          final String normalizedUrl = url.toExternalForm().substring(0 , url.toExternalForm().lastIndexOf(aClass.getPackage().getName().replace("." , "/")));
          return new URL(normalizedUrl);
        }
      } catch (MalformedURLException e) {
        final LoggingService log = Logger.getLogger(Reflections.class);
        if (log != null) {
          log.warning("Could not get URL" , e);
        }
      }
    }
    return null;
  }


  public static Collection<URL> forClassLoader() {
//    return forClassLoader(classLoaders(), ClasspathHelper.class.getClassLoader());
    return forClassLoader(ClasspathHelper.class.getClassLoader());
  }


  public static Collection<URL> forClassLoader(ClassLoader... classLoaders) {
    final Collection<URL> result = new ArrayList<>();
    final ClassLoader[] loaders = classLoaders(classLoaders);
    for (ClassLoader classLoader : loaders) {
      while (classLoader != null) {
        if (classLoader instanceof URLClassLoader) {
          URL[] urls = ((URLClassLoader) classLoader).getURLs();
          if (urls != null) {
            result.addAll(Arrays.asList(urls));
          }
        }
        classLoader = classLoader.getParent();
      }
    }
    return distinctUrls(result);
  }


  public static Collection<URL> forJavaClassPath() {
    Collection<URL> urls = new ArrayList<>();
    String javaClassPath = System.getProperty("java.class.path");
    if (javaClassPath != null) {
      for (String path : javaClassPath.split(File.pathSeparator)) {
        try {
          urls.add(new File(path).toURI().toURL());
        } catch (Exception e) {
          final LoggingService log = Logger.getLogger(Reflections.class);
          if (log != null) {
            log.warning("Could not get URL" , e);
          }
        }
      }
    }
    return distinctUrls(urls);
  }


  public static Collection<URL> forWebInfLib(final ServletContext servletContext) {
    final Collection<URL> urls = new ArrayList<>();
    Set<?> resourcePaths = servletContext.getResourcePaths("/WEB-INF/lib");
    if (resourcePaths == null) {
      return urls;
    }
    for (Object urlString : resourcePaths) {
      try {
        urls.add(servletContext.getResource((String) urlString));
      } catch (MalformedURLException e) { /*fuck off*/ }
    }
    return distinctUrls(urls);
  }


  public static URL forWebInfClasses(final ServletContext servletContext) {
    try {
      final String path = servletContext.getRealPath("/WEB-INF/classes");
      if (path != null) {
        final File file = new File(path);
        if (file.exists())
          return file.toURL();
      } else {
        return servletContext.getResource("/WEB-INF/classes");
      }
    } catch (MalformedURLException e) { /*fuck off*/ }
    return null;
  }


  public static Collection<URL> forManifest() {
    return forManifest(forClassLoader());
  }


  public static Collection<URL> forManifest(final URL url) {
    final Collection<URL> result = new ArrayList<>();
    result.add(url);
    try {
      final String part = cleanPath(url);
      File jarFile = new File(part);
      JarFile myJar = new JarFile(part);
      URL validUrl = tryToGetValidUrl(jarFile.getPath() , new File(part).getParent() , part);
      if (validUrl != null) {
        result.add(validUrl);
      }
      final Manifest manifest = myJar.getManifest();
      if (manifest != null) {
        final String classPath = manifest.getMainAttributes().getValue(new Attributes.Name("Class-Path"));
        if (classPath != null) {
          for (String jar : classPath.split(" ")) {
            validUrl = tryToGetValidUrl(jarFile.getPath() , new File(part).getParent() , jar);
            if (validUrl != null) {
              result.add(validUrl);
            }
          }
        }
      }
    } catch (IOException e) {
      // don't do anything, we're going on the assumption it is a jar, which could be wrong
    }
    return distinctUrls(result);
  }


  public static Collection<URL> forManifest(final Iterable<URL> urls) {
    Collection<URL> result = new ArrayList<>();
    // determine if any of the URLs are JARs, and get any dependencies
    for (URL url : urls) {
      result.addAll(forManifest(url));
    }
    return distinctUrls(result);
  }

  //a little bit cryptic...
  static URL tryToGetValidUrl(String workingDir , String path , String filename) {
    try {
      if (new File(filename).exists())
        return new File(filename).toURI().toURL();
      if (new File(path + File.separator + filename).exists())
        return new File(path + File.separator + filename).toURI().toURL();
      if (new File(workingDir + File.separator + filename).exists())
        return new File(workingDir + File.separator + filename).toURI().toURL();
      if (new File(new URL(filename).getFile()).exists())
        return new File(new URL(filename).getFile()).toURI().toURL();
    } catch (MalformedURLException e) {
      // don't do anything, we're going on the assumption it is a jar, which could be wrong
    }
    return null;
  }


  public static String cleanPath(final URL url) {
    String path = url.getPath();
    try {
      path = URLDecoder.decode(path , "UTF-8");
    } catch (UnsupportedEncodingException e) { /**/ }
    if (path.startsWith("jar:")) {
      path = path.substring("jar:".length());
    }
    if (path.startsWith("file:")) {
      path = path.substring("file:".length());
    }
    if (path.endsWith("!/")) {
      path = path.substring(0 , path.lastIndexOf("!/")) + "/";
    }
    return path;
  }
}

