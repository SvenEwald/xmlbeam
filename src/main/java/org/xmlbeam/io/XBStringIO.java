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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xmlbeam.XBProjector;

/**
 *
 */
public class XBStringIO {
    
    private final XBProjector projector;
    private String systemID;
    
    public XBStringIO(XBProjector xmlProjector) {
        this.projector = xmlProjector;
    }
    
    public <T> T parseXMLString(final String string, final Class<T> projectionInterface) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(string.getBytes());
        try {
            return new XBStreamInput(projector,inputStream).setSystemID(systemID).read(projectionInterface);
        } catch (IOException e) {
            throw new RuntimeException(e);            
        }
    }
    
    public String asString(Object projection) {
        return projection.toString();
    }
    
    public XBStringIO setSystemID(String systemID) {
        this.systemID=systemID;
        return this;
    }
}
