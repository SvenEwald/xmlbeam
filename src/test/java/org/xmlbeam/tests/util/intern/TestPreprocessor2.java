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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.xmlbeam.util.intern.Preprocessor;

@SuppressWarnings("javadoc")
public class TestPreprocessor2 {
    private Map<String, Integer> paramNameIndexMap;
    private final Object[] args = new Object[] { "value0", "value1", "value2" };

    @Before
    public void init() {
        paramNameIndexMap = new HashMap<String, Integer>();
        paramNameIndexMap.put("huhu0", 0);
        paramNameIndexMap.put("huhu1", 1);
        paramNameIndexMap.put("huhu2", 2);
    }

    @Test
    public void testStringWithoutParameter() {
        assertEquals("abcdefg", Preprocessor.applyParams("abcdefg", paramNameIndexMap, args));
    }

    @Test
    public void testEscape1() {
        assertEquals("abc{defg", Preprocessor.applyParams("abc{{defg", paramNameIndexMap, args));
    }

    @Test
    public void testEscape2() {
        assertEquals("abc}defg", Preprocessor.applyParams("abc}}defg", paramNameIndexMap, args));
    }

    @Test
    public void testEscape3() {
        assertEquals("abc{}defg", Preprocessor.applyParams("abc{{}}defg", paramNameIndexMap, args));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParam1() {
        assertEquals("abc{value0}defg", Preprocessor.applyParams("abc{value0}defg", paramNameIndexMap, args));
    }

    @Test
    public void testParam2() {
        assertEquals("abcvalue0defg", Preprocessor.applyParams("abc{huhu0}defg", paramNameIndexMap, args));
    }

    @Test
    public void testParam3() {
        assertEquals("avalue0bvalue1cvalue2", Preprocessor.applyParams("a{param0}b{arg1}c{2}", paramNameIndexMap, args));
    }

}
