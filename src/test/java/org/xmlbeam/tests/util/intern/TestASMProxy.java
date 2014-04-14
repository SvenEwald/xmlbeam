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

import org.junit.Test;
import org.xmlbeam.util.intern.ASMHelper;

/**
 * This test is needed to ensure correct parameter passing in the ASM proxy object. Every primitive
 * type needs to tested.
 */
@SuppressWarnings("javadoc")
public class TestASMProxy {

    public interface ProxyMe {

        int invokeMePlz(String a, String b, String c);

        //String invokeWithParam(String s);

        boolean passBoolean(boolean b);

        byte passByte(byte b);

        char passChar(char c);

        short passShort(short s);

        int passInt(int i);

        float passFloat(float f);

        long passLong(long l);

        double passDouble(double d);
    }

    final Object proxyObeject = new ProxyMe() {

        @Override
        public int invokeMePlz(final String a, final String b, final String c) {
            return a.length() + b.length() + c.length();
        }

        @Override
        public boolean passBoolean(final boolean b) {
            return b;
        }

        @Override
        public byte passByte(final byte b) {
            return b;
        }

        @Override
        public char passChar(final char c) {
            return c;
        }

        @Override
        public short passShort(final short s) {
            return s;
        }

        @Override
        public int passInt(final int i) {
            return i;
        }

        @Override
        public float passFloat(final float f) {
            return f;
        }

        @Override
        public long passLong(final long l) {
            return l;
        }

        @Override
        public double passDouble(final double d) {
            return d;
        }

    };

    final ProxyMe proxyMe = ASMHelper.createDefaultMethodProxy(ProxyMe.class, proxyObeject);

    @Test
    public void testProxyObject() {
        assertEquals(5, proxyMe.invokeMePlz("a", "bb", "cc"));
    }

    @Test
    public void testProxyBoolean() {
        assertEquals(true, proxyMe.passBoolean(true));
    }

    @Test
    public void testProxyByte() {
        assertEquals((byte) 7, proxyMe.passByte((byte) 7));
    }

    @Test
    public void testProxyChar() {
        assertEquals('x', proxyMe.passChar('x'));
    }

    @Test
    public void testProxyShort() {
        assertEquals((short) 23, proxyMe.passShort((short) 23));
    }

    @Test
    public void testProxyInt() {
        assertEquals(37, proxyMe.passInt(37));
    }

    @Test
    public void testProxyFloat() {
        assertEquals(37.5f, proxyMe.passFloat(37.5f), 0);
    }

    @Test
    public void testProxyLong() {
        assertEquals(372l, proxyMe.passLong(372l));
    }

    @Test
    public void testProxyDouble() {
        assertEquals(372d, proxyMe.passDouble(372d), 0);
    }
}
