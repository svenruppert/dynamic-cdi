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
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.reflections.serializers.JsonSerializer;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import repacked.com.google.common.base.Predicate;

import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

/** */
public class ReflectionsCollectTest extends ReflectionsTest {

  @BeforeAll
  public static void init() {
    Reflections ref = new Reflections(new ConfigurationBuilder()
            .addUrls(ClasspathHelper.forClass(TestModel.class))
            .filterInputsBy(TestModelFilter)
            .setScanners(
                    new SubTypesScanner(false),
                    new TypeAnnotationsScanner(),
                    new MethodAnnotationsScanner(),
                    new MethodParameterNamesScanner(),
                    new MethodParameterScanner(),
                    new MemberUsageScanner()));

    ref.save(getUserDir() + "/target/test-classes" + "/META-INF/reflections/testModel-reflections.xml");

    ref = new Reflections(new ConfigurationBuilder()
        .setUrls(asList(ClasspathHelper.forClass(TestModel.class)))
        .filterInputsBy(TestModelFilter)
        .setScanners(
            new MethodParameterScanner()));

    final JsonSerializer serializer = new JsonSerializer();
    ref.save(getUserDir() + "/target/test-classes" + "/META-INF/reflections/testModel-reflections.json", serializer);

    reflections = Reflections
        .collect()
        .merge(Reflections.collect("META-INF/reflections",
                                   new FilterBuilder().include(".*-reflections.json"),
                                   serializer));
  }

  @Test
  public void testResourcesScanner() {
    Predicate<String> filter = new FilterBuilder().include(".*\\.xml").include(".*\\.json");
    Reflections reflections = new Reflections(new ConfigurationBuilder()
        .filterInputsBy(filter)
        .setScanners(new ResourcesScanner())
        .setUrls(asList(ClasspathHelper.forClass(TestModel.class))));

    Set<String> resolved = reflections.getResources(Pattern.compile(".*resource1-reflections\\.xml"));
    assertThat(resolved, are("META-INF/reflections/resource1-reflections.xml"));

    Set<String> resources = reflections.getStore().get(ResourcesScanner.class.getSimpleName()).keySet();
    assertThat(resources, are("resource1-reflections.xml", "resource2-reflections.xml",
        "testModel-reflections.xml", "testModel-reflections.json"));
  }
}
