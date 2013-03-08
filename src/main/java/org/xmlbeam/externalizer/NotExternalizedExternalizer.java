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
package org.xmlbeam.externalizer;

import java.lang.reflect.Method;

/**
 * Default strategy to externalize projection metadata. The metadata defined in
 * XB-annotations is used directly.
 */
final public class NotExternalizedExternalizer implements Externalizer {

    private static final long serialVersionUID = -3614849117281620124L;

    /**
     * This class should never get a state. 
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolveXPath(String key, Method method, Object[] args) {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolveURL(String key, Method method, Object[] args) {
        return key;
    }

}
