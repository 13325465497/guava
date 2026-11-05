/*
 * Copyright (C) 2009 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.common.io;

import static com.google.common.io.Files.simplifyPath;
import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import junit.framework.TestCase;
import org.jspecify.annotations.NullUnmarked;

/**
 * Unit tests for {@link Files#simplifyPath}.
 *
 * @author Pablo Bellver
 */
@NullUnmarked
public class FilesSimplifyPathTest extends TestCase {

  public void testSimplifyEmptyString() {
    assertThat(simplifyPath("")).isEqualTo(".");
  }

  public void testSimplifyDot() {
    assertThat(simplifyPath(".")).isEqualTo(".");
  }

  public void testSimplifyWhiteSpace() {
    assertThat(simplifyPath(" ")).isEqualTo(" ");
  }

  public void testSimplify2() {
    assertThat(simplifyPath("x")).isEqualTo("x");
  }

  public void testSimplify3() {
    assertThat(simplifyPath("/a/b/c/d")).isEqualTo("/a/b/c/d");
  }

  public void testSimplify4() {
    assertThat(simplifyPath("/a/b/c/d/")).isEqualTo("/a/b/c/d");
  }

  public void testSimplify5() {
    assertThat(simplifyPath("/a//b")).isEqualTo("/a/b");
  }

  public void testSimplify6() {
    assertThat(simplifyPath("//a//b/")).isEqualTo("/a/b");
  }

  public void testSimplify7() {
    assertThat(simplifyPath("/..")).isEqualTo("/");
  }

  public void testSimplify8() {
    assertThat(simplifyPath("/././././")).isEqualTo("/");
  }

  public void testSimplify9() {
    assertThat(simplifyPath("/a/b/..")).isEqualTo("/a");
  }

  public void testSimplify10() {
    assertThat(simplifyPath("/a/b/../../..")).isEqualTo("/");
  }

  public void testSimplify11() {
    assertThat(simplifyPath("//a//b/..////../..//")).isEqualTo("/");
  }

  public void testSimplify12() {
    assertThat(simplifyPath("//a//../x//")).isEqualTo("/x");
  }

  public void testSimplify13() {
    assertThat(simplifyPath("a/b/../../../c")).isEqualTo("../c");
  }

  public void testSimplifyDotDot() {
    assertThat(simplifyPath("..")).isEqualTo("..");
  }

  public void testSimplifyDotDotSlash() {
    assertThat(simplifyPath("../")).isEqualTo("..");
    assertThat(simplifyPath("a/../..")).isEqualTo("..");
    assertThat(simplifyPath("a/../../")).isEqualTo("..");
  }

  public void testSimplifyDotDots() {
    assertThat(simplifyPath("a/../../..")).isEqualTo("../..");
    assertThat(simplifyPath("a/../../../..")).isEqualTo("../../..");
  }

  public void testSimplifyRootedDotDots() {
    assertThat(simplifyPath("/../../..")).isEqualTo("/");
    assertThat(simplifyPath("/../../../")).isEqualTo("/");
  }

  // b/4558855
  public void testMadbotsBug() {
    assertThat(simplifyPath("../this")).isEqualTo("../this");
    assertThat(simplifyPath("../this/is/ok")).isEqualTo("../this/is/ok");
    assertThat(simplifyPath("../this/../ok")).isEqualTo("../ok");
  }

  // https://github.com/google/guava/issues/705
  public void test705() {
    assertThat(simplifyPath("x/../../b")).isEqualTo("../b");
    assertThat(simplifyPath("x/../b")).isEqualTo("b");
  }

  // https://github.com/google/guava/issues/716
  public void test716() {
    assertThat(simplifyPath("./b")).isEqualTo("b");
    assertThat(simplifyPath("./b/.")).isEqualTo("b");
    assertThat(simplifyPath("././b/./.")).isEqualTo("b");
    assertThat(simplifyPath("././b")).isEqualTo("b");
    assertThat(simplifyPath("./a/b")).isEqualTo("a/b");
  }

  public void testHiddenFiles() {
    assertThat(simplifyPath(".b")).isEqualTo(".b");
    assertThat(simplifyPath("./.b")).isEqualTo(".b");
    assertThat(simplifyPath(".metadata/b")).isEqualTo(".metadata/b");
    assertThat(simplifyPath("./.metadata/b")).isEqualTo(".metadata/b");
  }

  // https://github.com/google/guava/issues/716
  public void testMultipleDotFilenames() {
    assertThat(simplifyPath("..a")).isEqualTo("..a");
    assertThat(simplifyPath("/..a")).isEqualTo("/..a");
    assertThat(simplifyPath("/..a/..b")).isEqualTo("/..a/..b");
    assertThat(simplifyPath("/.....a/..b")).isEqualTo("/.....a/..b");
    assertThat(simplifyPath("..../....")).isEqualTo("..../....");
    assertThat(simplifyPath("..a../..b..")).isEqualTo("..a../..b..");
  }

