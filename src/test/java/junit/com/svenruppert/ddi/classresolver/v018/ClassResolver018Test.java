/*
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
package junit.com.svenruppert.ddi.classresolver.v018;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.ResponsibleFor;
import com.svenruppert.ddi.implresolver.ClassResolver;

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
 * <p>
 * Created by RapidPM - Team on 02.06.16.
 */
public class ClassResolver018Test
    extends DDIBaseTest {

  public static final Context context = Context.SPEZIAL;

  @Test
  public void test001() {
    Assertions.assertEquals(ServiceB.class, DI.activateDI(Service.class).getClass());
  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolver implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      System.out.println("toggle = " + context);
      return (context.equals(Context.SPEZIAL)) ? ServiceB.class : ServiceA.class;
    }
  }

}




