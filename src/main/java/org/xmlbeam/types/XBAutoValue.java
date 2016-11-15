/**
 *  Copyright 2016 Sven Ewald
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

import java.util.Iterator;

/**
 * Instances of this type provide direct access to the DOM. A value may be present, or not. Changes
 * are directly applied to the DOM. This value implements Iterable to provide a convenient way to
 * handle nonexisting values like this:
 * <pre>
 * XBAutoValue<String> autoValue;
 * for (String string : autoValue) {     
 *     System.out.println(string);
 * } </pre>  instead of <pre>
 * XBAutoValue<String> autoValue;
 * if (autoValue.isPresent) {
 *    String string = autoValue.get();
 *    System.out.println(string);
 * }
 * </pre>
 * 
 * @param <E>
 *            Any type XMLBeam can work with.
 *            
 * @author sven           
 */
public interface XBAutoValue<E> extends Iterable<E> {

    /**
     * Getter for value of bound element.
     * 
     * @return value
     */
    E get();

    /**
     * Setter for value of bound
     * 
     * @param value
     * @return previous value
     */
    E set(E value);

    /**
     * Deletes value
     * 
     * @return removed value
     */
    E remove();

    /**
     * @return true if value exists
     */
    boolean isPresent();

    /**
     * @return iterator for this value
     * @see java.lang.Iterable#iterator()
     */
    Iterator<E> iterator();

    /**
     * @param string
     * @return this for convenience
     */
    XBAutoValue<E> rename(String string);

    /**
     * Getter for name of element or attribute this value is bound to
     * 
     * @return name of element or attribute
     */
    String getName();
}
