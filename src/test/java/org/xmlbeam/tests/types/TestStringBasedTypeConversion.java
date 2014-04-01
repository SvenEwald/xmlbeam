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
package org.xmlbeam.tests.types;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XBException;
import org.xmlbeam.types.DefaultTypeConverter;
import org.xmlbeam.types.TypeConverter;

/**
 *
 */
public class TestStringBasedTypeConversion {

    private final TypeConverter typeConverter = new DefaultTypeConverter();
    
    public static abstract class CustomClass {
        public final String param;
        protected CustomClass(final String param) {
            this.param=param;
        }
    }
    
    public static class CustomObjectWithPrivateConstructor {
       private CustomObjectWithPrivateConstructor(String string) {};
    }

    public static class CustomObjectThrowingException extends CustomClass {
        public CustomObjectThrowingException(String s) throws IOException
        { super(s);throw new IOException(); }
    }
    
    public static class CustomClassWithStringConstrucor extends CustomClass {
      
        public CustomClassWithStringConstrucor(String param) {
            super(param);
        }
    }
    
    public static class CustomClassWithStaticFactory extends CustomClass {
        private CustomClassWithStaticFactory(String param) {
            super(param);
        }
        public static CustomClassWithStaticFactory valueOf(String s) {
            return new CustomClassWithStaticFactory(s);
        }
    }
    
    public static class CustomClassWithShortStaticFactory extends CustomClass {
        private CustomClassWithShortStaticFactory(String param) {
            super(param);
        }
        public static CustomClassWithShortStaticFactory of(String s) {
            return new CustomClassWithShortStaticFactory(s);
        }
    }
    
    @Test
    public void ensureNotConvertable() {
       assertFalse(typeConverter.isConvertable(Object.class));
       assertFalse(typeConverter.isConvertable(CustomObjectWithPrivateConstructor.class));
    }
    
    @Test(expected=IOException.class)
    public void testConstructorThrowingException() throws Throwable {
        try {
        ensureConversion(CustomObjectThrowingException.class);
        } catch (XBException e) {
            throw e.getCause();
        }
    }
    
    @Test
    public void testStringBasedConstructor() {
        ensureConversion(CustomClassWithStringConstrucor.class);
    }
    
    @Test
    public void testStaticFactoryBasedConversion() {
        ensureConversion(CustomClassWithStaticFactory.class);
    }
    
    @Test
    public void testShortStaticFactoryBasedConversion() {
        ensureConversion(CustomClassWithShortStaticFactory.class);
    }
    
    private void ensureConversion(Class<? extends CustomClass> clazz) {
        assertTrue(typeConverter.isConvertable(clazz));
        CustomClass object = typeConverter.convertTo(clazz, "foo");
        assertEquals("foo",object.param);
    }
    
    
}
