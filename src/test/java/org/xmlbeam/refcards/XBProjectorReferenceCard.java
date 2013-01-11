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
package org.xmlbeam.refcards;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.junit.Ignore;
import org.w3c.dom.Node;
import org.xmlbeam.XBProjector;
import org.xmlbeam.util.IOHelper;

/**
 * 
 */
public class XBProjectorReferenceCard {

    @Ignore
    // This must compile, but it won't run
    public void ensureFileReading() throws Exception {

        File file = null;
        Class<Object> projectionInterface = null;
        Object projection = null;
        InputStream is = null;
        OutputStream os = null;
        String url=null,httpurl = null;
        Object[] replacements = null;
        Map<String, String> props = null;
        String name = null;
        Node node = null;
        String systemID = null;
        
//START SNIPPET: XBProjectorReferenceCard1      
new XBProjector().projectEmptyDocument(projectionInterface);

new XBProjector().projectEmptyElement(name, projectionInterface);
//END SNIPPET: XBProjectorReferenceCard1

//START SNIPPET: XBProjectorReferenceCard2
new XBProjector().projectXMLString("<xml/>", projectionInterface);
//END SNIPPET: XBProjectorReferenceCard2

//START SNIPPET: XBProjectorReferenceCard3
new XBProjector().projectDOMNode(node, projectionInterface);
//END SNIPPET: XBProjectorReferenceCard3

//START SNIPPET: XBProjectorReferenceCard4
new XBProjector().io().file(file).read(projectionInterface);

new XBProjector().io().file(file).write(projection);

new XBProjector().io().file(file).setAppend(true).write(projection);
//END SNIPPET: XBProjectorReferenceCard4

//START SNIPPET: XBProjectorReferenceCard5
new XBProjector().io().fromURLAnnotation(projectionInterface, replacements);

new XBProjector().io().toURLAnnotationViaPOST(projectionInterface, replacements);
//END SNIPPET: XBProjectorReferenceCard5

//START SNIPPET: XBProjectorReferenceCard6
new XBProjector().io().url(url).read(projectionInterface);

new XBProjector().io().url(url).write(projectionInterface);
//END SNIPPET: XBProjectorReferenceCard6

//START SNIPPET: XBProjectorReferenceCard7
new XBProjector().io().url(httpurl).addRequestProperty("key", "value").read(projectionInterface);

new XBProjector().io().url(httpurl).addRequestProperties(props).read(projectionInterface);
//END SNIPPET: XBProjectorReferenceCard7

//START SNIPPET: XBProjectorReferenceCard8
Map<String, String> credentials = IOHelper.createBasicAuthenticationProperty("user","pwd");

new XBProjector().io().url(httpurl).addRequestProperties(credentials).write(projectionInterface);
//END SNIPPET: XBProjectorReferenceCard8

//START SNIPPET: XBProjectorReferenceCard9
new XBProjector().io().stream(is).read(projectionInterface);

new XBProjector().io().stream(is).setSystemID(systemID).read(projectionInterface);

new XBProjector().io().stream(os).write(projection);
//END SNIPPET: XBProjectorReferenceCard9
    }

}
