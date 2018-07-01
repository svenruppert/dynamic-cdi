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

import java.io.IOException;
import java.util.Iterator;

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
public class Joiner {


  private final String separator;


  private Joiner(String separator) {
    this.separator = checkNotNull(separator);
  }

  public static Joiner on(String separator) {
    return new Joiner(separator);
  }

  public static Joiner on(char separator) {
    return new Joiner(String.valueOf(separator));
  }

  public String join(final Iterable<?> valuesToJoin) {
    return join(valuesToJoin.iterator());
  }


  public final String join(Iterator<?> valuesToJoin) {
    return appendTo(new StringBuilder() , valuesToJoin).toString();
  }


  public final StringBuilder appendTo(StringBuilder builder , Iterator<?> valuesToJoin) {
    try {
      appendTo((Appendable) builder , valuesToJoin);
    } catch (IOException impossible) {
      throw new AssertionError(impossible);
    }
    return builder;
  }


  public <A extends Appendable> A appendTo(A appendable , Iterator<?> parts) throws IOException {
    checkNotNull(appendable);
    if (parts.hasNext()) {
      appendable.append(toString(parts.next()));
      while (parts.hasNext()) {
        appendable.append(separator);
        appendable.append(toString(parts.next()));
      }
    }
    return appendable;
  }

  CharSequence toString(Object part) {
    checkNotNull(part); // checkNotNull for GWT (do not optimize).
    return (part instanceof CharSequence) ? (CharSequence) part : part.toString();
  }

}
