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

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public interface TypeConverter extends Serializable {

    /**
     * Check if this converter can convert Strings to the given target type.
     *
     * @param targetType
     * @return true if conversion is possible.
     */
    <T> boolean isConvertable(Class<T> targetType);

    /**
     * Convert a String value to the given target type. There is no parameter check. Caller
     * <b>must</b> check by calling {@code isConvertable(...)} before.
     *
     * @param targetType
     * @param data
     * @return a new instance of the target type.
     */
    <T> T convertTo(Class<T> targetType, String data, String... optionalFormatPattern);
}
