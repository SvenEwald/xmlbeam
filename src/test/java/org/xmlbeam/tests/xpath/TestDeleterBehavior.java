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
package org.xmlbeam.tests.xpath;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBDelete;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;

public class TestDeleterBehavior {    
    
    @XBDocURL("resource://bigxml.xml")
    public interface DeletingProjection {        
        @XBDelete("{0}")
        DeletingProjection delete(String xpath);
        
        @XBRead("count({0})>0")
        boolean exists(String xpath);
    }
    
    DeletingProjection projection;
    
    @Before
    public void init() throws IOException  {
        projection = new XBProjector().io().fromURLAnnotation(DeletingProjection.class);
    }
    
    @Test
    public void ensureDeletingSingleElement(){
        checkPathDeletion("/rootelement/firstlevel/secondlevel");        
    }
    
    @Test
    public void ensureDeletingAttributes(){
        checkPathDeletion("/rootelement/firstlevel/secondlevel/@a");
    }
    
    @Test
    public void ensureDeletingRootElement(){
        checkPathDeletion("/rootelement");
    }
    
    void checkPathDeletion(String path) {
        assertTrue(projection.exists(path));
        projection.delete(path);
        assertFalse(projection.exists(path));
    }
}
