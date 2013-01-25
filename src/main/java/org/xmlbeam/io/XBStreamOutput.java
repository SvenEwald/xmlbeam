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

import java.io.OutputStream;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.XBProjector;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class XBStreamOutput {

    private final XBProjector projector;
    private final  OutputStream os;

    /**
     * @param xmlProjector
     */
    public XBStreamOutput(XBProjector xmlProjector,OutputStream os) {
        this.projector = xmlProjector;
        this.os=os;
    }

    /**
     * @param projection
     * @param os
     */
    public void write(Object projection ) {        
        try {
            projector.config().createTransformer().transform(new DOMSource(((DOMAccess)projection).getDOMNode()), new StreamResult(os));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

}
