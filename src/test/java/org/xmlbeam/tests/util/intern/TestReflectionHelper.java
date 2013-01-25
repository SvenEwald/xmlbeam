/**
 *  Copyright 2013 Sven Ewald
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

import static junit.framework.Assert.assertFalse;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlbeam.util.intern.ReflectionHelper;

public class TestReflectionHelper {

    @Ignore
    public String withReturnTypeAndParameter(String a) {
        return a;
    }

    @Ignore
    public Void strageSignature(Void v) {
        return v;
    }

    @Test
    public void testNoReturnTypeNoParam() throws Exception {
        Method me = TestReflectionHelper.class.getMethod("testNoReturnTypeNoParam");
        assertFalse(ReflectionHelper.hasParameters(me));
        assertFalse(ReflectionHelper.hasReturnType(me));
        assertFalse(ReflectionHelper.hasReturnType(null));
        assertFalse(ReflectionHelper.hasParameters(null));
    }

    @Test
    public void testWithReturnTypeAndParam() throws Exception {
        Method m = TestReflectionHelper.class.getMethod("withReturnTypeAndParameter", String.class);
        assertTrue(ReflectionHelper.hasParameters(m));
        assertTrue(ReflectionHelper.hasReturnType(m));
    }

    @Test
    public void testStrangeSignature() throws Exception {
        Method s = TestReflectionHelper.class.getMethod("strageSignature", Void.class);
        assertTrue(ReflectionHelper.hasParameters(s));
        assertFalse(ReflectionHelper.hasReturnType(s));
    }

}
