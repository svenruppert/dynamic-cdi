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
package org.reflections.scanners;

import org.reflections.vfs.Vfs;


@Deprecated
public class TypesScanner extends AbstractScanner {

  @Override
  public Object scan(Vfs.File file , Object classObject) {
    classObject = super.scan(file , classObject);
    String className = getMetadataAdapter().getClassName(classObject);
    getStore().put(className , className);
    return classObject;
  }

  @Override
  public void scan(Object cls) {
    throw new UnsupportedOperationException("should not get here");
  }
}