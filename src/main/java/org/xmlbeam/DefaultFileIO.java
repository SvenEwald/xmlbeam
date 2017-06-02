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
package org.xmlbeam;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlbeam.evaluation.CanEvaluate;
import org.xmlbeam.evaluation.DefaultXPathEvaluator;
import org.xmlbeam.evaluation.DocumentResolver;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.evaluation.XPathBinder;
import org.xmlbeam.evaluation.XPathEvaluator;
import org.xmlbeam.exceptions.XBException;
import org.xmlbeam.io.FileIO;
import org.xmlbeam.io.StreamOutput;
import org.xmlbeam.types.CloseableMap;
import org.xmlbeam.types.XBAutoMap;
import org.xmlbeam.util.IOHelper;
import org.xmlbeam.util.intern.DOMHelper;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
class DefaultFileIO implements CanEvaluate, FileIO {

    private final XBProjector projector;
    private boolean append = false;
    private boolean failIfNotExists = false;
    private final File file;

    /**
     * Constructor for file input.
     *
     * @param xmlProjector
     * @param file
     */
    public DefaultFileIO(final XBProjector xmlProjector, final File file) {
        if (xmlProjector == null) {
            throw new NullPointerException("Parameter xmlProjector must not be null.");
        }
        if (file == null) {
            throw new NullPointerException("Parameter file must not be null.");
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException("File " + file + " is a directory.");
        }
        this.projector = xmlProjector;
        this.file = file;
    }

    /**
     * Convenient constructor using a filename.
     *
     * @param xmlProjector
     * @param fileName
     */
    public DefaultFileIO(final XBProjector xmlProjector, final String fileName) {
        this(xmlProjector, new File(fileName));
    }

    /**
     * Read a XML document and return a projection to it.
     *
     * @param projectionInterface
     * @return a new projection pointing to the content of the file.
     * @throws IOException
     */
    @Override
    public <T> T read(final Class<T> projectionInterface) throws IOException {
        try {
            Document document = projector.config().createDocumentBuilder().parse(file);
            return projector.projectDOMNode(document, projectionInterface);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param projection
     * @throws IOException
     */
    @Override
    public
    void write(final Object projection) throws IOException {
        FileOutputStream os = new FileOutputStream(file, append);
        new StreamOutput(projector, os).write(projection);
        os.close();
    }

    /**
     * Set whether output should be append to existing file. When this method is not invoked, or
     * invoked with 'false', The file will be replaced on writing operations.
     *
     * @param append
     *            optional parameter, default is true.
     * @return this to provide a fluent API.
     */
    @Override
    public FileIO setAppend(final boolean... append) {
        this.append = (append != null) && ((append.length == 0) || ((append.length > 0) && append[0]));
        return this;
    }

    /**
     * Set whether file should be created if it does not exist. When this method is not invoked, *
     * bind operations will fail if the file does not exist.
     *
     * @return this to provide a fluent API.
     */
    @Override
    public FileIO failIfNotExists(final boolean... failOnFNF) {
        this.failIfNotExists = (failOnFNF != null) && ((failOnFNF.length == 0) || ((failOnFNF.length > 0) && failOnFNF[0]));
        return this;
    }

    /**
     * @param xpath
     * @return evaluator
     * @see org.xmlbeam.evaluation.CanEvaluate#evalXPath(java.lang.String)
     */
    @Override
    public XPathEvaluator evalXPath(final String xpath) {
        return new DefaultXPathEvaluator(projector, new DocumentResolver() {

            @Override
            public Document resolve(final Class<?>... resourceAwareClass) throws IOException {
                FileInputStream fileInputStream = new FileInputStream(file);
                Document doc = IOHelper.loadDocument(projector, fileInputStream);
                fileInputStream.close();
                return doc;
            }

        }, xpath);
    }

    /**
     * @param xpath
     * @return binder
     */
    @Override
    @SuppressWarnings("resource")
    public XPathBinder bindXPath(final String xpath) {
        final Document[] doc = new Document[1];
        return new DefaultXPathBinder(projector, new DocumentResolver() {

            @Override
            public Document resolve(final Class<?>... resourceAwareClass) throws IOException {
                //Document doc;
                if (file.isFile()) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    doc[0] = IOHelper.loadDocument(projector, fileInputStream);
                    fileInputStream.close();
                } else {
                    if (failIfNotExists) {
                        throw new FileNotFoundException(file.getAbsolutePath());
                    }
                    doc[0] = projector.config().createDocumentBuilder().newDocument();
                }

                return doc[0];
            }

        }, xpath, new Closeable() {

            @Override
            public void close() throws IOException {
                try {
                    FileOutputStream fileOutPutStream = new FileOutputStream(file);
                    DOMHelper.trim(doc[0]);
                    projector.config().createTransformer().transform(new DOMSource(doc[0]), new StreamResult(fileOutPutStream));
                    fileOutPutStream.flush();
                    fileOutPutStream.close();
                } catch (TransformerException e) {
                    throw new XBException("Could not write to file " + file.getAbsolutePath(), e);
                }
            }
        });
    }

    /**
     * @param valueType
     * @return Map bound to file
     * @throws FileNotFoundException
     * @see org.xmlbeam.io.FileIO#bindAsMapOf(java.lang.Class)
     */
    @SuppressWarnings("resource")
    @Override
    public <T> CloseableMap<T> bindAsMapOf(final Class<T> valueType) throws IOException {
        DefaultXPathBinder.validateEvaluationType(valueType);
        if ((failIfNotExists) && (!file.exists())) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        final Document[] document = new Document[1];
        try {
            if (file.exists()) {
                document[0] = projector.config().createDocumentBuilder().parse(file);
            } else {
                document[0] = projector.config().createDocumentBuilder().newDocument();
            }
            InvocationContext invocationContext = new InvocationContext(null, null, null, null, null, valueType, projector);
            return new DefaultFileMap<T>(document[0], invocationContext, new Closeable() {
                final Document doc = document[0];

                @Override
                public void close() throws IOException {
                    try {
                        FileOutputStream fileOutPutStream = new FileOutputStream(file);
                        DOMHelper.trim(doc);
                        projector.config().createTransformer().transform(new DOMSource(doc), new StreamResult(fileOutPutStream));
                        fileOutPutStream.flush();
                        fileOutPutStream.close();
                    } catch (TransformerException e) {
                        throw new XBException("Could not write to file " + file.getAbsolutePath(), e);
                    }
                }
            }, valueType);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param valueType
     * @return XBAutoMap for the complete document
     * @throws IOException
     * @see org.xmlbeam.io.FileIO#asMapOf(java.lang.Class)
     */
    @Override
    public <T> XBAutoMap<T> asMapOf(final Class<T> valueType) throws IOException {
        DefaultXPathBinder.validateEvaluationType(valueType);
        try {
            Document document = projector.config().createDocumentBuilder().parse(file);
            InvocationContext invocationContext = new InvocationContext(null, null, null, null, null, valueType, projector);
            return new AutoMap<T>(document, invocationContext, valueType);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
