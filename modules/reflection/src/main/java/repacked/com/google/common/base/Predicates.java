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
package repacked.com.google.common.base;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

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
 * Created by RapidPM - Team on 18.09.16.
 */
public class Predicates {

  private Predicates() {
  }

  public static <T> Predicate<T> alwaysTrue() {
    return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
  }


  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return new NotPredicate<>(predicate);
  }


  public static <T> Predicate<T> in(Collection<? extends T> target) {
    return new InPredicate<>(target);
  }

  public static <T> boolean isEmpty(final Predicate<? super T>[] predicates) {
    return predicates == null || predicates.length == 0;
  }

  public static <T> Predicate<T> and(final Predicate<T>[] predicates) {
    return new AndPredicate<>(defensiveCopy(predicates));
  }

  @SafeVarargs
  private static <T> List<T> defensiveCopy(T... array) {
    return defensiveCopy(Arrays.asList(array));
  }

  private static <T> List<T> defensiveCopy(Iterable<T> iterable) {
    ArrayList<T> list = new ArrayList<>();
    for (T element : iterable) {
      list.add(checkNotNull(element));
    }
    return list;
  }


  public static <T> Predicate<T> isNull() {
    return ObjectPredicate.IS_NULL.withNarrowedType();
  }


  public static <T> Predicate<T> equalTo(@Nullable T target) {
    return (target == null) ? Predicates.<T>isNull() : new IsEqualToPredicate<T>(target);
  }


  enum ObjectPredicate implements Predicate<Object> {
    ALWAYS_TRUE {
      @Override
      public boolean apply(@Nullable Object o) {
        return true;
      }

      @Override
      public String toString() {
        return "Predicates.alwaysTrue()";
      }
    },
    ALWAYS_FALSE {
      @Override
      public boolean apply(@Nullable Object o) {
        return false;
      }

      @Override
      public String toString() {
        return "Predicates.alwaysFalse()";
      }
    },
    IS_NULL {
      @Override
      public boolean apply(@Nullable Object o) {
        return o == null;
      }

      @Override
      public String toString() {
        return "Predicates.isNull()";
      }
    },
    NOT_NULL {
      @Override
      public boolean apply(@Nullable Object o) {
        return o != null;
      }

      @Override
      public String toString() {
        return "Predicates.notNull()";
      }
    };

    @SuppressWarnings("unchecked")
      // safe contravariant cast
    <T> Predicate<T> withNarrowedType() {
      return (Predicate<T>) this;
    }
  }

  private static class InPredicate<T> implements Predicate<T>, Serializable {
    private static final long serialVersionUID = 0;
    private final Collection<?> target;

    private InPredicate(Collection<?> target) {
      this.target = checkNotNull(target);
    }

    @Override
    public boolean apply(T t) {
      try {
        return target.contains(t);
      } catch (NullPointerException | ClassCastException e) {
        return false;
      }
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof InPredicate) {
        InPredicate<?> that = (InPredicate<?>) obj;
        return target.equals(that.target);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return target.hashCode();
    }

    @Override
    public String toString() {
      return "Predicates.in(" + target + ")";
    }


  }

  private static class NotPredicate<T> implements Predicate<T>, Serializable {
    private static final long serialVersionUID = 0;
    final Predicate<T> predicate;

    NotPredicate(Predicate<T> predicate) {
      this.predicate = checkNotNull(predicate);
    }

    @Override
    public boolean apply(T t) {
      return ! predicate.apply(t);
    }

    @Override
    public int hashCode() {
      return ~ predicate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof NotPredicate) {
        NotPredicate<?> that = (NotPredicate<?>) obj;
        return predicate.equals(that.predicate);
      }
      return false;
    }

    @Override
    public String toString() {
      return "Predicates.not(" + predicate + ")";
    }


  }

  private static class AndPredicate<T> implements Predicate<T>, Serializable {
    private static final long serialVersionUID = 0;
    private final List<? extends Predicate<? super T>> components;

    private AndPredicate(List<? extends Predicate<? super T>> components) {
      this.components = components;
    }

    @Override
    public boolean apply(T t) {
      // Avoid using the Iterator to avoid generating garbage (issue 820).
      for (Predicate<? super T> component : components) {
        if (! component.apply(t)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      // add a random number to avoid collisions with OrPredicate
      return components.hashCode() + 0x12472c2c;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof AndPredicate) {
        AndPredicate<?> that = (AndPredicate<?>) obj;
        return components.equals(that.components);
      }
      return false;
    }

    @Override
    public String toString() {
      return "Predicates.and(" + Joiner.on(',').join(components) + ")";
    }


  }

  private static class IsEqualToPredicate<T> implements Predicate<T>, Serializable {
    private static final long serialVersionUID = 0;
    private final T target;

    private IsEqualToPredicate(T target) {
      this.target = target;
    }

    @Override
    public boolean apply(T t) {
      return target.equals(t);
    }

    @Override
    public int hashCode() {
      return target.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      if (obj instanceof IsEqualToPredicate) {
        IsEqualToPredicate<?> that = (IsEqualToPredicate<?>) obj;
        return target.equals(that.target);
      }
      return false;
    }

    @Override
    public String toString() {
      return "Predicates.equalTo(" + target + ")";
    }
  }
}
