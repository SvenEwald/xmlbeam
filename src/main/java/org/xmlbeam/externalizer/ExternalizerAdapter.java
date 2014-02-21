/**
 *  Copyright 2014 Sven Ewald
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
package org.xmlbeam.externalizer;

import java.lang.reflect.Method;

/**
 * This default implementation for the {@link Externalizer} interface is just a shortcut
 * if you only want to implement one of the interfaces methods. Feel free to extend
 * this class and implement your own logic.
 *  
 */
public class ExternalizerAdapter implements Externalizer {

    private static final long serialVersionUID = 352472023631762615L;

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolveXPath(String annotationValue, Method method, Object[] args) {
        return annotationValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolveURL(String annotationValue, Method method, Object[] args) {
        return annotationValue;
    }

}
