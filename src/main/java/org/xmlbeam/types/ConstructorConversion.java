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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.xmlbeam.exceptions.XBException;
import org.xmlbeam.types.DefaultTypeConverter.Conversion;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
class StringConstructorConversion<T> extends Conversion<T> {

    private static final long serialVersionUID = 7668145462451625954L;
    final private Constructor<T> constructor;

    /**
     * @param defaultValue
     */
    protected StringConstructorConversion(final Constructor<T> constructor, final T defaultValue) {
        super(defaultValue);
        this.constructor = constructor;
    }

    @Override
    public T convert(final String data) {
        try {
            return constructor.newInstance(data);
        } catch (IllegalArgumentException e) {
            assert false : "Unreachable code";
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new XBException("Exception while trying to invoke constructor " + constructor.toGenericString(), e);
        } catch (IllegalAccessException e) {
            assert false : "Unreachable code";
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new XBException("Exception while trying to invoke constructor " + constructor.toGenericString(), e.getCause());
        }
    }

    @Override
    public T getDefaultValue(final String value) {
        return convert(value);
    }

}
