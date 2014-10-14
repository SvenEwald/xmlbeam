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
package org.xmlbeam;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 */
@SuppressWarnings("javadoc")
public class TestPreprocessor {

    static Method m;

    @BeforeClass
    public static void init() throws Exception {
        m = ProjectionInvocationHandler.class.getDeclaredMethod("applyParams", new Class<?>[] { String.class, Method.class, new Object[0].getClass() });
        m.setAccessible(true);
    }

    private String applyParams(final String string, final Method method, final Object[] args) throws Exception {
        return (String) m.invoke(null, string, method, args);
    }

    @Test
    public void ensureDoubleQuoteHandling() throws Exception {
        for (String q : new String[] { "{", "}" }) {
            assertEquals(q, applyParams(q, null, null));
            assertEquals(" " + q, applyParams(" " + q, null, null));
            assertEquals(q, applyParams(q + q, null, null));
            assertEquals(" " + q, applyParams(" " + q + q, null, null));
            assertEquals(" " + q + q, applyParams(" " + q + q + q + q, null, null));
            assertEquals(q + q + " ", applyParams(q + q + q + q + " ", null, null));
        }
    }

    @Test
    public void testPreprocessor() throws Exception {
        assertEquals("/foo/bar", applyParams("/foo/{0}", null, new Object[] { "bar" }));
        assertEquals("/foo/{0}", applyParams("/foo/{{0}}", null, new Object[] { "bar" }));
        assertEquals("/foo/{0}/tail", applyParams("/foo/{{0}}/tail", null, new Object[] { "bar" }));
        assertEquals("/foo/bar", applyParams("/{0}/{1}", null, new Object[] { "foo", "bar" }));
        assertEquals("/foo/{0}/tail", applyParams("/foo/{{0}}/{1}", null, new Object[] { "bar", "tail" }));
        assertEquals("/bar/barbar", applyParams("/{0}/{0}{0}", null, new Object[] { "bar" }));
    }

}
