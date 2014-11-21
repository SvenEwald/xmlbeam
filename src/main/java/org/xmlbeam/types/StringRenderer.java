package org.xmlbeam.types;

import java.io.Serializable;

public interface StringRenderer extends Serializable {
    /**
     * @param dataType
     * @param data
     * @param optionalFormatPattern
     * @return a string representation of the input data.
     */
    <T> String render(Class<? extends T> dataType,T data, String... optionalFormatPattern);
}
