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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.xmlbeam.types.DefaultTypeConverter;
import org.xmlbeam.types.TypeConverter;

/**
 * @author sven
 */
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
                Character.class, Character.valueOf(' '),Character.TYPE,Character.valueOf(' ')//  
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
        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, ""));
        assertEquals(Boolean.TRUE, converter.convertTo(Boolean.class, "true"));
        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, "false"));
        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, "asdfasdf"));
    }

    @Test
    public void ensureByte() {
        assertEquals(Byte.valueOf((byte) -1), converter.convertTo(Byte.class, "-1"));
        assertEquals(Byte.valueOf(Byte.MAX_VALUE),converter.convertTo(Byte.class, "127"));
        assertEquals(Byte.valueOf(Byte.MIN_VALUE),converter.convertTo(Byte.class, "-128"));
    }

    @Test
    public void ensureShort() {
        assertEquals(Short.valueOf((byte) -1), converter.convertTo(Short.class, "-1"));
    }
}
