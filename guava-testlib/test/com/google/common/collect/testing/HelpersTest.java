/*
 * Copyright (C) 2008 The Guava Authors
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

package com.google.common.collect.testing;

import static com.google.common.collect.testing.Helpers.NullsBeforeB;
import static com.google.common.collect.testing.Helpers.assertContains;
import static com.google.common.collect.testing.Helpers.assertContainsAllOf;
import static com.google.common.collect.testing.Helpers.assertContentsInOrder;
import static com.google.common.collect.testing.Helpers.assertEmpty;
import static com.google.common.collect.testing.Helpers.assertEqualInOrder;
import static com.google.common.collect.testing.Helpers.testComparator;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyIterator;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertThrows;

import com.google.common.annotations.GwtCompatible;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * Unit test for {@link Helpers}.
 *
 * @author Chris Povirk
 */
@GwtCompatible
public class HelpersTest extends TestCase {
  public void testNullsBeforeB() {
    testComparator(NullsBeforeB.INSTANCE, "a", "azzzzzz", null, "b", "c");
  }

  public void testIsEmpty_iterable() {
    List<Object> list = new ArrayList<>();
    assertEmpty(list);
    assertEmpty(() -> emptyIterator());

    list.add("a");
    assertThrows(AssertionFailedError.class, () -> assertEmpty(list));
    assertThrows(
        AssertionFailedError.class,
        () ->
            assertEmpty(
                new Iterable<String>() {
                  @Override
                  public Iterator<String> iterator() {
                    return singleton("a").iterator();
                  }
                }));
  }

  public void testIsEmpty_map() {
    Map<Object, Object> map = new HashMap<>();
    assertEmpty(map);

    map.put("a", "b");
    assertThrows(AssertionFailedError.class, () -> assertEmpty(map));
  }

  public void testAssertEqualInOrder() {
    List<?> list = asList("a", "b", "c");
    assertEqualInOrder(list, list);

    List<?> fewer = asList("a", "b");
    assertThrows(AssertionFailedError.class, () -> assertEqualInOrder(list, fewer));

    assertThrows(AssertionFailedError.class, () -> assertEqualInOrder(fewer, list));

    List<?> differentOrder = asList("a", "c", "b");
    assertThrows(AssertionFailedError.class, () -> assertEqualInOrder(list, differentOrder));

    List<?> differentContents = asList("a", "b", "C");
    assertThrows(AssertionFailedError.class, () -> assertEqualInOrder(list, differentContents));
  }

  public void testAssertContentsInOrder() {
    List<?> list = asList("a", "b", "c");
    assertContentsInOrder(list, "a", "b", "c");

    assertThrows(AssertionFailedError.class, () -> assertContentsInOrder(list, "a", "b"));

    assertThrows(AssertionFailedError.class, () -> assertContentsInOrder(list, "a", "b", "c", "d"));

    assertThrows(AssertionFailedError.class, () -> assertContentsInOrder(list, "a", "c", "b"));

    assertThrows(AssertionFailedError.class, () -> assertContentsInOrder(list, "a", "B", "c"));
  }

  public void testAssertContains() {
    List<?> list = asList("a", "b");
    assertContains(list, "a");
    assertContains(list, "b");

    assertThrows(AssertionFailedError.class, () -> assertContains(list, "c"));
  }

  public void testAssertContainsAllOf() {
    List<?> list = asList("a", "a", "b", "c");
    assertContainsAllOf(list, "a");
    assertContainsAllOf(list, "a", "a");
    assertContainsAllOf(list, "a", "b", "c");
    assertContainsAllOf(list, "a", "b", "c", "a");

    assertThrows(AssertionFailedError.class, () -> assertContainsAllOf(list, "d"));

    assertThrows(AssertionFailedError.class, () -> assertContainsAllOf(list, "a", "b", "c", "d"));

    assertThrows(AssertionFailedError.class, () -> assertContainsAllOf(list, "a", "a", "a"));
  }
}
