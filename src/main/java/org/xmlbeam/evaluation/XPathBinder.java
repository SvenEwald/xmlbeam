/**
 *  Copyright 2015 Sven Ewald
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
package org.xmlbeam.evaluation;

import java.util.Date;
import java.util.List;

import org.xmlbeam.types.Bound;
import org.xmlbeam.types.BoundList;
import org.xmlbeam.types.Projected;
import org.xmlbeam.types.ProjectedList;

/**
 * Interface to build fluent API for the evaluation API.
 *
 */
public interface XPathBinder {

    /**
     * Evaluates the XPath as a boolean value. This method is just a shortcut for as(Boolean.TYPE);
     * 
     * @return true when the selected value equals (ignoring case) 'true'
     */
    Bound<Boolean> asBoolean();

    /**
     * Evaluates the XPath as a int value. This method is just a shortcut for as(Integer.TYPE);
     * 
     * @return int value of evaluation result.
     */
    Bound<Integer> asInt();

    /**
     * Evaluates the XPath as a String value. This method is just a shortcut for as(String.class);
     * 
     * @return String value of evaluation result.
     */
    Bound<String> asString();

    /**
     * Evaluates the XPath as a Date value. This method is just a shortcut for as(Date.class); You
     * probably want to specify ' using ' followed by some formatting pattern consecutive to the
     * XPAth.
     * 
     * @return Date value of evaluation result.
     */
    Bound<Date> asDate();

    /**
     * Evaluate the XPath as a value of the given type.
     * 
     * @param returnType
     *            Possible values: primitive types (e.g. Short.Type), Projection interfaces, any
     *            class with a String constructor or a String factory method, and org.w3c.Node
     * @return a value of return type that reflects the evaluation result.
     */
    <T> Bound<T> as(Class<T> returnType);

    /**
     * Evaluate the XPath as a list of the given type.
     * 
     * @param componentType
     *            Possible values: primitive types (e.g. Short.Type), Projection interfaces, any
     *            class with a String constructor or a String factory method, and org.w3c.Node
     * @return List of return type that reflects the evaluation result.
     */
    <T> BoundList<T> asListOf(Class<T> componentType);

}