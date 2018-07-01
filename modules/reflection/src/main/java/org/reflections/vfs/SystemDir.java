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

import repacked.com.google.common.collect.AbstractIterator;

import java.io.File;
import java.util.*;


public class SystemDir implements Vfs.Dir {
  private final File file;

  public SystemDir(File file) {
    if (file != null && (! file.isDirectory() || ! file.canRead())) {
      throw new RuntimeException("cannot use dir " + file);
    }

    this.file = file;
  }

  private static List<File> listFiles(final File file) {
    File[] files = file.listFiles();

    if (files != null)
      return new ArrayList<>(Arrays.asList(files));
    else
      return new ArrayList<>();
  }

  @Override
  public String toString() {
    return getPath();
  }

  public String getPath() {
    if (file == null) {
      return "/NO-SUCH-DIRECTORY/";
    }
    return file.getPath().replace("\\" , "/");
  }

  public Iterable<Vfs.File> getFiles() {
    if (file == null || ! file.exists()) {
      return Collections.emptyList();
    }
    return () -> new AbstractIterator<Vfs.File>() {
      final Stack<File> stack = new Stack<>();

      {
        stack.addAll(listFiles(file));
      }

      protected Vfs.File computeNext() {
        while (! stack.isEmpty()) {
          final File file = stack.pop();
          if (file.isDirectory()) {
            stack.addAll(listFiles(file));
          } else {
            return new SystemFile(SystemDir.this , file);
          }
        }

        return endOfData();
      }
    };
  }

  public void close() {
  }
}
