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

import java.util.Map;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Ignore;
import org.w3c.dom.Node;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.util.IOHelper;

/**
 * 
 */
public class XBProjectorReferenceCard {
    
    //START SNIPPET: mainExample
    public interface Example {
        
        @XBRead("/xml/example/content")
        String getContent();
        
        @XBRead("/xml/example/content/@type")
        String getType();
        
    }
    //END SNIPPET: mainExample
    
    //START SNIPPET: XBProjectorReferenceCardI     
    public interface Projection {
        // Define your projection methods in a public interface.
    };
    
    @XBDocURL("http://...")
    public interface ProjectionWithSourceDeclaration {
        // Define your projection methods in a public interface.
    }

    
//START SNIPPET: XBProjectorReferenceCard0
XBProjector projector = new XBProjector();       
//END SNIPPET: XBProjectorReferenceCard0

  //END SNIPPET: XBProjectorReferenceCardI
    @SuppressWarnings("unused")
    @Ignore
    // This must compile, but it won't run
    public void ensureFileReading() throws Exception {

        File file = null;
        Class<Object> projectionInterface = null;
        InputStream is = null;
        OutputStream os = null;
        String url = null, httpurl = null;
        Object[] params = null;
        Map<String, String> props = null;
        String name = null;
        Node node = null;
        String systemID = null;


        {
//START SNIPPET: XBProjectorReferenceCard1      
        Projection projection = projector.projectEmptyDocument(Projection.class);

        Projection subProjection = projector.projectEmptyElement(name, Projection.class);
//END SNIPPET: XBProjectorReferenceCard1
        }
        {
//START SNIPPET: XBProjectorReferenceCard2
        Projection projection = projector.projectXMLString("<xml/>", Projection.class);
//END SNIPPET: XBProjectorReferenceCard2

//START SNIPPET: XBProjectorReferenceCard2b
        // Let the projector convert your projection
        String xml = projector.asString(projection);
        
        // Or, configure the projector this way before you create a projection
        XBProjector projector  = new XBProjector(Flags.TO_STRING_RENDERS_XML);
        //... and later call:
        projection.toString();
//END SNIPPET: XBProjectorReferenceCard2b
        }
        {
//START SNIPPET: XBProjectorReferenceCard3
            Projection projection =  projector.projectDOMNode(node, Projection.class);
//END SNIPPET: XBProjectorReferenceCard3
        }
        {
//START SNIPPET: XBProjectorReferenceCard4
            Projection projection = projector.io().file(file).read(Projection.class);

            projector.io().file(file).write(projection);

            projector.io().file(file).setAppend(true).write(projection);
//END SNIPPET: XBProjectorReferenceCard4
        }
        {
//START SNIPPET: XBProjectorReferenceCard5
            Projection projection = projector.io().fromURLAnnotation(Projection.class, params);

            projector.io().toURLAnnotationViaPOST(projectionInterface, projection, params);
//END SNIPPET: XBProjectorReferenceCard5
        }
        {
//START SNIPPET: XBProjectorReferenceCard6
            Projection projection = projector.io().url(url).read(Projection.class);

            projector.io().url(url).write(projection);
//END SNIPPET: XBProjectorReferenceCard6
        }
        {
//START SNIPPET: XBProjectorReferenceCard7
            Projection projection = projector.io().url(httpurl).addRequestProperty("key", "value").read(Projection.class);

            Projection projection2 = projector.io().url(httpurl).addRequestProperties(props).read(Projection.class);
//END SNIPPET: XBProjectorReferenceCard7
        }
        {
            Object projection = null;
//START SNIPPET: XBProjectorReferenceCard8
Map<String, String> credentials = IOHelper.createBasicAuthenticationProperty("user","pwd");

projector.io().url(httpurl).addRequestProperties(credentials).write(projection);
//END SNIPPET: XBProjectorReferenceCard8
        }
        {
//START SNIPPET: XBProjectorReferenceCard9
            Projection projection = projector.io().stream(is).read(Projection.class);

            Projection projectionWithSystemID = projector.io().stream(is).setSystemID(systemID).read(Projection.class);

            projector.io().stream(os).write(projection);
//END SNIPPET: XBProjectorReferenceCard9
        }
    }
}
