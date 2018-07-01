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

import java.util.Iterator;
import java.util.NoSuchElementException;


public abstract class AbstractIterator<E> implements Iterator<E> {
  private State state = State.NOT_READY;
  private E next;


  protected AbstractIterator() {
  }


  protected final E endOfData() {
    state = State.DONE;
    return null;
  }


  public final E peek() {
    if (! hasNext()) {
      throw new NoSuchElementException();
    }
    return next;
  }

  protected abstract E computeNext();

  @Override
  public final boolean hasNext() {
    checkState(state != State.FAILED);
    switch (state) {
      case DONE:
        return false;
      case READY:
        return true;
      default:
    }
    return tryToComputeNext();
  }

  private boolean tryToComputeNext() {
    state = State.FAILED; // temporary pessimism
    next = computeNext();
    if (state != State.DONE) {
      state = State.READY;
      return true;
    }
    return false;
  }

  @Override
  public final E next() {
    if (! hasNext()) {
      throw new NoSuchElementException();
    }
    state = State.NOT_READY;
    E result = next;
    next = null;
    return result;
  }

  public void checkState(boolean expression) {
    if (! expression) {
      throw new IllegalStateException();
    }
  }


  private enum State {
    READY,
    NOT_READY,
    DONE,
    FAILED,
  }
}
