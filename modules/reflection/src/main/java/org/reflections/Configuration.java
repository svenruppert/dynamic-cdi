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
package org.reflections;

import org.reflections.adapters.MetadataAdapter;
import org.reflections.scanners.Scanner;
import org.reflections.serializers.Serializer;
import repacked.com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutorService;


public interface Configuration {

  Set<Scanner> getScanners();


  Set<URL> getUrls();


  @SuppressWarnings({"RawUseOfParameterizedType"})
  MetadataAdapter getMetadataAdapter();


  @Nullable
  Predicate<String> getInputsFilter();


  ExecutorService getExecutorService();


  Serializer getSerializer();


  @Nullable
  ClassLoader[] getClassLoaders();


  boolean shouldExpandSuperTypes();
}
