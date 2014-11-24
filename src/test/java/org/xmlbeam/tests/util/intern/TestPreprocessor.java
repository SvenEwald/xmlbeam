/**
 *  Copyright 2014 Sven Ewald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.xmlbeam.tests.util.intern;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.xmlbeam.util.intern.Preprocessor;

/**
 */
@SuppressWarnings("javadoc")
public class TestPreprocessor {

    @Test
    public void ensureDoubleQuoteHandling() throws Exception {
        for (String q : new String[] { "{", "}" }) {
            assertEquals(q, applyParams(q+q, null, null));
            assertEquals(" " + q, applyParams(" " + q+q, null, null));
            assertEquals(q, applyParams(q + q, null, null));
            assertEquals(" " + q, applyParams(" " + q + q, null, null));
            assertEquals(" " + q + q, applyParams(" " + q + q + q + q, null, null));
            assertEquals(q + q + " ", applyParams(q + q + q + q + " ", null, null));
        }
    }

    private String applyParams(String q, Map<String, Integer> indexMap, Object[] args) {
        return Preprocessor.applyParams(q, indexMap, args);
    }

    @Test
    public void testPreprocessor() {
        assertEquals("abarb", applyParams("a{0}b", null, new Object[] { "bar" }));
        assertEquals("", applyParams("", null, new Object[] { "bar" }));
        assertEquals("abcd", applyParams("abcd", null, new Object[] { "bar" }));
        assertEquals("/foo/bar", applyParams("/foo/{0}", null, new Object[] { "bar" }));
        assertEquals("/foo/{0}", applyParams("/foo/{{0}}", null, new Object[] { "bar" }));
        assertEquals("/foo/{0}/tail", applyParams("/foo/{{0}}/tail", null, new Object[] { "bar" }));
        assertEquals("/foo/bar", applyParams("/{0}/{1}", null, new Object[] { "foo", "bar" }));
        assertEquals("/foo/{0}/tail", applyParams("/foo/{{0}}/{1}", null, new Object[] { "bar", "tail" }));
        assertEquals("/bar/barbar", applyParams("/{0}/{0}{0}", null, new Object[] { "bar" }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMailformedPreprocessor1() {
        applyParams("{", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMailformedPreprocessor2() {
        applyParams("abcd{", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMailformedPreprocessor3() {
        applyParams("abcd{f", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMailformedPreprocessor4() {
        applyParams("abcd{fg", null, null);
    }

}
