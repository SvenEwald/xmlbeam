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
package org.xmlbeam.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlbeam.XBProjector;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class XBFileIO {

    private final XBProjector projector;
    private boolean append = false;
    private final File file;

    /**
     * Constructor for file input.
     *
     * @param xmlProjector
     * @param file
     */
    public XBFileIO(final XBProjector xmlProjector, final File file) {
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
    public XBFileIO(final XBProjector xmlProjector, final String fileName) {
        this(xmlProjector, new File(fileName));
    }

    /**
     * Read a XML document and return a projection to it.
     *
     * @param projectionInterface
     * @return a new projection pointing to the content of the file.
     * @throws IOException
     */
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
     * @return this to provide a fluent API.
     */
    public XBFileIO write(final Object projection) throws IOException {
        FileOutputStream os = new FileOutputStream(file, append);
        new XBStreamOutput(projector, os).write(projection);
        os.close();
        return this;
    }

    /**
     * Set whether output should be append to existing file.
     *
     * @param append
     * @return this to provide a fluent API.
     */
    public XBFileIO setAppend(final boolean append) {
        this.append = append;
        return this;
    }

}
