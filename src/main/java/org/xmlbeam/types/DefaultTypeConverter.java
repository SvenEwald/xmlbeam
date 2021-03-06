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

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xmlbeam.types.DefaultTypeConverter.Conversion;
import org.xmlbeam.util.intern.ReflectionHelper;
import static org.xmlbeam.util.intern.ReflectionHelper.array;
/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
@SuppressWarnings({ "serial", "javadoc" })
public class DefaultTypeConverter implements TypeConverter, StringRenderer {

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
        public T convertWithPattern(final String data, final String pattern) {
            throw new IllegalArgumentException("Can not convert type " + getClass().getSimpleName() + " with format pattern.");
        }

    }

    private final Map<Class<?>, Conversion<?>> CONVERSIONS = new HashMap<Class<?>, Conversion<?>>();
    private Locale locale;
    private TimeZone timezone;
    private DecimalFormat decimalFormat;

    public DefaultTypeConverter(final Locale locale, final TimeZone timezone) {
        setLocale(locale);
        setTimeZone(timezone);

        CONVERSIONS.put(Boolean.class, new Conversion<Boolean>(null) {
            @Override
            public Boolean convert(final String data) {
                return Boolean.valueOf(data.trim());
            }
        });
        CONVERSIONS.put(Boolean.TYPE, new Conversion<Boolean>(false) {
            @Override
            public Boolean convert(final String data) {
                return Boolean.valueOf(data.trim());
            }
        });
        CONVERSIONS.put(Byte.class, new Conversion<Byte>(null) {
            @Override
            public Byte convert(final String data) {
                return Byte.valueOf(data.trim());
            }

            @Override
            public Byte convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).byteValue();
            }
        });
        CONVERSIONS.put(Byte.TYPE, new Conversion<Byte>((byte) 0) {
            @Override
            public Byte convert(final String data) {
                return Byte.valueOf(data.trim());
            }

            @Override
            public Byte convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).byteValue();
            }
        });
        CONVERSIONS.put(Float.class, new Conversion<Float>(null) {
            @Override
            public Float convert(final String data) {
                return Float.valueOf(data.trim());
            }

            @Override
            public Float convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).floatValue();
            }
        });
        CONVERSIONS.put(Float.TYPE, new Conversion<Float>(0F) {
            @Override
            public Float convert(final String data) {
                return Float.valueOf(data.trim());
            }

            @Override
            public Float convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).floatValue();
            }
        });
        CONVERSIONS.put(Double.class, new Conversion<Double>(null) {
            @Override
            public Double convert(final String data) {
                return Double.valueOf(data.trim());
            }

            @Override
            public Double convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).doubleValue();
            }
        });
        CONVERSIONS.put(Double.TYPE, new Conversion<Double>(0D) {
            @Override
            public Double convert(final String data) {
                return Double.valueOf(data.trim());
            }

            @Override
            public Double convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).doubleValue();
            }
        });
        CONVERSIONS.put(Short.class, new Conversion<Short>(null) {
            @Override
            public Short convert(final String data) {
                return Short.valueOf(data.trim());
            }

            @Override
            public Short convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).shortValue();
            }
        });
        CONVERSIONS.put(Short.TYPE, new Conversion<Short>((short) 0) {
            @Override
            public Short convert(final String data) {
                return Short.valueOf(data.trim());
            }

            @Override
            public Short convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).shortValue();
            }
        });
        CONVERSIONS.put(Integer.class, new Conversion<Integer>(null) {
            @Override
            public Integer convert(final String data) {
                return Integer.valueOf(data.trim());
            }

            @Override
            public Integer convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).intValue();
            }
        });
        CONVERSIONS.put(Integer.TYPE, new Conversion<Integer>(0) {
            @Override
            public Integer convert(final String data) {
                return Integer.valueOf(data.trim());
            }

            @Override
            public Integer convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).intValue();
            }
        });
        CONVERSIONS.put(Long.class, new Conversion<Long>(null) {
            @Override
            public Long convert(final String data) {
                return Long.valueOf(data.trim());
            }

            @Override
            public Long convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).longValue();
            }
        });
        CONVERSIONS.put(Long.TYPE, new Conversion<Long>(0L) {
            @Override
            public Long convert(final String data) {
                return Long.valueOf(data.trim());
            }

            @Override
            public Long convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern).longValue();
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
            public Date convertWithPattern(final String data, final String pattern) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, DateFormatSymbols.getInstance(locale));
                dateFormat.setTimeZone(timezone);
                try {
                    return dateFormat.parse(data);
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        if (ReflectionHelper.LOCAL_DATE_CLASS != null) {
            CONVERSIONS.put(ReflectionHelper.LOCAL_DATE_CLASS, new Conversion<Object>(null) {
                /**
                 * @param data
                 * @return LocalDateTime
                 * @see org.xmlbeam.types.DefaultTypeConverter.Conversion#convert(java.lang.String)
                 */
                @Override
                public Object convert(String data) {
                    return ReflectionHelper.invokeMethod(null, ReflectionHelper.LOCAL_DATE_CLASS, "parse", array(CharSequence.class),array(data));

                }

                public Object convertWithPattern(final String data, final String pattern) {
                    Object formatter = ReflectionHelper.invokeMethod(null, ReflectionHelper.LOCAL_DATE_TIME_FORMATTER_CLASS, "ofPattern", array(String.class),array(pattern));
                    return ReflectionHelper.invokeMethod(null, ReflectionHelper.LOCAL_DATE_CLASS, "parse",array(CharSequence.class,ReflectionHelper.LOCAL_DATE_TIME_FORMATTER_CLASS),array(data, formatter));
                }

            });

            CONVERSIONS.put(ReflectionHelper.LOCAL_DATE_TIME_CLASS, new Conversion<Object>(null) {

                @Override
                public Object convert(String data) {
                    return ReflectionHelper.invokeMethod(null, ReflectionHelper.LOCAL_DATE_TIME_CLASS, "parse",array(CharSequence.class),array(data) );
                }

                public Object convertWithPattern(final String data, final String pattern) {
                    Object formatter = ReflectionHelper.invokeMethod(null, ReflectionHelper.LOCAL_DATE_TIME_FORMATTER_CLASS, "ofPattern", array(String.class,Locale.class),array(pattern,Locale.getDefault()));
                    return ReflectionHelper.invokeMethod(null, ReflectionHelper.LOCAL_DATE_TIME_CLASS, "parse", array(CharSequence.class,ReflectionHelper.LOCAL_DATE_TIME_FORMATTER_CLASS),array(data, formatter));
                }

            });
        }

        CONVERSIONS.put(BigDecimal.class, new Conversion<BigDecimal>(null) {
            @Override
            public BigDecimal convert(final String data) {
                return new BigDecimal(data);
                //throw new IllegalArgumentException("Conversion to type BigDecimal needs a format pattern.");
            }

            @Override
            public BigDecimal convertWithPattern(final String data, final String pattern) {
                DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(locale);
                decimalFormat.setParseBigDecimal(true);
                try {
                    return (BigDecimal) decimalFormat.parseObject(data);
                } catch (ParseException e) {
                    NumberFormatException exception = new NumberFormatException(data);
                    exception.initCause(e);
                    throw exception;
                }
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
            public Number convertWithPattern(final String data, final String pattern) {
                return parseWithPattern(data, pattern);
            }
        });

    }

    /**
     * @param pattern
     * @return
     */
    protected Number parseWithPattern(final String data, final String pattern) {
        decimalFormat.applyPattern(pattern);
        try {
            return decimalFormat.parse(data);
        } catch (ParseException e) {
            throw new IllegalArgumentException("can not parse '" + data + "' with pattern '" + pattern + "'", e);
        }
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
            return (T) conversion.convertWithPattern(data, optionalFormatPattern[0]);
        }
        return (T) conversion.convert(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean isConvertable(final Class<T> targetType) {
        if (targetType == null) {
            return false; // That is really bad.
        }
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

//    Method may be obsolete
//    @SuppressWarnings("unchecked")
//    public <T> Conversion<T> getConversionForType(final Class<T> type) {
//        assert type != null;
//        return (Conversion<T>) CONVERSIONS.get(type);
//    }

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
        if (locale == null) {
            throw new IllegalArgumentException("You must provide a Locale, not null");
        }
        final NumberFormat format = NumberFormat.getInstance(locale);
        if (!(format instanceof DecimalFormat)) {
            throw new IllegalArgumentException("Can not find a DecimalFormat for locale " + locale);
        }
        this.decimalFormat = (DecimalFormat) format;
        this.locale = locale;
        return this;
    }

    public DefaultTypeConverter setTimeZone(final TimeZone timezone) {
        if (timezone == null) {
            throw new IllegalArgumentException("You must provide a timezone, not null");
        }
        this.timezone = timezone;
        return this;
    }

    @Override
    public <T> String render(final Class<? extends T> dataType, final T data, final String... optionalFormatPattern) {
        assert dataType != null;
        if ((optionalFormatPattern == null) || (optionalFormatPattern.length == 0) || (optionalFormatPattern[0] == null)) {
            return data == null ? null : data.toString();
        }
        if (Date.class.isAssignableFrom(dataType)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(optionalFormatPattern[0], DateFormatSymbols.getInstance(locale));
            dateFormat.setTimeZone(timezone);
            return dateFormat.format(data);
        }
        if (isNumber(dataType)) {
            DecimalFormat clone = (DecimalFormat) decimalFormat.clone();
            clone.applyPattern(optionalFormatPattern[0]);
            return clone.format(data);
        }
        if ("java.time.LocalDate".equals(dataType.getCanonicalName())) {
            Object formatter = ReflectionHelper.invokeMethod(null, ReflectionHelper.LOCAL_DATE_TIME_FORMATTER_CLASS, "ofPattern",array(String.class),array( optionalFormatPattern[0]));
            return (String) ReflectionHelper.invokeMethod(data, ReflectionHelper.LOCAL_DATE_CLASS, "format",array(ReflectionHelper.LOCAL_DATE_TIME_FORMATTER_CLASS),array( formatter));
        }
        if ("java.time.LocalDateTime".equals(dataType.getCanonicalName())) {
            Object formatter = ReflectionHelper.invokeMethod(null, ReflectionHelper.LOCAL_DATE_TIME_FORMATTER_CLASS, "ofPattern", array(String.class),array(optionalFormatPattern[0]));
            return (String) ReflectionHelper.invokeMethod(data, ReflectionHelper.LOCAL_DATE_TIME_CLASS, "format",array(ReflectionHelper.LOCAL_DATE_TIME_FORMATTER_CLASS),array( formatter));
        }
        throw new IllegalArgumentException("Type " + data.getClass().getSimpleName() + " can not be formatted using a pattern");
    }

    /**
     * @param dataType
     * @return
     */
    private boolean isNumber(final Class<?> dataType) {
        if (Number.class.isAssignableFrom(dataType)) {
            return true;
        }
        if (Integer.TYPE.equals(dataType)) {
            return true;
        }
        if (Long.TYPE.equals(dataType)) {
            return true;
        }
        if (Short.TYPE.equals(dataType)) {
            return true;
        }
        if (Float.TYPE.equals(dataType)) {
            return true;
        }
        if (Double.TYPE.equals(dataType)) {
            return true;
        }
        return false;
    }
}
