/**
 *  Copyright 2016 Sven Ewald
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
package org.xmlbeam.tests.bugs;

import org.junit.Test;
import org.xmlbeam.XBDataNotFoundException;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBDelete;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBUpdate;

/**
 *
 */
@SuppressWarnings("javadoc")
public class TestDataNotFoundException {

 private final static XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
    
    private final static String XML = "<root><element attrib='attribValue'>elementValue</element></root>";
   
    private final static TestProjection projection = projector.projectXMLString(XML, TestProjection.class);
    
    interface TestProjection {
        @XBRead("/root/element/@notthere")
        String getNonexistingAttribute() throws XBDataNotFoundException;
        
        @XBRead("/root/element/notthere")
        String getNonexistingValue() throws XBDataNotFoundException;
        
        @XBUpdate("/root/element/@notthere")
        void updateNonexistingAttribute(String v) throws XBDataNotFoundException;
        
        @XBUpdate("/root/element/notthere")
        void updateNonexistingValue(String v) throws XBDataNotFoundException;
        
        @XBDelete("/root/element/@notthere")
        void deleteNonexistingAttribute() throws XBDataNotFoundException;
        
        @XBDelete("/root/element/notthere")
        void deleteNonexistingValue() throws XBDataNotFoundException;
    }
   
    @Test(expected=XBDataNotFoundException.class)
    public void testExceptionOnAttributget() {
       projection.getNonexistingAttribute();
    }
    
    @Test(expected=XBDataNotFoundException.class)
    public void testExceptionOnValueget() {
       projection.getNonexistingValue();
    }

    @Test(expected=XBDataNotFoundException.class)
    public void testExceptionOnAttributupdate() {
       projection.updateNonexistingAttribute("x");
    }
    
    @Test(expected=XBDataNotFoundException.class)
    public void testExceptionOnValueupdate() {
       projection.updateNonexistingValue("x");
    }
    
    @Test(expected=XBDataNotFoundException.class)
    public void testExceptionOnAttributdelete() {
       projection.deleteNonexistingAttribute();
    }
    
    @Test(expected=XBDataNotFoundException.class)
    public void testExceptionOnValuedelete() {
       projection.deleteNonexistingValue();
    }
}
