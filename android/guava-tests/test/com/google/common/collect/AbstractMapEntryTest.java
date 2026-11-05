/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.singletonMap;

import com.google.common.annotations.GwtCompatible;
import com.google.common.testing.EqualsTester;
import java.util.Map.Entry;
import junit.framework.TestCase;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Tests for {@code AbstractMapEntry}.
 *
 * @author Mike Bostock
 */
@GwtCompatible
@NullMarked
public class AbstractMapEntryTest extends TestCase {
  private static <K extends @Nullable Object, V extends @Nullable Object> Entry<K, V> entry(
      K key, V value) {
    return new AbstractMapEntry<K, V>() {
      @Override
      public K getKey() {
        return key;
      }

      @Override
      public V getValue() {
        return value;
      }
    };
  }

  private static <K extends @Nullable Object, V extends @Nullable Object> Entry<K, V> control(
      K key, V value) {
    return singletonMap(key, value).entrySet().iterator().next();
  }

  public void testToString() {
    assertThat(entry("foo", 1).toString()).isEqualTo("foo=1");
  }

  public void testToStringNull() {
    assertThat(entry(null, 1).toString()).isEqualTo("null=1");
    assertThat(entry("foo", null).toString()).isEqualTo("foo=null");
    assertThat(entry(null, null).toString()).isEqualTo("null=null");
  }

  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(entry("foo", 1), control("foo", 1))
        .addEqualityGroup(entry("foo", 2))
        .addEqualityGroup(entry("bar", 1))
        .addEqualityGroup(entry(null, 1), control(null, 1))
        .addEqualityGroup(entry("foo", null), control("foo", null))
        .addEqualityGroup(entry(null, null), control(null, null))
        .testEquals();
  }
}
