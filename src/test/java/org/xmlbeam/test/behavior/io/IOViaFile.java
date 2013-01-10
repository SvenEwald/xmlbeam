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
package org.xmlbeam.test.behavior.io;

import java.util.Map;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.w3c.dom.Node;
import org.xmlbeam.XBProjector;

/**
 *
 */
public class IOViaFile {

    public void ensureFileReading() throws Exception {

        File file = null;
        Class<Object> projectionInterface = null;
        Object projection = null;
        String a = null, b = null;
        InputStream is = null;
        OutputStream os = null;
        String url = null;
        Object[] replacements = null;
        Map<String, String> params;
        String name=null;
        Node node = null;
        
        String systemID = null;

        new XBProjector().projectEmptyDocument(projectionInterface);
        new XBProjector().projectEmptyElement(name, projectionInterface);

        new XBProjector().projectXMLString("<xml/>", projectionInterface);

        new XBProjector().projectDOMNode(node, projectionInterface);

        new XBProjector().io().file(file).read(projectionInterface);
        new XBProjector().io().file(file).setAppend(true).write(projection);

        new XBProjector().io().fromURLAnnotation(projectionInterface, replacements);
        new XBProjector().io().toURLAnnotationViaPOST(projectionInterface, replacements);

        new XBProjector().io().url(url).read(projectionInterface);
        new XBProjector().io().url(url).addRequestParam(a, b).read(projectionInterface);
        new XBProjector().io().url(url).write(projectionInterface);

        new XBProjector().io().stream(is).read(projectionInterface);
        new XBProjector().io().stream(is).setSystemID(systemID).read(projectionInterface);
        new XBProjector().io().stream(os).write(projection);

    }

}
