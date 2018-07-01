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
import org.reflections.vfs.Vfs.Dir;
import org.reflections.vfs.Vfs.UrlType;
import repacked.com.google.common.base.Predicate;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UrlTypeVFS implements UrlType {
  public static final String[] REPLACE_EXTENSION = new String[]{".ear/" , ".jar/" , ".war/" , ".sar/" , ".har/" , ".par/"};

  private static final String VFSZIP = "vfszip";
  private static final String VFSFILE = "vfsfile";

  Predicate<File> realFile = file -> file.exists() && file.isFile();

  public boolean matches(URL url) {
    return VFSZIP.equals(url.getProtocol()) || VFSFILE.equals(url.getProtocol());
  }

  public Dir createDir(final URL url) {
    try {
      URL adaptedUrl = adaptURL(url);
      return new ZipDir(new JarFile(adaptedUrl.getFile()));
    } catch (Exception e) {
      try {
        return new ZipDir(new JarFile(url.getFile()));
      } catch (IOException e1) {
        final LoggingService log = Logger.getLogger(Reflections.class);
        if (log != null) {
          log.warning("Could not get URL" , e);
          log.warning("Could not get URL" , e1);
        }
      }
    }
    return null;
  }

  public URL adaptURL(URL url) throws MalformedURLException {
    if (VFSZIP.equals(url.getProtocol())) {
      return replaceZipSeparators(url.getPath() , realFile);
    } else if (VFSFILE.equals(url.getProtocol())) {
      return new URL(url.toString().replace(VFSFILE , "file"));
    } else {
      return url;
    }
  }

  URL replaceZipSeparators(String path , Predicate<File> acceptFile)
      throws MalformedURLException {
    int pos = 0;
    while (pos != - 1) {
      pos = findFirstMatchOfDeployableExtention(path , pos);

      if (pos > 0) {
        File file = new File(path.substring(0 , pos - 1));
        if (acceptFile.apply(file)) {
          return replaceZipSeparatorStartingFrom(path , pos);
        }
      }
    }

    throw new ReflectionsException("Unable to identify the real zip file in path '" + path + "'.");
  }

  int findFirstMatchOfDeployableExtention(String path , int pos) {
    Pattern p = Pattern.compile("\\.[ejprw]ar/");
    Matcher m = p.matcher(path);
    if (m.find(pos)) {
      return m.end();
    } else {
      return - 1;
    }
  }

  URL replaceZipSeparatorStartingFrom(String path , int pos)
      throws MalformedURLException {
    String zipFile = path.substring(0 , pos - 1);
    String zipPath = path.substring(pos);

    int numSubs = 1;
    for (String ext : REPLACE_EXTENSION) {
      while (zipPath.contains(ext)) {
        zipPath = zipPath.replace(ext , ext.substring(0 , 4) + "!");
        numSubs++;
      }
    }

    String prefix = "";
    for (int i = 0; i < numSubs; i++) {
      prefix += "zip:";
    }

    if (zipPath.trim().length() == 0) {
      return new URL(prefix + "/" + zipFile);
    } else {
      return new URL(prefix + "/" + zipFile + "!" + zipPath);
    }
  }
}
