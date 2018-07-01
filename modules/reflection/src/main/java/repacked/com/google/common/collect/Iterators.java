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
import repacked.com.google.common.base.Predicates;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
public class Iterators {

  private Iterators() {
  }


  public static <T> Iterator<T> filter(
      final Iterator<T> unfiltered , final Predicate<? super T> retainIfTrue) {
    if (unfiltered == null || retainIfTrue == null) throw new NullPointerException();
    return new AbstractIterator<T>() {
      @Override
      protected T computeNext() {
        while (unfiltered.hasNext()) {
          T element = unfiltered.next();
          if (retainIfTrue.apply(element)) {
            return element;
          }
        }
        return endOfData();
      }
    };
  }


  public static int size(Iterator<?> iterator) {
    long count = 0L;
    while (iterator.hasNext()) {
      iterator.next();
      count++;
    }
    return saturatedCast(count);
  }


  public static int saturatedCast(long value) {
    if (value > Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    if (value < Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }
    return (int) value;
  }


  public static <F, T> Iterator<T> transform(
      final Iterator<F> fromIterator , final Function<? super F, ? extends T> function) {
    return new TransformedIterator<F, T>(fromIterator) {
      @Override
      T transform(F from) {
        return function.apply(from);
      }
    };
  }

  public static <T> T getOnlyElement(final Iterator<T> iterator) {
    T first = iterator.next();
    if (! iterator.hasNext()) {
      return first;
    }

    StringBuilder sb = new StringBuilder().append("expected one element but was: <").append(first);
    for (int i = 0; i < 4 && iterator.hasNext(); i++) {
      sb.append(", ").append(iterator.next());
    }
    if (iterator.hasNext()) {
      sb.append(", ...");
    }
    sb.append('>');

    throw new IllegalArgumentException(sb.toString());
  }

  public static <T> boolean any(Iterator<T> iterator , Predicate<? super T> predicate) {
    return indexOf(iterator , predicate) != - 1;
  }

  public static boolean contains(final Iterator<?> iterator , final Object element) {
    return any(iterator , Predicates.equalTo(element));
  }

  public static <T> Iterator<T> limit(final Iterator<T> iterator , final int limitSize) {
    return new Iterator<T>() {
      private int count;

      @Override
      public boolean hasNext() {
        return count < limitSize && iterator.hasNext();
      }

      @Override
      public T next() {
        if (! hasNext()) {
          throw new NoSuchElementException();
        }
        count++;
        return iterator.next();
      }

      @Override
      public void remove() {
        iterator.remove();
      }
    };
  }

  public static <T> int indexOf(Iterator<T> iterator , Predicate<? super T> predicate) {
    for (int i = 0; iterator.hasNext(); i++) {
      T current = iterator.next();
      if (predicate.apply(current)) {
        return i;
      }
    }
    return - 1;
  }

  public static <T> boolean addAll(Collection<T> addTo , Iterator<? extends T> iterator) {
    boolean wasModified = false;
    while (iterator.hasNext()) {
      wasModified |= addTo.add(iterator.next());
    }
    return wasModified;
  }

  abstract static class TransformedIterator<F, T> implements Iterator<T> {
    final Iterator<? extends F> backingIterator;

    TransformedIterator(Iterator<? extends F> backingIterator) {
      this.backingIterator = backingIterator;
    }

    abstract T transform(F from);

    @Override
    public final boolean hasNext() {
      return backingIterator.hasNext();
    }

    @Override
    public final T next() {
      return transform(backingIterator.next());
    }

    @Override
    public final void remove() {
      backingIterator.remove();
    }
  }

}
