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
package org.xmlbeam.io;

import java.io.IOException;

import org.xmlbeam.evaluation.XPathBinder;
import org.xmlbeam.evaluation.XPathEvaluator;

/**
 * 
 *
 */
public interface FileIO {

    /**
     * Read a XML document and return a projection to it.
     *
     * @param projectionInterface
     * @return a new projection pointing to the content of the file.
     * @throws IOException
     */
    <T> T read(Class<T> projectionInterface) throws IOException;

    /**
     * @param projection
     * @throws IOException
     * @return this to provide a fluent API.
     */
    FileIO write(Object projection) throws IOException;

    /**
     * Set whether output should be append to existing file. When this method is not invoked, or
     * invoked with 'false', The file will be replaced on writing operations.
     *
     * @param append
     *            optional parameter, default is true.
     * @return this to provide a fluent API.
     */
    FileIO setAppend(boolean... append);

    /**
     * @param xpath
     * @return evaluator
     * @see org.xmlbeam.evaluation.CanEvaluate#evalXPath(java.lang.String)
     */
    XPathEvaluator evalXPath(String xpath);

    /**
     * @param xpath
     * @return binder
     */
    XPathBinder bindXPath(String xpath);

}