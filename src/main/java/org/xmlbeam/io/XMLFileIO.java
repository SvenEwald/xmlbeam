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
import org.xmlbeam.XMLProjector;

/**
 *
 */
public class XMLFileIO {

    private final XMLProjector projector;
    boolean append = false;

    /**
     * @param xmlProjector
     */
    public XMLFileIO(XMLProjector xmlProjector) {
        this.projector = xmlProjector;
    }

    public <T> T read(File file, Class<T> projectionInterface) throws IOException {
        try {
            Document document = projector.config().getDocumentBuilder().parse(file);
            return projector.projectXML(document, projectionInterface);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param projection
     * @param file
     * @throws IOException
     */
    public XMLFileIO write(Object projection, File file) throws IOException {
        FileOutputStream os = new FileOutputStream(file, append);
        new XMLStreamIO(projector).write(projection, os);
        return this;
    }

    public XMLFileIO setAppend(boolean append) {
        this.append = append;
        return this;
    }

}
