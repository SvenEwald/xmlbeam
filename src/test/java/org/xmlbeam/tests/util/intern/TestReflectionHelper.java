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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlbeam.util.intern.ReflectionHelper;

@SuppressWarnings("javadoc")
public class TestReflectionHelper {

    @Ignore
    public String withReturnTypeAndParameter(final String a) {
        return a;
    }

    @Ignore
    public Void strageSignature(final Void v) {
        return v;
    }

    @SuppressWarnings("rawtypes")
    @Ignore
    public List methodWithRawReturn() {
        return null;
    }

    @Ignore
    public List<String> methodWithNonRawReturn() {
        return null;
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

    @Test
    public void testRawTypeDetection() throws Exception {
        Method raw = TestReflectionHelper.class.getMethod("methodWithRawReturn", (Class[]) null);
        assertTrue(ReflectionHelper.isRawType(raw.getGenericReturnType()));
    }

    @Test
    public void testRawTypeDetectionForParameterizedTypes() throws Exception {
        Method nonraw = TestReflectionHelper.class.getMethod("methodWithNonRawReturn", (Class[]) null);
        assertFalse(ReflectionHelper.isRawType(nonraw.getGenericReturnType()));
    }

    @Test
    public void testNonGegenricTypeIsNoRawType() throws Exception {
        Method nonraw = TestReflectionHelper.class.getMethod("withReturnTypeAndParameter", new Class[] { String.class });
        assertFalse(ReflectionHelper.isRawType(nonraw.getGenericReturnType()));
    }
    
    @Test(expected = RuntimeException.class)
    public void testThrowThrowableWithoutArgs() throws Throwable {
        ReflectionHelper.throwThrowable(RuntimeException.class, new Object[]{});
    }
    
    @Test
    public void testThrowThrowableWithMatchingArgs() throws Throwable {
        boolean exceptionCaught = false;
        try {
        ReflectionHelper.throwThrowable(IllegalArgumentException.class, new Object[]{"My message"});
        } catch(IllegalArgumentException e) {
            exceptionCaught = true;
            assertEquals("My message", e.getMessage());
        }
        assertTrue(exceptionCaught);
    }
    
    @Test
    public void testThrowThrowableWithNotMatchingArgs() throws Throwable {
        boolean exceptionCaught = false;
        try {
        ReflectionHelper.throwThrowable(IllegalArgumentException.class, new Object[]{15});
        } catch(IllegalArgumentException e) {
            exceptionCaught = true;
            assertNull(e.getMessage());
        }
        assertTrue(exceptionCaught);
    }

//    @Test
//    public void testOverridenMethods() {
//        Method toStringMethod = ReflectionHelper.findMethodByName(Object.class, "toString");
//        Object o = new Object() {
//            public String toString() {
//                return "myOverridenString";
//            }
//        };
//        List<Method> findAllOverridenMethods = ReflectionHelper.findAllOverridenMethods(ReflectionHelper.findMethodByName(o.getClass(), "toString"));
//        assertTrue(findAllOverridenMethods.contains(toStringMethod));
//        assertTrue(findAllOverridenMethods.contains(ReflectionHelper.findMethodByName(o.getClass(), "toString")));
//        assertEquals(2,findAllOverridenMethods.size());
//    }

}
