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
package org.xmlbeam.tests.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.io.XBUrlIO;
import org.xmlbeam.testutils.HTTPParrot;
import org.xmlbeam.util.IOHelper;

/**
 */
public class TestIOBehavior {

    public interface FooProjection extends DOMAccess{
        @XBRead("name(/*)")
        String getRootName();
    };

    @XBDocURL("http://{0}:{1,number,#}/path")
    public interface FooProjectionWithDocSource extends FooProjection {
    }
    
    public interface ExternalFooProjection extends FooProjection {        
        @XBRead("//foo")
        @XBDocURL("{0}")
        FooProjection getExternalProjection(String url,Map<String,String> requestParams);
        
        @XBWrite("/*")
        @XBDocURL("{0}")
        FooProjection postExternalProjection(String url,@XBValue FooProjection value);
    }

    @Test
    public void ensureHTTPGetRespectsAdditionalRequestParamsInHeader() throws Exception {
        HTTPParrot parrot = HTTPParrot.serve("<foo/>");
        FooProjection projection = addRequestParams(new XBUrlIO(new XBProjector(), parrot.getURL().toString())).read(FooProjection.class);
        assertEquals("foo", projection.getRootName());
        validateRequest(parrot.getRequest());
    }

    @Test
    public void ensureHTTPPostRespectsAdditionalRequestParamsInHeader() throws Exception {
        HTTPParrot parrot = HTTPParrot.serve("<foo/>");
        FooProjection projection = new XBProjector().projectEmptyDocument(FooProjection.class);
        addRequestParams(new XBUrlIO(new XBProjector(), parrot.getURL().toString())).write(projection);
        validateRequest(parrot.getRequest());
    }

    @Test
    public void ensureHTTPGetRespectsSystemID() throws Exception {
        HTTPParrot parrot = HTTPParrot.serve("<foo/>");
        FooProjection projection = addRequestParams(new XBUrlIO(new XBProjector(), parrot.getURL().toString())).read(FooProjection.class);
        assertEquals("foo", projection.getRootName());
        assertEquals(parrot.getURL().toString(), projection.getDOMOwnerDocument().getBaseURI());
    }

    @Test
    public void ensureStreamParsingRespectsSystemID() throws Exception {
        String systemID = "http://xmlbeam.org/MyFineSystemID";
        ByteArrayInputStream inputStream = new ByteArrayInputStream("<foo/>".getBytes());
        FooProjection projection = new XBProjector().io().stream(inputStream).setSystemID(systemID).read(FooProjection.class);
        assertEquals(systemID, projection.getDOMOwnerDocument().getBaseURI());
        assertEquals("foo", projection.getRootName());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new XBProjector().io().stream(outputStream).write(projection);
        assertEquals("<foo/>", outputStream.toString("UTF-8").trim());
    }

    @Test
    public void ensureGetDocURLAnnotationWorksWithParams() throws Exception {
        HTTPParrot parrot = HTTPParrot.serve("<foo/>");
        String host = parrot.getURL().getHost();
        int port = parrot.getURL().getPort();
        Map<String, String> requestParams = new HashMap<String, String>(1);
        requestParams.put("A", "B");
        FooProjectionWithDocSource projection = new XBProjector().io().fromURLAnnotation(FooProjectionWithDocSource.class, host, port, requestParams);
        assertTrue(parrot.getRequest().contains("A: B"));
        assertEquals("foo", projection.getRootName());
    }
    
    @Test
    public void ensureGetExternalProjectionsWorksWithParams() throws Exception {
        HTTPParrot parrot = HTTPParrot.serve("<foo/>");
        Map<String, String> requestParams = new HashMap<String, String>(1);
        requestParams.put("A", "B");
        FooProjection projection =new XBProjector().projectEmptyDocument(ExternalFooProjection.class).getExternalProjection(parrot.getURL().toString(), requestParams);
        assertTrue(parrot.getRequest().contains("A: B"));
        assertEquals("foo", projection.getRootName());
    }

    @Test
    public void ensurePostDocURLAnnotationWorksWithParams() throws Exception {
        HTTPParrot parrot = HTTPParrot.serve("<foo/>");
        String host = parrot.getURL().getHost();
        int port = parrot.getURL().getPort();
        Map<String, String> requestParams = new HashMap<String, String>(1);
        requestParams.put("A", "B");
        FooProjectionWithDocSource projection = new XBProjector().projectEmptyDocument(FooProjectionWithDocSource.class);
        new XBProjector().io().toURLAnnotationViaPOST(projection, host, port, requestParams);
        assertTrue(parrot.getRequest().contains("A: B"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void ensurePostExternalProjectionsWorksWithParams() throws Exception {
        FooProjection postValue = new XBProjector().projectEmptyDocument(FooProjection.class);
        new XBProjector().projectEmptyDocument(ExternalFooProjection.class).postExternalProjection("http://foo",postValue);
    }

    @Test
    public void testFileIO() throws IOException {
        XBProjector projector=new XBProjector(new DefaultXMLFactoriesConfig().setPrettyPrinting(false));
        FooProjection p = projector.projectXMLString("<foo><bar/></foo>", FooProjection.class);
        File tempFile = File.createTempFile(this.getClass().getSimpleName(), Long.toBinaryString(System.currentTimeMillis()));
        {
            projector.io().file(tempFile).write(p);
            assertEquals(17, tempFile.length());
        }
        {
            FooProjection p2 = new XBProjector().io().file(tempFile).read(FooProjection.class);
            assertEquals(p, p2);
        }
        {
            projector.io().file(tempFile).setAppend(true).write(p);
            assertEquals(34, tempFile.length());
        }
        {
            projector.io().file(tempFile).setAppend(false).write(p);
            assertEquals(17, tempFile.length());
        }
        tempFile.delete();
    }

    private XBUrlIO addRequestParams(XBUrlIO io) {
        return io.addRequestProperty("testparam", "mustBeInRequest").addRequestProperties(IOHelper.createBasicAuthenticationProperty("user", "password"));
    }

    private void validateRequest(String request) {
        assertTrue(request.contains("Authorization: Basic dXNlcjpwYXNzd29yZA=="));
        assertTrue(request.contains("testparam: mustBeInRequest"));
    }

}
