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
import org.xmlbeam.types.CloseableMap;
import org.xmlbeam.types.XBAutoMap;
import org.xmlbeam.util.intern.DocScope;
import org.xmlbeam.util.intern.Scope;

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
    @Scope(DocScope.INPUT)
    <T> T read(Class<T> projectionInterface) throws IOException;

    /**
     * @param projection
     * @throws IOException
     */
    @Scope(DocScope.OUTPUT)
    void write(Object projection) throws IOException;

    /**
     * Set whether output should be append to existing file. When this method is not invoked, or
     * invoked with 'false', The file will be replaced on writing operations.
     *
     * @param append
     *            optional parameter, default is true.
     * @return this to provide a fluent API.
     */
    @Scope(DocScope.OUTPUT)
    FileIO setAppend(boolean... append);

    /**
     * @param xpath
     * @return evaluator
     * @see org.xmlbeam.evaluation.CanEvaluate#evalXPath(java.lang.String)
     */
    @Scope(DocScope.INPUT)
    XPathEvaluator evalXPath(String xpath);

    /**
     * Read complete document to a Map. The document must exist.
     *
     * @param valueType
     * @return XBAutoMap map for the complete document.
     * @throws IOException
     */
    @Scope(DocScope.INPUT)
    <T> XBAutoMap<T> readAsMapOf(Class<T> valueType) throws IOException;

    /**
     * Evaluate given XPath and bind result to a List or Map. Use this method to bind parts of
     * documents to a map. If failIfNotExists() was not called before, the file does not need to
     * exist. If it does not exist, it will be created when calling close().
     *
     * @param xpath
     * @return binder
     */
    @Scope(DocScope.IO)
    XPathBinder bindXPath(String xpath);

    /**
     * Bind complete document to a Map. The returned value will be a Closeable map. Calling close()
     * on this map will write back the changes to the file. If failIfNotExists() was not called
     * before, the file does not need to exist. If it does not exist, it will be created when
     * calling close().
     *
     * @param valueType
     * @return Closeable map bound to complete document.
     * @throws IOException
     */
    @Scope(DocScope.IO)
    <T> CloseableMap<T> bindAsMapOf(Class<T> valueType) throws IOException;

    /**
     * Set whether files should be created if they don't exist. When this method is not invoked, or
     * invoked with 'false', a FileNotFound exception will be thrown on bind operations. Calling
     * this method has effect on bind operations only.
     *
     * @return this to provide fluent API.
     */
    @Scope(DocScope.INPUT)
    FileIO failIfNotExists(boolean... create);

}