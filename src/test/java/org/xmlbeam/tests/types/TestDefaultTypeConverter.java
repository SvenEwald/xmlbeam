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

import org.junit.Test;
import org.xmlbeam.types.DefaultTypeConverter;
import org.xmlbeam.types.TypeConverter;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestDefaultTypeConverter {

    TypeConverter converter = new DefaultTypeConverter();

    @Test
    public void ensureConversionDefauls() {
        Object[] defaults = new Object[] { //
        Boolean.class, Boolean.FALSE, Boolean.TYPE, Boolean.FALSE,//
                Byte.class, Byte.valueOf((byte) 0), Byte.TYPE, Byte.valueOf((byte) 0),//
                Short.class, Short.valueOf((short) 0), Short.TYPE, Short.valueOf((short) 0),//
                Integer.class, Integer.valueOf(0), Integer.TYPE, Integer.valueOf(0),//
                Float.class, Float.valueOf(0), Float.TYPE, Float.valueOf(0),//
                Double.class, Double.valueOf(0), Double.TYPE, Double.valueOf(0),//
                Long.class, Long.valueOf(0), Long.TYPE, Long.valueOf(0), //
                Character.class, Character.valueOf(' '), Character.TYPE, Character.valueOf(' ') //
        };

        for (int i = 0; i < defaults.length; i += 2) {
            Class<?> type = (Class<?>) defaults[i];
            Object value = defaults[i + 1];
            assertTrue(converter.isConvertable(type));
            assertEquals(value, converter.convertTo(type, ""));
            assertEquals(value, converter.convertTo(type, value.toString()));
        }
    }

    @Test
    public void ensureBoolean() {
        for (Class<?> c : new Class<?>[] { Boolean.class, Boolean.TYPE }) {
            assertEquals(Boolean.FALSE, converter.convertTo(c, ""));
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
        }
    }

    @Test
    public void ensureShort() {
        for (Class<?> c : new Class<?>[] { Short.class, Short.TYPE }) {
            assertEquals(Short.valueOf((short) -1), converter.convertTo(c, "-1"));
            assertEquals(Short.valueOf(Short.MIN_VALUE), converter.convertTo(c, "-32768"));
            assertEquals(Short.valueOf(Short.MAX_VALUE), converter.convertTo(c, "32767"));
        }
    }
}
