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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;
import org.xmlbeam.io.XBUrlIO;
import org.xmlbeam.util.HTTPParrot;
import org.xmlbeam.util.IOHelper;

/**
 */
public class IOViaUrl {

    public interface FooProjection {
        @XBRead("name(/*)")
        String getRootName();
    };

    @Test
    public void ensureHTTPGetRespectsAdditionalRequestParamsInHeader() throws Exception {
        HTTPParrot parrot = HTTPParrot.serve("<foo/>");        
        FooProjection projection = addRequestParams(new XBUrlIO(new XBProjector(),parrot.getURL())).read(FooProjection.class);
        assertEquals("foo", projection.getRootName());
        validateRequest(parrot.getRequest());
    }
    
    @Test
    public void ensureHTTPPostRespectsAdditionalRequestParamsInHeader() throws Exception {
        HTTPParrot parrot = HTTPParrot.serve("<foo/>");    
        FooProjection projection = new XBProjector().create().createEmptyDocumentProjection(FooProjection.class);
        addRequestParams(new XBUrlIO(new XBProjector(),parrot.getURL())).write(projection);
        validateRequest(parrot.getRequest());
    }
    
    private XBUrlIO addRequestParams(XBUrlIO io) {
        return io.addRequestParam("testparam", "mustBeInRequest").addRequestParams(IOHelper.createBasicAuthenticationProperty("user", "password"));
    }
    
    private void validateRequest(String request) {
        assertTrue(request.contains("Authorization: Basic dXNlcjpwYXNzd29yZA=="));        
        assertTrue(request.contains("testparam: mustBeInRequest"));
    }
}