  public void testSlashDot() {
    assertThat(simplifyPath("/.")).isEqualTo("/");
  }

  // https://github.com/google/guava/issues/722
  public void testInitialSlashDotDot() {
    assertThat(simplifyPath("/../c")).isEqualTo("/c");
  }

  // https://github.com/google/guava/issues/722
  public void testInitialSlashDot() {
    assertThat(simplifyPath("/./a")).isEqualTo("/a");
    assertThat(simplifyPath("/.a/a/..")).isEqualTo("/.a");
  }

  // https://github.com/google/guava/issues/722
  public void testConsecutiveParentsAfterPresent() {
    assertThat(simplifyPath("./../../")).isEqualTo("../..");
    assertThat(simplifyPath("./.././../")).isEqualTo("../..");
  }

  /*
   * We co-opt some URI resolution tests for our purposes.
   * Some of the tests have queries and anchors that are a little silly here.
   */

  /** http://gbiv.com/protocols/uri/rfc/rfc2396.html#rfc.section.C.1 */
  public void testRfc2396Normal() {
    assertThat(simplifyPath("/a/b/c/g")).isEqualTo("/a/b/c/g");
    assertThat(simplifyPath("/a/b/c/./g")).isEqualTo("/a/b/c/g");
    assertThat(simplifyPath("/a/b/c/g/")).isEqualTo("/a/b/c/g");

    assertThat(simplifyPath("/a/b/c/g?y")).isEqualTo("/a/b/c/g?y");
    assertThat(simplifyPath("/a/b/c/g#s")).isEqualTo("/a/b/c/g#s");
    assertThat(simplifyPath("/a/b/c/g?y#s")).isEqualTo("/a/b/c/g?y#s");
    assertThat(simplifyPath("/a/b/c/;x")).isEqualTo("/a/b/c/;x");
    assertThat(simplifyPath("/a/b/c/g;x")).isEqualTo("/a/b/c/g;x");
    assertThat(simplifyPath("/a/b/c/g;x?y#s")).isEqualTo("/a/b/c/g;x?y#s");
    assertThat(simplifyPath("/a/b/c/.")).isEqualTo("/a/b/c");
    assertThat(simplifyPath("/a/b/c/./")).isEqualTo("/a/b/c");
    assertThat(simplifyPath("/a/b/c/..")).isEqualTo("/a/b");
    assertThat(simplifyPath("/a/b/c/../")).isEqualTo("/a/b");
    assertThat(simplifyPath("/a/b/c/../g")).isEqualTo("/a/b/g");
    assertThat(simplifyPath("/a/b/c/../..")).isEqualTo("/a");
    assertThat(simplifyPath("/a/b/c/../../")).isEqualTo("/a");
    assertThat(simplifyPath("/a/b/c/../../g")).isEqualTo("/a/g");
  }

  /** http://gbiv.com/protocols/uri/rfc/rfc2396.html#rfc.section.C.2 */
  public void testRfc2396Abnormal() {
    assertThat(simplifyPath("/a/b/c/g.")).isEqualTo("/a/b/c/g.");
    assertThat(simplifyPath("/a/b/c/.g")).isEqualTo("/a/b/c/.g");
    assertThat(simplifyPath("/a/b/c/g..")).isEqualTo("/a/b/c/g..");
    assertThat(simplifyPath("/a/b/c/..g")).isEqualTo("/a/b/c/..g");
    assertThat(simplifyPath("/a/b/c/./../g")).isEqualTo("/a/b/g");
    assertThat(simplifyPath("/a/b/c/./g/.")).isEqualTo("/a/b/c/g");
    assertThat(simplifyPath("/a/b/c/g/./h")).isEqualTo("/a/b/c/g/h");
    assertThat(simplifyPath("/a/b/c/g/../h")).isEqualTo("/a/b/c/h");
    assertThat(simplifyPath("/a/b/c/g;x=1/./y")).isEqualTo("/a/b/c/g;x=1/y");
    assertThat(simplifyPath("/a/b/c/g;x=1/../y")).isEqualTo("/a/b/c/y");
  }

