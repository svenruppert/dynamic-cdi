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


import org.junit.jupiter.api.Test;
import org.reflections.util.ClasspathHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Test ClasspathHelper utility class
 */
public final class ClasspathHelperTest {
  @Test
  public void testForClassLoaderShouldntReorderUrls() throws MalformedURLException {
    // testing same URL set with different order to not fall into the case when HashSet orders elements in the same order as we do
    final URL[] urls1 = {new URL("file", "foo", 1111, "foo"), new URL("file", "bar", 1111, "bar"), new URL("file", "baz", 1111, "baz")};
    final List<URL> urlsList2 = Arrays.asList(urls1);
    Collections.reverse(urlsList2);
    final URL[] urls2 = urlsList2.toArray(new URL[urlsList2.size()]);

    final URLClassLoader urlClassLoader1 = new URLClassLoader(urls1, null);
    final URLClassLoader urlClassLoader2 = new URLClassLoader(urls2, null);
    final Collection<URL> resultUrls1 = ClasspathHelper.forClassLoader(urlClassLoader1);
    final Collection<URL> resultUrls2 = ClasspathHelper.forClassLoader(urlClassLoader2);

    assertArrayEquals(urls1,resultUrls1.toArray());
    assertArrayEquals( urls2, resultUrls2.toArray());
  }
}
