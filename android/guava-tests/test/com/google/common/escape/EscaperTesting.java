/*
 * Copyright (C) 2009 The Guava Authors
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

package com.google.common.escape;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import org.jspecify.annotations.Nullable;

/** Utility methods for escaper tests. */
final class EscaperTesting {
  static CharEscaper createSimpleCharEscaper(ImmutableMap<Character, char[]> replacementMap) {
    checkNotNull(replacementMap);
    return new CharEscaper() {
      @Override
      protected char @Nullable [] escape(char c) {
        return replacementMap.get(c);
      }
    };
  }

  static UnicodeEscaper createSimpleUnicodeEscaper(ImmutableMap<Integer, char[]> replacementMap) {
    checkNotNull(replacementMap);
    return new UnicodeEscaper() {
      @Override
      protected char @Nullable [] escape(int cp) {
        return replacementMap.get(cp);
      }
    };
  }

  private EscaperTesting() {}
}
