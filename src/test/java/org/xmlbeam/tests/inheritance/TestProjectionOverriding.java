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
package org.xmlbeam.tests.inheritance;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBRead;

@SuppressWarnings("javadoc")
public class TestProjectionOverriding {

    public interface BaseProjection {
        @XBRead("string(1)")
        int getNumber();
        
        @XBRead("string(3)")
        int getAnotherNumber();
    }

    public interface ExtendedProjection extends BaseProjection {
        @XBRead("string(2)")
        int getNumber();
        
        @XBRead("string(4)")
        int getAnotherNumber(String withParam);
        
        @XBRead("string(5)")
        int withOutParam();
    }
    
    @Test
    public void testOverriding() {
        BaseProjection projection = new XBProjector().projectEmptyDocument(BaseProjection.class);
        assertEquals(1,projection.getNumber());
        
        ExtendedProjection subProjection = new XBProjector().projectEmptyDocument(ExtendedProjection.class);
        assertEquals(2,subProjection.getNumber());              
        assertEquals(3,subProjection.getAnotherNumber());
    }
}
