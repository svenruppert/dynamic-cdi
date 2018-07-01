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

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
public abstract class FluentIterable<E> implements Iterable<E> {


  public static <T> Iterable<T> concat(Iterable<? extends T> a , Iterable<? extends T> b) {
    return Stream
        .concat(
            StreamSupport.stream(a.spliterator() , false) ,
            StreamSupport.stream(b.spliterator() , false))
        .collect(Collectors.toList());
//    return concat(ImmutableList.of(a, b));
  }


  public static <T> Iterable<T> concat(final Iterable<Iterable<T>> inputs) {


    return StreamSupport
        .stream(inputs.spliterator() , false)
        .filter(Objects::nonNull)
        .flatMap(itreable -> StreamSupport.stream(itreable.spliterator() , false))
        .collect(Collectors.toList());
  }
}
