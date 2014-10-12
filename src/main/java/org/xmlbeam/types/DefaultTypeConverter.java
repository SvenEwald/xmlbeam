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
package org.xmlbeam.types;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.xmlbeam.util.intern.ReflectionHelper;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
@SuppressWarnings({ "serial", "javadoc" })
public class DefaultTypeConverter implements TypeConverter {

    public static abstract class Conversion<T> implements Serializable {

        private final T defaultValue;

        protected Conversion(final T defaultValue) {
            this.defaultValue = defaultValue;
        }

        public abstract T convert(final String data);

        public T getDefaultValue(final String data) {
            return defaultValue;
        }
    }

    private final Map<Class<?>, Conversion<?>> CONVERSIONS = new HashMap<Class<?>, Conversion<?>>();

    public DefaultTypeConverter() {
        CONVERSIONS.put(Boolean.class, new Conversion<Boolean>(null) {
            @Override
            public Boolean convert(final String data) {
                return Boolean.valueOf(data);
            }
        });
        CONVERSIONS.put(Boolean.TYPE, new Conversion<Boolean>(false) {
            @Override
            public Boolean convert(final String data) {
                return Boolean.valueOf(data);
            }
        });
        CONVERSIONS.put(Byte.class, new Conversion<Byte>(null) {
            @Override
            public Byte convert(final String data) {
                return Byte.valueOf(data);
            }
        });
        CONVERSIONS.put(Byte.TYPE, new Conversion<Byte>((byte) 0) {
            @Override
            public Byte convert(final String data) {
                return Byte.valueOf(data);
            }
        });
        CONVERSIONS.put(Float.class, new Conversion<Float>(null) {
            @Override
            public Float convert(final String data) {
                return Float.valueOf(data);
            }
        });
        CONVERSIONS.put(Float.TYPE, new Conversion<Float>(0F) {
            @Override
            public Float convert(final String data) {
                return Float.valueOf(data);
            }
        });
        CONVERSIONS.put(Double.class, new Conversion<Double>(null) {
            @Override
            public Double convert(final String data) {
                return Double.valueOf(data);
            }
        });
        CONVERSIONS.put(Double.TYPE, new Conversion<Double>(0D) {
            @Override
            public Double convert(final String data) {
                return Double.valueOf(data);
            }
        });
        CONVERSIONS.put(Short.class, new Conversion<Short>(null) {
            @Override
            public Short convert(final String data) {
                return Short.valueOf(data);
            }
        });
        CONVERSIONS.put(Short.TYPE, new Conversion<Short>((short) 0) {
            @Override
            public Short convert(final String data) {
                return Short.valueOf(data);
            }
        });
        CONVERSIONS.put(Integer.class, new Conversion<Integer>(null) {
            @Override
            public Integer convert(final String data) {
                return Integer.valueOf(data);
            }
        });
        CONVERSIONS.put(Integer.TYPE, new Conversion<Integer>(0) {
            @Override
            public Integer convert(final String data) {
                return Integer.valueOf(data);
            }
        });
        CONVERSIONS.put(Long.class, new Conversion<Long>(null) {
            @Override
            public Long convert(final String data) {
                return Long.valueOf(data);
            }
        });
        CONVERSIONS.put(Long.TYPE, new Conversion<Long>(0L) {
            @Override
            public Long convert(final String data) {
                return Long.valueOf(data);
            }
        });
        CONVERSIONS.put(Character.class, new Conversion<Character>(null) {
            @Override
            public Character convert(final String data) {
                if (data.length() == 1) {
                    return data.charAt(0);
                }
                String trimmed = data.trim();
                return trimmed.isEmpty() ? getDefaultValue("") : trimmed.charAt(0);
            }
        });
        CONVERSIONS.put(Character.TYPE, new Conversion<Character>(' ') {
            @Override
            public Character convert(final String data) {
                if (data.length() == 1) {
                    return data.charAt(0);
                }
                String trimmed = data.trim();
                return trimmed.isEmpty() ? getDefaultValue("") : trimmed.charAt(0);
            }
        });

        CONVERSIONS.put(String.class, new Conversion<String>(null) {
            @Override
            public String convert(final String data) {
                return data;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertTo(final Class<T> targetType, final String data) {
        Conversion<?> conversion = CONVERSIONS.get(targetType);
        assert conversion != null : "Method caller must check existence of conversion. (" + targetType.getName() + ")";

        if (data == null) {
            return (T) conversion.getDefaultValue(data);
        }

        return (T) conversion.convert(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean isConvertable(final Class<T> targetType) {
        if (CONVERSIONS.containsKey(targetType)) {
            return true;
        }
        Constructor<T> constructor = ReflectionHelper.getCallableConstructorForParams(targetType, String.class);
        if (constructor != null) {
            CONVERSIONS.put(targetType, new StringConstructorConversion<T>(constructor, null));
            return true;
        }

        Method factory = ReflectionHelper.getCallableFactoryForParams(targetType, String.class);
        if (factory != null) {
            CONVERSIONS.put(targetType, new StringFactoryConversion<T>(factory, null));
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public <T> Conversion<T> getConversionForType(final Class<T> type) {
        assert type != null;
        return (Conversion<T>) CONVERSIONS.get(type);
    }

    public <T> DefaultTypeConverter setConversionForType(final Class<T> type, final Conversion<T> conversion) {
        assert type != null;
        if (conversion == null) {
            CONVERSIONS.remove(type);
            return this;
        }
        CONVERSIONS.put(type, conversion);
        return this;
    }
}
