package org.xmlbeam.types;

import java.util.HashMap;
import java.util.Map;

public  class DefaultTypeConverter implements TypeConverter {

    private final Map<Class<?>, Conversion<?>> CONVERSIONS = new HashMap<Class<?>, Conversion<?>>();

    public DefaultTypeConverter() {
        this.CONVERSIONS.putAll(PragmaticConverterFactory.getConversions());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertTo(Class<T> targetType, String data) {
        assert data != null;

        Conversion<?> conversion = CONVERSIONS.get(targetType);
        assert conversion != null : "Method caller must check existence of conversion.";

        if (data.isEmpty()) {
            return (T) conversion.getDefaultValue();
        }

        return (T) conversion.convert(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean isConvertable(Class<T> targetType) {
        return CONVERSIONS.containsKey(targetType);
    }


    /**
     * @return the conversions
     */
    protected Map<Class<?>, Conversion<?>> getConversions() {
        return CONVERSIONS;
    }

}
