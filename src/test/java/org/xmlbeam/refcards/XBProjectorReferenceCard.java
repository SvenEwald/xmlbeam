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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Ignore;
import org.w3c.dom.Node;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.types.CloseableMap;
import org.xmlbeam.types.DefaultTypeConverter;
import org.xmlbeam.types.XBAutoMap;
import org.xmlbeam.util.IOHelper;

/**
 *
 */
@SuppressWarnings("javadoc")
public class XBProjectorReferenceCard {

    //START SNIPPET: mainExample
    public interface Example {

        @XBRead("/xml/example/content")
        String getContent();

        @XBRead("/xml/example/content/@type")
        String getType();

    }
    //END SNIPPET: mainExample

    { try{
        if (false) {
    //START SNIPPET: mainExample2    
{   
    // Read xml file
    CloseableMap<String> map = new XBProjector().io().file("example.xml").bindAsMapOf(String.class);
    
    // Get content via XPath
    String content = map.get("/xml/example/content"); // "bar"
    
    // Create new attribute "type"
    map.put("/xml/example/content/@type", "foo");
    
    // Apply changes to the file
    map.close();
}
    //END SNIPPET: mainExample2
        }
    }catch (IOException e){
        
    }
     }

    
    //START SNIPPET: XBProjectorReferenceCardI
    public interface Projection {
        // Define your projection methods in a public interface.
    };

    @XBDocURL("http://...")
    public interface ProjectionWithSourceDeclaration {
        // Define your projection methods in a public interface.
        // You may add a document url to specify where to get the document for this projection.
    }

    //END SNIPPET: XBProjectorReferenceCardI

//START SNIPPET: XBProjectorReferenceCard0
XBProjector projector = new XBProjector();
//END SNIPPET: XBProjectorReferenceCard0

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
            projection.hashCode();
            subProjection.hashCode();
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
            projector.hashCode();
            xml.hashCode();
        }
        {
//START SNIPPET: XBProjectorReferenceCard3
            Projection projection =  projector.projectDOMNode(node, Projection.class);
//END SNIPPET: XBProjectorReferenceCard3
            projection.hashCode();
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
            projection.hashCode();
            projection2.hashCode();
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
            projectionWithSystemID.hashCode();
        }
    }

//START SNIPPET: XBProjectorReferenceCard10
    public interface MyProjection {
        @XBRead("some/path/to/data")
        MyCustomType getData();
    }
//END SNIPPET: XBProjectorReferenceCard10

//START SNIPPET: XBProjectorReferenceCard11
    public class MyCustomType {
        public MyCustomType(final String data) {
            //...
        }
    }
//END SNIPPET: XBProjectorReferenceCard11

//START SNIPPET: XBProjectorReferenceCard12
    public static MyCustomType valueOf(final String data) {
        return somehowCreateInstanceFor(data);
    }

    public static MyCustomType of(final String data) {
        return somehowCreateInstanceFor(data);
    }

    public static MyCustomType parse(final String data) {
        return somehowCreateInstanceFor(data);
    }

    public static MyCustomType getInstance(final String data) {
        return somehowCreateInstanceFor(data);
    }
//END SNIPPET: XBProjectorReferenceCard12

    public static MyCustomType somehowCreateInstanceFor(final Object o) {
        return null;
    }

    interface Snipped13 {
//START SNIPPET: XBProjectorReferenceCard13
    @XBRead("/{parentNode}/{subnode}[@id='{id}']")
    String readSomeValue(String parentNode,String subnode,int id);
//END SNIPPET: XBProjectorReferenceCard13
    }

    interface Snipped14 {
//START SNIPPET: XBProjectorReferenceCard14
    @XBRead("/{0}/{1}[@id='{2}']")
    String readSomeValue(String parentNode,String subnode,int id);
//END SNIPPET: XBProjectorReferenceCard14
    }

    {
//START SNIPPET: XBProjectorReferenceCard15
        new XBProjector().config().getTypeConverterAs(DefaultTypeConverter.class).setLocale(Locale.ROOT);
//END SNIPPET: XBProjectorReferenceCard15
    }

    {
//START SNIPPET: XBProjectorReferenceCard16
        new XBProjector().config().getTypeConverterAs(DefaultTypeConverter.class).setTimeZone(TimeZone.getDefault());
//END SNIPPET: XBProjectorReferenceCard16
    }
}
