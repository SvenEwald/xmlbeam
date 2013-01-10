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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlbeam.XBProjector;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class XBStreamInput {

    private final XBProjector projector;
    private final  InputStream is;
    private String systemID;
    
    /**
     * @param xmlProjector
     */
    public XBStreamInput(XBProjector xmlProjector,InputStream is) {
        this.projector = xmlProjector;
        this.is=is;
    }

    /**
     * Create a new projection by parsing the data provided by the input stream.
     * 
     * @param is
     * @param projectionInterface
     *            A Java interface to project the data on.
     * @return
     * @throws IOException
     */
    public <T> T read(final Class<T> projectionInterface) throws IOException {
        try {
            DocumentBuilder documentBuilder = projector.config().getDocumentBuilder();
            Document document = systemID==null ? documentBuilder.parse(is) : documentBuilder.parse(is,systemID);
            return projector.projectDOMNode(document, projectionInterface);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
   
    public XBStreamInput setSystemID(String systemID) {
        this.systemID=systemID;
        return this;
    }
}
