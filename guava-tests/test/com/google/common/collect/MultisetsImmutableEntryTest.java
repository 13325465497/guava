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
import static java.util.Collections.nCopies;
import static org.junit.Assert.assertThrows;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Multiset.Entry;
import com.google.common.testing.EqualsTester;
import junit.framework.TestCase;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Tests for {@link Multisets#immutableEntry}.
 *
 * @author Mike Bostock
 */
@GwtCompatible
@NullMarked
public class MultisetsImmutableEntryTest extends TestCase {
  private static final @Nullable String NE = null;

  private static <E extends @Nullable Object> Entry<E> entry(E element, int count) {
    return Multisets.immutableEntry(element, count);
  }

  private static <E extends @Nullable Object> Entry<E> control(E element, int count) {
    return HashMultiset.create(nCopies(count, element)).entrySet().iterator().next();
  }

  public void testToString() {
    assertThat(entry("foo", 1).toString()).isEqualTo("foo");
    assertThat(entry("bar", 2).toString()).isEqualTo("bar x 2");
  }

  public void testToStringNull() {
    assertThat(entry(NE, 1).toString()).isEqualTo("null");
    assertThat(entry(NE, 2).toString()).isEqualTo("null x 2");
  }

  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(control("foo", 1), entry("foo", 1))
        .addEqualityGroup(control("bar", 2), entry("bar", 2))
        .addEqualityGroup(control("foo", 2), entry("foo", 2))
        .addEqualityGroup(control("bar", 1), entry("bar", 1))
        .addEqualityGroup(control(NE, 1), entry(NE, 1))
        .addEqualityGroup(control(NE, 2), entry(NE, 2))
        .testEquals();
  }

  public void testNegativeCount() {
    assertThrows(IllegalArgumentException.class, () -> entry("foo", -1));
  }
}
