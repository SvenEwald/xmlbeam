/**
 *    Copyright 2015 Sven Ewald
 *
 *    This file is part of JSONBeam.
 *
 *    JSONBeam is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, any
 *    later version.
 *
 *    JSONBeam is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with JSONBeam.  If not, see <http://www.gnu.org/licenses/>.
 */
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
