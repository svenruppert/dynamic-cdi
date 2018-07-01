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
package repacked.com.google.common.collect;

import repacked.com.google.common.base.Predicate;
import repacked.com.google.common.base.Predicates;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

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
 * Created by RapidPM - Team on 19.09.16.
 */
public class Sets {
  private Sets() {
  }


  //  public static <E> SetView<E> difference(final Set<E> set1, final Set<?> set2) {
  public static <E> Set<E> difference(final Set<E> set1 , final Set<?> set2) {
    final Predicate<Object> notInSet2 = Predicates.not(Predicates.in(set2));

    return new SetView<E>() {
      @Override
      public Iterator<E> iterator() {
        return Iterators.filter(set1.iterator() , notInSet2);
      }

      @Override
      public int size() {
        return Iterators.size(iterator());
      }

      @Override
      public boolean isEmpty() {
        return set2.containsAll(set1);
      }

      @Override
      public boolean contains(Object element) {
        return set1.contains(element) && ! set2.contains(element);
      }
    };
    //return null;
  }


  public abstract static class SetView<E> extends AbstractSet<E> {
    private SetView() {
    } // no subclasses but our own


    public Set<E> immutableCopy() {
      throw new RuntimeException("not yet factory - immutableCopy"); //Todo factory
      //return null;
//      return ImmutableSet.copyOf(this);
    }


    // Note: S should logically extend Set<? super E> but can't due to either
    // some javac bug or some weirdness in the spec, not sure which.
    public <S extends Set<E>> S copyInto(S set) {
      set.addAll(this);
      return set;
    }


    @Override
    public abstract Iterator<E> iterator();
  }
}
