package org.xmlbeam.externalizer;

import java.lang.reflect.Method;

import java.io.Serializable;
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

/**
 * This interface may be used to define a external source for projection metadata.
 * By default, all projection metadata is defined via annotations in projection interfaces.
 *  
 */
public interface Externalizer extends Serializable {    

    /**
     * Implement this method to provide a XPath expression for a given method invocation.
     * 
     * @param annotationValue
     * @param method
     * @param args
     * @return
     */
    String resolveXPath(String annotationValue, Method method, Object args[]);

    /**
     * Implement this method to provide an URL for a given method invocation.
     * 
     * @param annotationValue
     * @param method
     * @param args
     * @return
     */
    String resolveURL(String annotationValue, Method method, Object args[]);
}
