/*
 * Copyright (C) 2010 The Guava Authors
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

package com.google.common.base;

import static com.google.common.base.Strings.repeat;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.testing.NullPointerTester;
import junit.framework.TestCase;
import org.jspecify.annotations.NullMarked;

/**
 * Unit test for {@link Strings}.
 *
 * @author Kevin Bourrillion
 */
@NullMarked
@GwtCompatible
public class StringsTest extends TestCase {
  public void testNullToEmpty() {
    assertThat(Strings.nullToEmpty(null)).isEqualTo("");
    assertThat(Strings.nullToEmpty("")).isEqualTo("");
    assertThat(Strings.nullToEmpty("a")).isEqualTo("a");
  }

  public void testEmptyToNull() {
    assertThat(Strings.emptyToNull(null)).isNull();
    assertThat(Strings.emptyToNull("")).isNull();
    assertThat(Strings.emptyToNull("a")).isEqualTo("a");
  }

  public void testIsNullOrEmpty() {
    assertTrue(Strings.isNullOrEmpty(null));
    assertTrue(Strings.isNullOrEmpty(""));
    assertFalse(Strings.isNullOrEmpty("a"));
  }

  public void testPadStart_noPadding() {
    assertThat(Strings.padStart("", 0, '-')).isSameInstanceAs("");
    assertThat(Strings.padStart("x", 0, '-')).isSameInstanceAs("x");
    assertThat(Strings.padStart("x", 1, '-')).isSameInstanceAs("x");
    assertThat(Strings.padStart("xx", 0, '-')).isSameInstanceAs("xx");
    assertThat(Strings.padStart("xx", 2, '-')).isSameInstanceAs("xx");
  }

  public void testPadStart_somePadding() {
    assertThat(Strings.padStart("", 1, '-')).isEqualTo("-");
    assertThat(Strings.padStart("", 2, '-')).isEqualTo("--");
    assertThat(Strings.padStart("x", 2, '-')).isEqualTo("-x");
    assertThat(Strings.padStart("x", 3, '-')).isEqualTo("--x");
    assertThat(Strings.padStart("xx", 3, '-')).isEqualTo("-xx");
  }

  public void testPadStart_negativeMinLength() {
    assertThat(Strings.padStart("x", -1, '-')).isSameInstanceAs("x");
  }

  @SuppressWarnings("nullness") // test of a bogus call
  public void testPadStart_null() {
    assertThrows(NullPointerException.class, () -> Strings.padStart(null, 5, '0'));
  }

  public void testPadEnd_noPadding() {
    assertThat(Strings.padEnd("", 0, '-')).isSameInstanceAs("");
    assertThat(Strings.padEnd("x", 0, '-')).isSameInstanceAs("x");
    assertThat(Strings.padEnd("x", 1, '-')).isSameInstanceAs("x");
    assertThat(Strings.padEnd("xx", 0, '-')).isSameInstanceAs("xx");
    assertThat(Strings.padEnd("xx", 2, '-')).isSameInstanceAs("xx");
  }

  public void testPadEnd_somePadding() {
    assertThat(Strings.padEnd("", 1, '-')).isEqualTo("-");
    assertThat(Strings.padEnd("", 2, '-')).isEqualTo("--");
    assertThat(Strings.padEnd("x", 2, '-')).isEqualTo("x-");
    assertThat(Strings.padEnd("x", 3, '-')).isEqualTo("x--");
    assertThat(Strings.padEnd("xx", 3, '-')).isEqualTo("xx-");
  }

  public void testPadEnd_negativeMinLength() {
    assertThat(Strings.padEnd("x", -1, '-')).isSameInstanceAs("x");
  }

  @SuppressWarnings("nullness") // test of a bogus call
  public void testPadEnd_null() {
    assertThrows(NullPointerException.class, () -> Strings.padEnd(null, 5, '0'));
  }

