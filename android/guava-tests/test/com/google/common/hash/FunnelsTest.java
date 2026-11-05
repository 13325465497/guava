/*
 * Copyright (C) 2011 The Guava Authors
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

package com.google.common.hash;

import static com.google.common.hash.Funnels.asOutputStream;
import static com.google.common.hash.Funnels.byteArrayFunnel;
import static com.google.common.hash.Funnels.integerFunnel;
import static com.google.common.hash.Funnels.longFunnel;
import static com.google.common.hash.Funnels.sequentialFunnel;
import static com.google.common.hash.Funnels.stringFunnel;
import static com.google.common.hash.Funnels.unencodedCharsFunnel;
import static com.google.common.testing.SerializableTester.reserialize;
import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.common.annotations.J2ktIncompatible;
import com.google.common.testing.EqualsTester;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import junit.framework.TestCase;
import org.jspecify.annotations.NullUnmarked;
import org.mockito.InOrder;

/**
 * Tests for HashExtractors.
 *
 * @author Dimitris Andreou
 */
@J2ktIncompatible
@NullUnmarked
public class FunnelsTest extends TestCase {
  public void testForBytes() {
    PrimitiveSink primitiveSink = mock(PrimitiveSink.class);
    byteArrayFunnel().funnel(new byte[] {4, 3, 2, 1}, primitiveSink);
    verify(primitiveSink).putBytes(new byte[] {4, 3, 2, 1});
  }

  public void testForBytes_null() {
    assertNullsThrowException(byteArrayFunnel());
  }

  public void testForStrings() {
    PrimitiveSink primitiveSink = mock(PrimitiveSink.class);
    unencodedCharsFunnel().funnel("test", primitiveSink);
    verify(primitiveSink).putUnencodedChars("test");
  }

  public void testForStrings_null() {
    assertNullsThrowException(unencodedCharsFunnel());
  }

  public void testForStringsCharset() {
    for (Charset charset : Charset.availableCharsets().values()) {
      PrimitiveSink primitiveSink = mock(PrimitiveSink.class);
      stringFunnel(charset).funnel("test", primitiveSink);
      verify(primitiveSink).putString("test", charset);
    }
  }

  public void testForStringsCharset_null() {
    for (Charset charset : Charset.availableCharsets().values()) {
      assertNullsThrowException(stringFunnel(charset));
    }
  }

  public void testForInts() {
    Integer value = 1234;
    PrimitiveSink primitiveSink = mock(PrimitiveSink.class);
    integerFunnel().funnel(value, primitiveSink);
    verify(primitiveSink).putInt(1234);
  }

  public void testForInts_null() {
    assertNullsThrowException(integerFunnel());
  }

  public void testForLongs() {
    Long value = 1234L;
    PrimitiveSink primitiveSink = mock(PrimitiveSink.class);
    longFunnel().funnel(value, primitiveSink);
    verify(primitiveSink).putLong(1234);
  }

  public void testForLongs_null() {
    assertNullsThrowException(longFunnel());
  }

  public void testSequential() {
    @SuppressWarnings({"unchecked", "DoNotMock"})
    Funnel<Object> elementFunnel = mock(Funnel.class);
    PrimitiveSink primitiveSink = mock(PrimitiveSink.class);
    Funnel<Iterable<?>> sequential = sequentialFunnel(elementFunnel);
    sequential.funnel(asList("foo", "bar", "baz", "quux"), primitiveSink);
    InOrder inOrder = inOrder(elementFunnel);
    inOrder.verify(elementFunnel).funnel("foo", primitiveSink);
    inOrder.verify(elementFunnel).funnel("bar", primitiveSink);
    inOrder.verify(elementFunnel).funnel("baz", primitiveSink);
    inOrder.verify(elementFunnel).funnel("quux", primitiveSink);
  }

  private static void assertNullsThrowException(Funnel<?> funnel) {
    PrimitiveSink primitiveSink =
        new AbstractStreamingHasher(4, 4) {
          @Override
          protected HashCode makeHash() {
            throw new UnsupportedOperationException();
          }

          @Override
          protected void process(ByteBuffer bb) {
            while (bb.hasRemaining()) {
              bb.get();
            }
          }
        };
    try {
      funnel.funnel(null, primitiveSink);
      fail();
    } catch (NullPointerException ok) {
    }
  }

  public void testAsOutputStream() throws Exception {
    PrimitiveSink sink = mock(PrimitiveSink.class);
    OutputStream out = asOutputStream(sink);
    byte[] bytes = {1, 2, 3, 4};
    out.write(255);
    out.write(bytes);
    out.write(bytes, 1, 2);
    verify(sink).putByte((byte) 255);
    verify(sink).putBytes(bytes);
    verify(sink).putBytes(bytes, 1, 2);
  }

  public void testSerialization() {
    assertThat(reserialize(byteArrayFunnel())).isSameInstanceAs(byteArrayFunnel());
    assertThat(reserialize(integerFunnel())).isSameInstanceAs(integerFunnel());
    assertThat(reserialize(longFunnel())).isSameInstanceAs(longFunnel());
    assertThat(reserialize(unencodedCharsFunnel())).isSameInstanceAs(unencodedCharsFunnel());
    assertEquals(sequentialFunnel(integerFunnel()), reserialize(sequentialFunnel(integerFunnel())));
    assertEquals(stringFunnel(US_ASCII), reserialize(stringFunnel(US_ASCII)));
  }

  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(byteArrayFunnel())
        .addEqualityGroup(integerFunnel())
        .addEqualityGroup(longFunnel())
        .addEqualityGroup(unencodedCharsFunnel())
        .addEqualityGroup(stringFunnel(UTF_8))
        .addEqualityGroup(stringFunnel(US_ASCII))
        .addEqualityGroup(
            sequentialFunnel(integerFunnel()), reserialize(sequentialFunnel(integerFunnel())))
        .addEqualityGroup(sequentialFunnel(longFunnel()))
        .testEquals();
  }
}
