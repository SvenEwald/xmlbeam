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
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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

        /**
         * @param data
         * @return converted object
         */
        public T convertWithPattern(final String data, final String pattern) throws ParseException {
            throw new IllegalArgumentException("Can not convert type " + getClass().getSimpleName() + " with format pattern.");
        }

    }

    private final Map<Class<?>, Conversion<?>> CONVERSIONS = new HashMap<Class<?>, Conversion<?>>();
    private Locale locale;
    private TimeZone timezone;

    public DefaultTypeConverter(final Locale locale, final TimeZone timezone) {
        this.locale = locale;
        this.timezone = timezone;

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

            @Override
            public Byte convertWithPattern(final String data, final String pattern) throws ParseException {
                return Byte.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).byteValue());
            }
        });
        CONVERSIONS.put(Byte.TYPE, new Conversion<Byte>((byte) 0) {
            @Override
            public Byte convert(final String data) {
                return Byte.valueOf(data);
            }

            @Override
            public Byte convertWithPattern(final String data, final String pattern) throws ParseException {
                return Byte.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).byteValue());
            }
        });
        CONVERSIONS.put(Float.class, new Conversion<Float>(null) {
            @Override
            public Float convert(final String data) {
                return Float.valueOf(data);
            }

            @Override
            public Float convertWithPattern(final String data, final String pattern) throws ParseException {
                return Float.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).floatValue());
            }
        });
        CONVERSIONS.put(Float.TYPE, new Conversion<Float>(0F) {
            @Override
            public Float convert(final String data) {
                return Float.valueOf(data);
            }

            @Override
            public Float convertWithPattern(final String data, final String pattern) throws ParseException {
                return Float.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).floatValue());
            }
        });
        CONVERSIONS.put(Double.class, new Conversion<Double>(null) {
            @Override
            public Double convert(final String data) {
                return Double.valueOf(data);
            }

            @Override
            public Double convertWithPattern(final String data, final String pattern) throws ParseException {
                return Double.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).doubleValue());
            }
        });
        CONVERSIONS.put(Double.TYPE, new Conversion<Double>(0D) {
            @Override
            public Double convert(final String data) {
                return Double.valueOf(data);
            }

            @Override
            public Double convertWithPattern(final String data, final String pattern) throws ParseException {
                return Double.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).doubleValue());
            }
        });
        CONVERSIONS.put(Short.class, new Conversion<Short>(null) {
            @Override
            public Short convert(final String data) {
                return Short.valueOf(data);
            }

            @Override
            public Short convertWithPattern(final String data, final String pattern) throws ParseException {
                return Short.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).shortValue());
            }
        });
        CONVERSIONS.put(Short.TYPE, new Conversion<Short>((short) 0) {
            @Override
            public Short convert(final String data) {
                return Short.valueOf(data);
            }

            @Override
            public Short convertWithPattern(final String data, final String pattern) throws ParseException {
                return Short.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).shortValue());
            }
        });
        CONVERSIONS.put(Integer.class, new Conversion<Integer>(null) {
            @Override
            public Integer convert(final String data) {
                return Integer.valueOf(data);
            }

            @Override
            public Integer convertWithPattern(final String data, final String pattern) throws ParseException {
                return Integer.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).intValue());
            }
        });
        CONVERSIONS.put(Integer.TYPE, new Conversion<Integer>(0) {
            @Override
            public Integer convert(final String data) {
                return Integer.valueOf(data);
            }

            @Override
            public Integer convertWithPattern(final String data, final String pattern) throws ParseException {
                return Integer.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).intValue());
            }
        });
        CONVERSIONS.put(Long.class, new Conversion<Long>(null) {
            @Override
            public Long convert(final String data) {
                return Long.valueOf(data);
            }

            @Override
            public Long convertWithPattern(final String data, final String pattern) throws ParseException {
                return Long.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).longValue());
            }
        });
        CONVERSIONS.put(Long.TYPE, new Conversion<Long>(0L) {
            @Override
            public Long convert(final String data) {
                return Long.valueOf(data);
            }

            @Override
            public Long convertWithPattern(final String data, final String pattern) throws ParseException {
                return Long.valueOf(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale)).parse(data).longValue());
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

        CONVERSIONS.put(Date.class, new Conversion<Date>(null) {

            @Override
            public Date convert(final String data) {
                try {
                    return DateFormat.getTimeInstance(DateFormat.SHORT, locale).parse(data);
                } catch (ParseException e) {
                    NumberFormatException exception = new NumberFormatException(data);
                    exception.initCause(e);
                    throw exception;
                }
            }

            @Override
            public Date convertWithPattern(final String data, final String pattern) throws ParseException {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, DateFormatSymbols.getInstance(locale));
                dateFormat.setTimeZone(timezone);
                return dateFormat.parse(data);
            }
        });

        CONVERSIONS.put(BigDecimal.class, new Conversion<BigDecimal>(null) {
            @Override
            public BigDecimal convert(final String data) {
                return new BigDecimal(data);
                //throw new IllegalArgumentException("Conversion to type BigDecimal needs a format pattern.");
            }

            @Override
            public BigDecimal convertWithPattern(final String data, final String pattern) throws ParseException {
                DecimalFormat decimalFormat = new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale));
                decimalFormat.setParseBigDecimal(true);
                return (BigDecimal) decimalFormat.parseObject(data);
            }

        });

        CONVERSIONS.put(Number.class, new Conversion<Number>(null) {
            @Override
            public Number convert(final String data) {
                try {
                    return NumberFormat.getInstance(locale).parse(data);
                } catch (ParseException e) {
                    NumberFormatException exception = new NumberFormatException(data);
                    exception.initCause(e);
                    throw exception;
                }
            }

            @Override
            public Number convertWithPattern(final String data, final String pattern) throws ParseException {
                DecimalFormat decimalFormat = new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale));
                return decimalFormat.parse(data);
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertTo(final Class<T> targetType, final String data, final String... optionalFormatPattern) {
        Conversion<?> conversion = CONVERSIONS.get(targetType);
        assert conversion != null : "Method caller must check existence of conversion. (" + targetType.getName() + ")";
        if (data == null) {
            return (T) conversion.getDefaultValue(data);
        }

        if ((optionalFormatPattern != null) && (optionalFormatPattern.length > 0) && (optionalFormatPattern[0] != null)) {
            try {
                return (T) conversion.convertWithPattern(data, optionalFormatPattern[0]);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Can not parse pattern '" + optionalFormatPattern[0] + "'", e);
            }
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

    public DefaultTypeConverter setLocale(final Locale locale) {
        this.locale = locale;
        return this;
    }

    public DefaultTypeConverter setTimeZone(final TimeZone timezone) {
        this.timezone = timezone;
        return this;
    }
}
