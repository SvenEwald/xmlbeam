/**
 *  Copyright 2017 Sven Ewald
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

import java.util.Map;

/**
 * Instances of this type will be bound to an element in the dom tree.
 * Read and write operations on this map will directly be mapped to the xml document.
 * 
 * @author sven
 * @param <T> Map component type. Key is always String containing a relative xpath.
 */
public interface XBAutoMap<T> extends Map<String, T> {

    /**
     * Removes all elements below the element this map is bound to.
     * @see java.util.AbstractMap#clear()
     */
    void clear();
    
    /**
     * @deprecated Please use stronger typed XBAutoMap#get(CharSequence) instead.
     * @see java.util.Map#get(java.lang.Object)
     */
    @Deprecated
    T get(final Object path);
    
    /**
     * Resolve given xpath and return the result. 
     * @param path xpath relative to the bound element.
     * @return value at the position of given xpath, or null if no such value exists
     */
    T get(final CharSequence path);
    
    
    /**
     * @deprecated Please use stronger typed XBAutoMap#get(CharSequence) instead.
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Deprecated
    boolean containsKey(Object path);
    
    /**
     * Checks existence of value at given xpath.
     * 
     * @param path
     * @return true if non null value exists at given path
     */
    boolean containsKey(CharSequence path);
    
    /**
     * Just like java.util.Map#containsValue(java.lang.Object).
     * Notice that this map can not hold null values.
     * @param value
     * @return true if there is an element or attribute with a value equals to the given value.
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    boolean containsValue(Object value);
    
    /**
     * Sets the value at the given xpath to a new value.
     * @param path xpath relative to bound element
     * @param value new value to be set
     * @return previous value or null if there was none.
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    T put(final String path, final T value);
}
