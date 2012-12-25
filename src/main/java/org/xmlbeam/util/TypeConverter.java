/**
 *  Copyright 2012 Sven Ewald
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
package org.xmlbeam.util;

import java.util.HashMap;
import java.util.Map;

public interface TypeConverter<T> {
    @SuppressWarnings("serial")
    final static Map<Class<?>, TypeConverter<?>> CONVERTERS = new HashMap<Class<?>, TypeConverter<?>>() {
        {
            put(Boolean.class, new TypeConverter<Boolean>() {
                @Override
                public Boolean convert(final String data) {
                    return Boolean.valueOf(data);
                }
            });
            put(Boolean.TYPE, get(Boolean.class));
            put(Byte.class, new TypeConverter<Byte>() {
                @Override
                public Byte convert(final String data) {
                    return Byte.valueOf(data);
                }
            });
            put(Byte.TYPE, get(Byte.class));
            put(Float.class, new TypeConverter<Float>() {
                @Override
                public Float convert(final String data) {
                    return Float.valueOf(data);
                }
            });
            put(Float.TYPE, get(Float.class));
            put(Double.class, new TypeConverter<Double>() {
                @Override
                public Double convert(final String data) {
                    return Double.valueOf(data);
                }
            });
            put(Double.TYPE, get(Double.class));
            put(Short.class, new TypeConverter<Short>() {
                @Override
                public Short convert(final String data) {
                    return Short.valueOf(data);
                }
            });
            put(Short.TYPE, get(Short.class));
            put(Integer.class, new TypeConverter<Integer>() {
                @Override
                public Integer convert(final String data) {
                    return Integer.valueOf(data);
                }
            });
            put(Integer.TYPE, get(Integer.class));
            put(Long.class, new TypeConverter<Long>() {
                @Override
                public Long convert(final String data) {
                    return Long.valueOf(data);
                }
            });
            put(Long.TYPE, get(Long.class));
            put(String.class, new TypeConverter<String>() {
                @Override
                public String convert(final String data) {
                    return data;
                }
            });
        }
    };

    T convert(final String data);
}
