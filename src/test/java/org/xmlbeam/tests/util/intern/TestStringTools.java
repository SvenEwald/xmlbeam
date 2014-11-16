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

import java.lang.reflect.Method;

import org.junit.Test;
import org.xmlbeam.util.intern.duplex.DuplexExpression;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestStringTools {

    @Test
    public void testStringReplacer1() {
        assertEquals("acde", removeStringPart("abcde", 1, 2));
    }

    @Test
    public void testStringReplacer2() {
        assertEquals("bcde", removeStringPart("abcde", 0, 1));
    }

    @Test
    public void testStringReplacer3() {
        assertEquals("abcd", removeStringPart("abcde", 4, 5));
    }

    @Test
    public void testStringReplacer4() {
        assertEquals("", removeStringPart("abcde", 0, 5));
    }

    private final static String removeStringPart(final String string, final int begin, final int end) {
        //  return string.substring(0, begin) + (end > string.length() ? "" : string.substring(end, string.length()));
        Method method;
        try {
            method = DuplexExpression.class.getDeclaredMethod("removeStringPart", String.class, Integer.TYPE, Integer.TYPE);
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
        method.setAccessible(true);
        try {
            return (String) method.invoke(null, string, begin, end);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
