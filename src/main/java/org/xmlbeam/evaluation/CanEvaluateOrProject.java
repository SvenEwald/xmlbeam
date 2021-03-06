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
package org.xmlbeam.evaluation;

import org.xmlbeam.types.XBAutoMap;
import org.xmlbeam.util.intern.DocScope;
import org.xmlbeam.util.intern.Scope;

/**
 * @author sven
 */
public interface CanEvaluateOrProject extends CanEvaluate {

    /**
     * @param type
     * @return a projection
     */
    @Scope(DocScope.IO)
    <T> T createProjection(Class<T> type);

    /**
     * @param class1
     * @return a map bound to the XML document element
     */
    @Scope(DocScope.IO)
    <T> XBAutoMap<T> createMapOf(Class<T> class1);
}