/*
 * Copyright (C) 2026 The Guava Authors
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

package com.google.common.cache;

import com.google.common.cache.AbstractCache.StatsCounter;
import com.google.common.cache.LocalCache.Timestamped;
import java.util.LinkedHashMap;
import org.jspecify.annotations.Nullable;

/**
 * LinkedHashMap that enforces its maximum size and logs events in a StatsCounter object and an
 * optional RemovalListener.
 *
 * @param <K> the base key type
 * @param <V> the base value type
 */
final class CapacityEnforcingMap<K, V> extends LinkedHashMap<K, Timestamped<V>> {

  private final StatsCounter statsCounter;
  private final @Nullable RemovalListener<? super K, ? super V> removalListener;
  private final long maximumSize;

  CapacityEnforcingMap(
      int initialCapacity,
      float loadFactor,
      boolean accessOrder,
      long maximumSize,
      StatsCounter statsCounter,
      @Nullable RemovalListener<? super K, ? super V> removalListener) {
    super(initialCapacity, loadFactor, accessOrder);
    this.maximumSize = maximumSize;
    this.statsCounter = statsCounter;
    this.removalListener = removalListener;
  }

  @Override
  protected boolean removeEldestEntry(Entry<K, Timestamped<V>> ignored) {
    boolean removal = (maximumSize == LocalCache.UNSET_INT) ? false : (size() > maximumSize);
    if ((removalListener != null) && removal) {
      removalListener.onRemoval(
          RemovalNotification.create(
              ignored.getKey(), ignored.getValue().getValue(), RemovalCause.SIZE));
    }
    statsCounter.recordEviction();
    return removal;
  }
}
