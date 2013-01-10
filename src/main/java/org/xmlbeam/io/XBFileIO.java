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
    boolean append = false;
    private final  File file;

    /**
     * @param xmlProjector
     * @param file 
     */
    public XBFileIO(XBProjector xmlProjector, File file) {
        this.projector = xmlProjector;
        this.file=file;
    }

    public <T> T read(Class<T> projectionInterface) throws IOException {
        try {
            Document document = projector.config().getDocumentBuilder().parse(file);
            return projector.projectDOMNode(document, projectionInterface);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param projection
     * @param file
     * @throws IOException
     */
    public XBFileIO write(Object projection) throws IOException {
        FileOutputStream os = new FileOutputStream(file, append);
        new XBStreamOutput(projector,os).write(projection);
        return this;
    }

    public XBFileIO setAppend(boolean append) {
        this.append = append;
        return this;
    }

}
