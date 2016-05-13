package junit.org.rapidpm.ddi.reflections.v002;

import org.junit.Ignore;
import org.junit.Test;
import org.rapidpm.ddi.reflections.ReflectionUtils;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by RapidPM - Team on 13.05.16.
 */
@Ignore
public class ReflectionUtilsV002Test {

  @Test
  public void test001() throws Exception {
    final ReflectionUtils utils = new ReflectionUtils();
    utils.setDelegatorToProxy(new TestImplStaticLoggingProxy(), new TestImpl(), TestInterface.class);
  }

  @Test
  public void test002() throws Exception {
    final ReflectionUtils utils = new ReflectionUtils();
    utils.setDelegatorToProxy(new TestImplStaticLoggingProxy(), () -> null,TestInterface.class);
  }

  @Test
  public void test003() throws Exception {
    final ReflectionUtils utils = new ReflectionUtils();
    utils.setDelegatorToProxy(new TestImplStaticLoggingProxy(), new TestInterface(){
      @Override
      public String doWork() {
        return "";
      }
    },TestInterface.class);
  }

  @Test
  public void test001a() throws Exception {
    final ReflectionUtils utils = new ReflectionUtils();
    utils.setDelegatorToProxyViaMethod(new TestImplStaticLoggingProxy(), new TestImpl());
  }

  @Test
  public void test002a() throws Exception {
    final ReflectionUtils utils = new ReflectionUtils();
    utils.setDelegatorToProxyViaMethod(new TestImplStaticLoggingProxy(), (TestInterface) () -> null);
  }

  @Test
  public void test003a() throws Exception {
    final ReflectionUtils utils = new ReflectionUtils();
    utils.setDelegatorToProxyViaMethod(new TestImplStaticLoggingProxy(), new TestInterface(){
      @Override
      public String doWork() {
        return "";
      }
    });
  }




}
