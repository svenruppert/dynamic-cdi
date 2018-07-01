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
package junit.org.reflections;

import org.junit.jupiter.api.BeforeAll;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import static java.util.Collections.singletonList;

/** */
public class ReflectionsParallelTest extends ReflectionsTest {

  @BeforeAll
  public static void init() {
    reflections = new Reflections(new ConfigurationBuilder()
                                      .setUrls(singletonList(ClasspathHelper.forClass(TestModel.class)))
                                      .filterInputsBy(TestModelFilter)
                                      .setScanners(
                                          new SubTypesScanner(false) ,
                                          new TypeAnnotationsScanner() ,
                                          new FieldAnnotationsScanner() ,
                                          new MethodAnnotationsScanner() ,
                                          new MethodParameterScanner() ,
                                          new MethodParameterNamesScanner() ,
                                          new MemberUsageScanner())
                                      .useParallelExecutor());
  }
}
