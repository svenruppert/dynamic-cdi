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

import java.util.Map;

/**
 * Created by benjamin-bosch on 02.10.16.
 */
public class MultiMapEntry<KEY, VALUE> implements Map.Entry<KEY, VALUE> {

  private KEY key;
  private VALUE value;

  public MultiMapEntry(KEY key , VALUE value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public KEY getKey() {
    return key;
  }

  @Override
  public VALUE getValue() {
    return value;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MultiMapEntry<?, ?> that = (MultiMapEntry<?, ?>) o;

    if (key != null ? ! key.equals(that.key) : that.key != null) return false;
    return value != null ? value.equals(that.value) : that.value == null;

  }

  @Override
  public int hashCode() {
    int result = key != null ? key.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }

  @Override
  public VALUE setValue(VALUE value) {
    this.value = value;
    return this.value;
  }


}
