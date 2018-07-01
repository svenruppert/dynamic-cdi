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
package org.reflections.util;

import org.reflections.ReflectionsException;
import repacked.com.google.common.base.Joiner;
import repacked.com.google.common.base.Predicate;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class FilterBuilder implements Predicate<String> {
  private final List<Predicate<String>> chain;

  public FilterBuilder() {
    chain = new ArrayList<>();
  }

  private FilterBuilder(final Iterable<Predicate<String>> filters) {
    final Iterator<Predicate<String>> predicateIterator = filters.iterator();
    Stream<Predicate<String>> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(predicateIterator , Spliterator.ORDERED) , false);
    chain = stream.collect(Collectors.toList());
  }


  public static FilterBuilder parse(String includeExcludeString) {
    List<Predicate<String>> filters = new ArrayList<>();

    if (! Utils.isEmpty(includeExcludeString)) {
      for (String string : includeExcludeString.split(",")) {
        String trimmed = string.trim();
        char prefix = trimmed.charAt(0);
        String pattern = trimmed.substring(1);

        Predicate<String> filter;
        switch (prefix) {
          case '+':
            filter = new Include(pattern);
            break;
          case '-':
            filter = new Exclude(pattern);
            break;
          default:
            throw new ReflectionsException("includeExclude should start with either + or -");
        }

        filters.add(filter);
      }

      return new FilterBuilder(filters);
    } else {
      return new FilterBuilder();
    }
  }


  public static FilterBuilder parsePackages(String includeExcludeString) {
    List<Predicate<String>> filters = new ArrayList<>();

    if (! Utils.isEmpty(includeExcludeString)) {
      for (String string : includeExcludeString.split(",")) {
        String trimmed = string.trim();
        char prefix = trimmed.charAt(0);
        String pattern = trimmed.substring(1);
        if (! pattern.endsWith(".")) {
          pattern += ".";
        }
        pattern = prefix(pattern);

        Predicate<String> filter;
        switch (prefix) {
          case '+':
            filter = new Include(pattern);
            break;
          case '-':
            filter = new Exclude(pattern);
            break;
          default:
            throw new ReflectionsException("includeExclude should start with either + or -");
        }

        filters.add(filter);
      }

      return new FilterBuilder(filters);
    } else {
      return new FilterBuilder();
    }
  }

  public static String prefix(String qualifiedName) {
    return qualifiedName.replace("." , "\\.") + ".*";
  }

  private static String packageNameRegex(Class<?> aClass) {
    return prefix(aClass.getPackage().getName() + ".");
  }

  public FilterBuilder include(final String regex) {
    return add(new Include(regex));
  }

  public FilterBuilder add(Predicate<String> filter) {
    chain.add(filter);
    return this;
  }

  public FilterBuilder exclude(final String regex) {
    add(new Exclude(regex));
    return this;
  }

  public FilterBuilder includePackage(final Class<?> aClass) {
    return add(new Include(packageNameRegex(aClass)));
  }

  public FilterBuilder excludePackage(final Class<?> aClass) {
    return add(new Exclude(packageNameRegex(aClass)));
  }


  public FilterBuilder includePackage(final String... prefixes) {
    for (String prefix : prefixes) {
      add(new Include(prefix(prefix)));
    }
    return this;
  }


  public FilterBuilder excludePackage(final String prefix) {
    return add(new Exclude(prefix(prefix)));
  }

  @Override
  public String toString() {
    return Joiner
        .on(", ")
        .join(chain);
  }

  public boolean apply(String regex) {
    boolean accept = chain == null || chain.isEmpty() || chain.get(0) instanceof Exclude;

    if (chain != null) {
      for (Predicate<String> filter : chain) {
        if (accept && filter instanceof Include) {
          continue;
        } //skip if this filter won't change
        if (! accept && filter instanceof Exclude) {
          continue;
        }
        accept = filter.apply(regex);
        if (! accept && filter instanceof Exclude) {
          break;
        } //break on first exclusion
      }
    }
    return accept;
  }

  public abstract static class Matcher implements Predicate<String> {
    final Pattern pattern;

    public Matcher(final String regex) {
      pattern = Pattern.compile(regex);
    }

    public abstract boolean apply(String regex);

    @Override
    public String toString() {
      return pattern.pattern();
    }
  }

  public static class Include extends Matcher {
    public Include(final String patternString) {
      super(patternString);
    }

    @Override
    public boolean apply(final String regex) {
      return pattern.matcher(regex).matches();
    }

    @Override
    public String toString() {
      return "+" + super.toString();
    }
  }

  public static class Exclude extends Matcher {
    public Exclude(final String patternString) {
      super(patternString);
    }

    @Override
    public boolean apply(final String regex) {
      return ! pattern.matcher(regex).matches();
    }

    @Override
    public String toString() {
      return "-" + super.toString();
    }
  }
}
