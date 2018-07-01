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

import org.reflections.ReflectionsException;
import org.reflections.util.Utils;
import repacked.com.google.common.collect.AbstractIterator;

import java.io.IOException;
import java.net.URL;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

/**
 *
 */
public class JarInputDir implements Vfs.Dir {
  private final URL url;
  JarInputStream jarInputStream;
  long cursor = 0;
  long nextCursor = 0;

  public JarInputDir(URL url) {
    this.url = url;
  }

  public String getPath() {
    return url.getPath();
  }

  public Iterable<Vfs.File> getFiles() {
    return () -> new AbstractIterator<Vfs.File>() {

      {
        try {
          jarInputStream = new JarInputStream(url.openConnection().getInputStream());
        } catch (Exception e) {
          throw new ReflectionsException("Could not open url connection" , e);
        }
      }

      protected Vfs.File computeNext() {
        while (true) {
          try {
            ZipEntry entry = jarInputStream.getNextJarEntry();
            if (entry == null) {
              return endOfData();
            }

            long size = entry.getSize();
            if (size < 0) size = 0xffffffffL + size; //JDK-6916399
            nextCursor += size;
            if (! entry.isDirectory()) {
              return new JarInputFile(entry , JarInputDir.this , cursor , nextCursor);
            }
          } catch (IOException e) {
            throw new ReflectionsException("could not get next zip entry" , e);
          }
        }
      }
    };
  }

  public void close() {
    Utils.close(jarInputStream);
  }
}
