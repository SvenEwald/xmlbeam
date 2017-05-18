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
package org.xmlbeam.types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.xmlbeam.exceptions.XBException;
import org.xmlbeam.types.DefaultTypeConverter.Conversion;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 * @param <T>
 */
class StringFactoryConversion<T> extends Conversion<T> {

    private static final long serialVersionUID = 5720231829193321892L;
    private final Method factory;

    StringFactoryConversion(final Method factory, final T defaultValue) {
        super(defaultValue);
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T convert(final String data) {
        try {
            return (T) factory.invoke(null, data);
        } catch (IllegalArgumentException e) {
            assert false : "Unreachable code";
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            assert false : "Unreachable code";
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new XBException("Exception while trying to invoke factory method " + factory.toGenericString(), e.getCause());
        }
    }

    @Override
    public T getDefaultValue(final String value) {
        return convert(value);
    }
}
