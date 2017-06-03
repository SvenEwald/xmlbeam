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
package org.xmlbeam.tests.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import java.math.BigDecimal;

import org.junit.Test;
import org.xmlbeam.types.DefaultTypeConverter;
import org.xmlbeam.types.TypeConverter;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestDefaultTypeConverter {

    TypeConverter converter = new DefaultTypeConverter(Locale.getDefault(), TimeZone.getDefault());

    @Test
    public void ensureConversionDefauls() {
        Object[] defaults = new Object[] { //
                Boolean.class, null, Boolean.TYPE, Boolean.FALSE, //
                Byte.class, null, Byte.TYPE, Byte.valueOf((byte) 0), //
                Short.class, null, Short.TYPE, Short.valueOf((short) 0), //
                Integer.class, null, Integer.TYPE, Integer.valueOf(0), //
                Float.class, null, Float.TYPE, Float.valueOf(0), //
                Double.class, null, Double.TYPE, Double.valueOf(0), //
                Long.class, null, Long.TYPE, Long.valueOf(0), //
                Character.class, null, Character.TYPE, Character.valueOf(' ') //
        };

        for (int i = 0; i < defaults.length; i += 2) {
            Class<?> type = (Class<?>) defaults[i];
            Object value = defaults[i + 1];
            assertTrue(converter.isConvertable(type));
            //assertEquals(value, converter.convertTo(type, ""));
            assertEquals(value, converter.convertTo(type, value == null ? null : value.toString()));
        }
    }

    @Test
    public void ensureBoolean() {
        for (Class<?> c : new Class<?>[] { Boolean.class, Boolean.TYPE }) {
            assertEquals(Boolean.TRUE, converter.convertTo(c, "true"));
            assertEquals(Boolean.FALSE, converter.convertTo(c, "false"));
            assertEquals(Boolean.FALSE, converter.convertTo(c, "asdfasdf"));
        }
    }

    @Test
    public void ensureCharacter() {
        for (Class<?> c : new Class<?>[] { Character.class, Character.TYPE }) {
            assertEquals(Character.valueOf('A'), converter.convertTo(c, "  A "));
            assertEquals(Character.valueOf('X'), converter.convertTo(c, "X"));
        }
    }

    @Test
    public void ensureByte() {
        for (Class<?> c : new Class<?>[] { Byte.class, Byte.TYPE }) {
            assertEquals(Byte.valueOf((byte) -1), converter.convertTo(c, "-1"));
            assertEquals(Byte.valueOf(Byte.MAX_VALUE), converter.convertTo(c, "127"));
            assertEquals(Byte.valueOf(Byte.MIN_VALUE), converter.convertTo(c, "-128"));

            assertEquals(Byte.valueOf((byte) 124), converter.convertTo(c, "$124", "$###"));
        }
    }

    @Test
    public void ensureShort() {
        for (Class<?> c : new Class<?>[] { Short.class, Short.TYPE }) {
            assertEquals(Short.valueOf((short) -1), converter.convertTo(c, "-1"));
            assertEquals(Short.valueOf(Short.MIN_VALUE), converter.convertTo(c, "-32768"));
            assertEquals(Short.valueOf(Short.MAX_VALUE), converter.convertTo(c, "32767"));

            assertEquals(Short.valueOf((short) 124), converter.convertTo(c, "$124", "$###"));
        }
    }

    @Test
    public void ensureFloat() {
        for (Class<?> c : new Class<?>[] { Float.class, Float.TYPE }) {
            assertEquals(Float.valueOf(-1f), converter.convertTo(c, "-1"));
            assertEquals(Float.valueOf(-32768.0f), converter.convertTo(c, "-32768"));
            assertEquals(Float.valueOf(32767.0f), converter.convertTo(c, "32767"));

            assertEquals(Float.valueOf((short) 124), converter.convertTo(c, "$124", "$###"));
        }
    }

    @Test
    public void ensureDouble() {
        for (Class<?> c : new Class<?>[] { Double.class, Double.TYPE }) {
            assertEquals(Double.valueOf(-1d), converter.convertTo(c, "-1"));
            assertEquals(Double.valueOf(-32768.0d), converter.convertTo(c, "-32768"));
            assertEquals(Double.valueOf(32767.0d), converter.convertTo(c, "32767"));

            assertEquals(Double.valueOf((short) 124), converter.convertTo(c, "$124", "$###"));
        }
    }

    @Test
    public void ensureInteger() {
        for (Class<?> c : new Class<?>[] { Integer.class, Integer.TYPE }) {
            assertEquals(Integer.valueOf(-1), converter.convertTo(c, "-1"));
            assertEquals(Integer.valueOf(-32768), converter.convertTo(c, "-32768"));
            assertEquals(Integer.valueOf(32767), converter.convertTo(c, "32767"));

            assertEquals(Integer.valueOf(124), converter.convertTo(c, "$124", "$###"));
        }
    }

    @Test
    public void ensureLong() {
        for (Class<?> c : new Class<?>[] { Long.class, Long.TYPE }) {
            assertEquals(Long.valueOf(-1L), converter.convertTo(c, "-1"));
            assertEquals(Long.valueOf(-32768L), converter.convertTo(c, "-32768"));
            assertEquals(Long.valueOf(32767L), converter.convertTo(c, "32767"));

            assertEquals(Long.valueOf(124L), converter.convertTo(c, "$124", "$###"));
        }
    }

    @Test
    public void ensureDate() {
        assertEquals(549849600000L, new DefaultTypeConverter(Locale.US, TimeZone.getTimeZone("GMT")).convertTo(Date.class, "19870605", "yyyyMMdd").getTime());
    }

    @Test
    public void ensureNumber() {
        assertEquals(Double.valueOf(123456.987D), new DefaultTypeConverter(Locale.US, TimeZone.getTimeZone("GMT")).convertTo(Number.class, "123,456.987", "###,###.###"));
        assertEquals(Long.valueOf(123456), new DefaultTypeConverter(Locale.US, TimeZone.getTimeZone("GMT")).convertTo(Number.class, "123,456", "###,###"));
    }

    @Test
    public void ensureBigdecimal() {
        assertEquals(new BigDecimal("123456"), new DefaultTypeConverter(Locale.US, TimeZone.getTimeZone("GMT")).convertTo(BigDecimal.class, "123,456", ""));
    }

}
