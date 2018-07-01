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


import repacked.com.google.common.base.Function;
import repacked.com.google.common.base.Predicate;

import java.util.Collection;
import java.util.Iterator;

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
public class Iterables {


  private Iterables() {
  }


  public static <T> Iterable<T> filter(
      final Iterable<T> unfiltered ,
      final Predicate<? super T> retainIfTrue) {

    if (unfiltered == null || retainIfTrue == null) throw new NullPointerException();
    return new FluentIterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return Iterators.filter(unfiltered.iterator() , retainIfTrue);
      }
    };
  }


  public static <T> Iterable<T> concat(Iterable<? extends T> a , Iterable<? extends T> b) {
    return FluentIterable.concat(a , b);
  }


  public static <T> Iterable<T> concat(Iterable<Iterable<T>> inputs) {
    return FluentIterable.concat(inputs);
  }


  public static <T> boolean isEmpty(final Iterable<T> iterable) {
    if (iterable instanceof Collection) {
      return ((Collection<?>) iterable).isEmpty();
    }
    return ! iterable.iterator().hasNext();
  }

  public static <T> T getOnlyElement(final Iterable<T> iterable) {
    final Iterator<T> iterator = iterable.iterator();
    return Iterators.getOnlyElement(iterator);
  }

  public static <T> boolean any(Iterable<T> iterable , Predicate<? super T> predicate) {
    return Iterators.any(iterable.iterator() , predicate);
  }

  public static boolean contains(final Iterable<?> iterable , final Object element) {
    if (iterable instanceof Collection) {
      Collection<?> collection = (Collection<?>) iterable;
      return Collections2.safeContains(collection , element);
    }
    return Iterators.contains(iterable.iterator() , element);
  }

  public static <F, T> Iterable<T> transform(
      final Iterable<F> fromIterable , final Function<? super F, ? extends T> function) {
    return new FluentIterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return Iterators.transform(fromIterable.iterator() , function);
      }
    };
  }

  public static <T> Iterable<T> limit(final Iterable<T> iterable , final int limitSize) {
    return new FluentIterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return Iterators.limit(iterable.iterator() , limitSize);
      }
    };
  }
}