  /** http://gbiv.com/protocols/uri/rfc/rfc3986.html#relative-normal */
  public void testRfc3986Normal() {
    assertThat(simplifyPath("/a/b/c/g")).isEqualTo("/a/b/c/g");
    assertThat(simplifyPath("/a/b/c/./g")).isEqualTo("/a/b/c/g");
    assertThat(simplifyPath("/a/b/c/g/")).isEqualTo("/a/b/c/g");

    assertThat(simplifyPath("/a/b/c/g?y")).isEqualTo("/a/b/c/g?y");
    assertThat(simplifyPath("/a/b/c/g#s")).isEqualTo("/a/b/c/g#s");
    assertThat(simplifyPath("/a/b/c/g?y#s")).isEqualTo("/a/b/c/g?y#s");
    assertThat(simplifyPath("/a/b/c/;x")).isEqualTo("/a/b/c/;x");
    assertThat(simplifyPath("/a/b/c/g;x")).isEqualTo("/a/b/c/g;x");
    assertThat(simplifyPath("/a/b/c/g;x?y#s")).isEqualTo("/a/b/c/g;x?y#s");

    assertThat(simplifyPath("/a/b/c/.")).isEqualTo("/a/b/c");
    assertThat(simplifyPath("/a/b/c/./")).isEqualTo("/a/b/c");
    assertThat(simplifyPath("/a/b/c/..")).isEqualTo("/a/b");
    assertThat(simplifyPath("/a/b/c/../")).isEqualTo("/a/b");
    assertThat(simplifyPath("/a/b/c/../g")).isEqualTo("/a/b/g");
    assertThat(simplifyPath("/a/b/c/../..")).isEqualTo("/a");
    assertThat(simplifyPath("/a/b/c/../../")).isEqualTo("/a");
    assertThat(simplifyPath("/a/b/c/../../g")).isEqualTo("/a/g");
  }

  /** http://gbiv.com/protocols/uri/rfc/rfc3986.html#relative-abnormal */
  public void testRfc3986Abnormal() {
    assertThat(simplifyPath("/a/b/c/../../../g")).isEqualTo("/g");
    assertThat(simplifyPath("/a/b/c/../../../../g")).isEqualTo("/g");

    assertThat(simplifyPath("/a/b/c/g.")).isEqualTo("/a/b/c/g.");
    assertThat(simplifyPath("/a/b/c/.g")).isEqualTo("/a/b/c/.g");
    assertThat(simplifyPath("/a/b/c/g..")).isEqualTo("/a/b/c/g..");
    assertThat(simplifyPath("/a/b/c/..g")).isEqualTo("/a/b/c/..g");
    assertThat(simplifyPath("/a/b/c/./../g")).isEqualTo("/a/b/g");
    assertThat(simplifyPath("/a/b/c/./g/.")).isEqualTo("/a/b/c/g");
    assertThat(simplifyPath("/a/b/c/g/./h")).isEqualTo("/a/b/c/g/h");
    assertThat(simplifyPath("/a/b/c/g/../h")).isEqualTo("/a/b/c/h");
    assertThat(simplifyPath("/a/b/c/g;x=1/./y")).isEqualTo("/a/b/c/g;x=1/y");
    assertThat(simplifyPath("/a/b/c/g;x=1/../y")).isEqualTo("/a/b/c/y");
  }

  public void testExtensiveWithAbsolutePrefix() throws IOException {
    // Inputs are /b/c/<every possible 10-character string of characters "a./">
    // Expected outputs are from realpath -s.
    doExtensiveTest("testdata/simplifypathwithabsoluteprefixtests.txt");
  }

  public void testExtensiveNoPrefix() throws IOException {
    /*
     * Inputs are <every possible 10-character string of characters "a./">
     *
     * Expected outputs are generated by the code itself, but they've been
     * checked against the inputs under Bash in order to confirm that the two
     * forms are equivalent (though not necessarily minimal, though we hope this
     * to be the case). Thus, this test is more of a regression test.
     *
     * Rough instructions to regenerate the test outputs and verify correctness:
     * - Temporarily change this test:
     * --- Comment out assertEquals.
     * --- System.out.println(input + " " + simplifyPath(input));
     * --- fail(). (If the test were to pass, its output would be hidden.)
     * - Run the test.
     * - Pull the relevant lines of output from the test into a testcases file.
     * - Test the output:
     * --- cat testcases | while read L; do
     *       X=($L)
     *       A=$( cd /b/c && sudo mkdir -p ${X[0]} && cd ${X[0]} && pwd |
     *           sed -e 's#^//*#/#' )
     *       B=$( cd /b/c && cd ${X[1]} && pwd )
     *       cmp -s <(echo $A) <(echo $B) || echo "$X[0] -> $A vs. $B"
     *     done | tee testoutput
     * - Move that testcases file to the appropriate name under testdata.
     *
     * The last test will take hours, and if it passes, the output will be empty.
     */
    doExtensiveTest("testdata/simplifypathnoprefixtests.txt");
  }

  private void doExtensiveTest(String resourceName) throws IOException {
    Splitter splitter = Splitter.on(CharMatcher.whitespace());
    URL url = getClass().getResource(resourceName);
    for (String line : Resources.readLines(url, UTF_8)) {
      Iterator<String> iterator = splitter.split(line).iterator();
      String input = iterator.next();
      String expectedOutput = iterator.next();
      assertFalse(iterator.hasNext());
      assertThat(simplifyPath(input)).isEqualTo(expectedOutput);
    }
  }
}
