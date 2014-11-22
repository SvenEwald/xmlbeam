package org.xmlbeam.types;

import java.io.Serializable;

/**
 * A StringRenderer is used to render values in XML. Optionally a pattern may be used. Default is
 * calling toString().
 *
 * @author sven
 */
public interface StringRenderer extends Serializable {
    /**
     * @param dataType
     * @param data
     * @param optionalFormatPattern
     * @return a string representation of the input data.
     */
    <T> String render(Class<? extends T> dataType, T data, String... optionalFormatPattern);
}