  @SuppressWarnings("InlineMeInliner") // test of method that doesn't just delegate
  public void testRepeat() {
    String input = "20";
    assertThat(repeat(input, 0)).isEqualTo("");
    assertThat(repeat(input, 1)).isEqualTo("20");
    assertThat(repeat(input, 2)).isEqualTo("2020");
    assertThat(repeat(input, 3)).isEqualTo("202020");

    assertThat(repeat("", 4)).isEqualTo("");

    for (int i = 0; i < 100; ++i) {
      assertEquals(2 * i, repeat(input, i).length());
    }

    assertThrows(IllegalArgumentException.class, () -> repeat("x", -1));
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> repeat("12345678", (1 << 30) + 3));
  }

  @SuppressWarnings({
    "InlineMeInliner", // test of method that doesn't just delegate
    "nullness", // test of a bogus call
  })
  public void testRepeat_null() {
    assertThrows(NullPointerException.class, () -> repeat(null, 5));
  }

  @SuppressWarnings("UnnecessaryStringBuilder") // We want to test a non-String CharSequence
  public void testCommonPrefix() {
    assertThat(Strings.commonPrefix("", "")).isEqualTo("");
    assertThat(Strings.commonPrefix("abc", "")).isEqualTo("");
    assertThat(Strings.commonPrefix("", "abc")).isEqualTo("");
    assertThat(Strings.commonPrefix("abcde", "xyz")).isEqualTo("");
    assertThat(Strings.commonPrefix("xyz", "abcde")).isEqualTo("");
    assertThat(Strings.commonPrefix("xyz", "abcxyz")).isEqualTo("");
    assertThat(Strings.commonPrefix("abc", "aaaaa")).isEqualTo("a");
    assertThat(Strings.commonPrefix("aa", "aaaaa")).isEqualTo("aa");
    assertThat(Strings.commonPrefix(new StringBuilder("abcdef"), "abcxyz")).isEqualTo("abc");

    // Identical valid surrogate pairs.
    assertThat(Strings.commonPrefix("abc\uD8AB\uDCABdef", "abc\uD8AB\uDCABxyz"))
        .isEqualTo("abc\uD8AB\uDCAB");
    // Differing valid surrogate pairs.
    assertThat(Strings.commonPrefix("abc\uD8AB\uDCABdef", "abc\uD8AB\uDCACxyz")).isEqualTo("abc");
    // One invalid pair.
    assertThat(Strings.commonPrefix("abc\uD8AB\uDCABdef", "abc\uD8AB\uD8ABxyz")).isEqualTo("abc");
    // Two identical invalid pairs.
    assertThat(Strings.commonPrefix("abc\uD8AB\uD8ACdef", "abc\uD8AB\uD8ACxyz"))
        .isEqualTo("abc\uD8AB\uD8AC");
    // Two differing invalid pairs.
    assertThat(Strings.commonPrefix("abc\uD8AB\uD8ABdef", "abc\uD8AB\uD8ACxyz"))
        .isEqualTo("abc\uD8AB");
    // One orphan high surrogate.
    assertThat(Strings.commonPrefix("\uD8AB\uDCAB", "\uD8AB")).isEqualTo("");
    // Two orphan high surrogates.
    assertThat(Strings.commonPrefix("\uD8AB", "\uD8AB")).isEqualTo("\uD8AB");
  }

  @SuppressWarnings("UnnecessaryStringBuilder") // We want to test a non-String CharSequence
  public void testCommonSuffix() {
    assertThat(Strings.commonSuffix("", "")).isEqualTo("");
    assertThat(Strings.commonSuffix("abc", "")).isEqualTo("");
    assertThat(Strings.commonSuffix("", "abc")).isEqualTo("");
    assertThat(Strings.commonSuffix("abcde", "xyz")).isEqualTo("");
    assertThat(Strings.commonSuffix("xyz", "abcde")).isEqualTo("");
    assertThat(Strings.commonSuffix("xyz", "xyzabc")).isEqualTo("");
    assertThat(Strings.commonSuffix("abc", "ccccc")).isEqualTo("c");
    assertThat(Strings.commonSuffix("aa", "aaaaa")).isEqualTo("aa");
    assertThat(Strings.commonSuffix(new StringBuilder("xyzabc"), "xxxabc")).isEqualTo("abc");

    // Identical valid surrogate pairs.
    assertThat(Strings.commonSuffix("abc\uD8AB\uDCABdef", "xyz\uD8AB\uDCABdef"))
        .isEqualTo("\uD8AB\uDCABdef");
    // Differing valid surrogate pairs.
    assertThat(Strings.commonSuffix("abc\uD8AB\uDCABdef", "abc\uD8AC\uDCABdef")).isEqualTo("def");
    // One invalid pair.
    assertThat(Strings.commonSuffix("abc\uD8AB\uDCABdef", "xyz\uDCAB\uDCABdef")).isEqualTo("def");
    // Two identical invalid pairs.
    assertThat(Strings.commonSuffix("abc\uD8AB\uD8ABdef", "xyz\uD8AB\uD8ABdef"))
        .isEqualTo("\uD8AB\uD8ABdef");
    // Two differing invalid pairs.
    assertThat(Strings.commonSuffix("abc\uDCAB\uDCABdef", "abc\uDCAC\uDCABdef"))
        .isEqualTo("\uDCABdef");
    // One orphan low surrogate.
    assertThat(Strings.commonSuffix("x\uD8AB\uDCAB", "\uDCAB")).isEqualTo("");
    // Two orphan low surrogates.
    assertThat(Strings.commonSuffix("\uDCAB", "\uDCAB")).isEqualTo("\uDCAB");
  }

  public void testValidSurrogatePairAt() {
    assertTrue(Strings.validSurrogatePairAt("\uD8AB\uDCAB", 0));
    assertTrue(Strings.validSurrogatePairAt("abc\uD8AB\uDCAB", 3));
    assertTrue(Strings.validSurrogatePairAt("abc\uD8AB\uDCABxyz", 3));
    assertFalse(Strings.validSurrogatePairAt("\uD8AB\uD8AB", 0));
    assertFalse(Strings.validSurrogatePairAt("\uDCAB\uDCAB", 0));
    assertFalse(Strings.validSurrogatePairAt("\uD8AB\uDCAB", -1));
    assertFalse(Strings.validSurrogatePairAt("\uD8AB\uDCAB", 1));
    assertFalse(Strings.validSurrogatePairAt("\uD8AB\uDCAB", -2));
    assertFalse(Strings.validSurrogatePairAt("\uD8AB\uDCAB", 2));
    assertFalse(Strings.validSurrogatePairAt("x\uDCAB", 0));
    assertFalse(Strings.validSurrogatePairAt("\uD8ABx", 0));
  }

  @SuppressWarnings("LenientFormatStringValidation") // Intentional for testing.
  public void testLenientFormat() {
    assertThat(Strings.lenientFormat("%s")).isEqualTo("%s");
    assertThat(Strings.lenientFormat("%s", 5)).isEqualTo("5");
    assertThat(Strings.lenientFormat("foo", 5)).isEqualTo("foo [5]");
    assertThat(Strings.lenientFormat("foo", 5, 6, 7)).isEqualTo("foo [5, 6, 7]");
    assertThat(Strings.lenientFormat("%s %s %s", "%s", 1, 2)).isEqualTo("%s 1 2");
    assertThat(Strings.lenientFormat("", 5, 6)).isEqualTo(" [5, 6]");
    assertThat(Strings.lenientFormat("%s%s%s", 1, 2, 3)).isEqualTo("123");
    assertThat(Strings.lenientFormat("%s%s%s", 1)).isEqualTo("1%s%s");
    assertThat(Strings.lenientFormat("%s + 6 = 11", 5)).isEqualTo("5 + 6 = 11");
    assertThat(Strings.lenientFormat("5 + %s = 11", 6)).isEqualTo("5 + 6 = 11");
    assertThat(Strings.lenientFormat("5 + 6 = %s", 11)).isEqualTo("5 + 6 = 11");
    assertThat(Strings.lenientFormat("%s + %s = %s", 5, 6, 11)).isEqualTo("5 + 6 = 11");
    assertThat(Strings.lenientFormat("%s + %s = %s", (Object[]) new Integer[] {5, 6, 11}))
        .isEqualTo("5 + 6 = 11");
    assertThat(Strings.lenientFormat("%s", null, null, null)).isEqualTo("null [null, null]");
    assertThat(Strings.lenientFormat(null, 5, 6)).isEqualTo("null [5, 6]");
    assertThat(Strings.lenientFormat("%s", (Object) null)).isEqualTo("null");
  }

  @J2ktIncompatible // TODO(b/319404022): Allow passing null array as varargs
  public void testLenientFormat_nullArrayVarargs() {
    assertThat(Strings.lenientFormat("%s", (Object[]) null)).isEqualTo("(Object[])null");
  }

  @GwtIncompatible // GWT reflection includes less data
  public void testLenientFormat_badArgumentToString() {
    assertThat(Strings.lenientFormat("boiler %s plate", new ThrowsOnToString()))
        .matches(
            // J2kt nested class name does not use "$"
            "boiler <com\\.google\\.common\\.base\\.StringsTest[.$]ThrowsOnToString@[0-9a-f]+ "
                + "threw java\\.lang\\.UnsupportedOperationException> plate");
  }

  public void testLenientFormat_badArgumentToString_gwtFriendly() {
    assertThat(Strings.lenientFormat("boiler %s plate", new ThrowsOnToString()))
        .matches("boiler <.*> plate");
  }

  private static class ThrowsOnToString {
    @Override
    public String toString() {
      throw new UnsupportedOperationException();
    }
  }

  @J2ktIncompatible
  @GwtIncompatible // NullPointerTester
  public void testNullPointers() {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(Strings.class);
  }
}
