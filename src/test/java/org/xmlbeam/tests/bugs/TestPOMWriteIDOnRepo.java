/**
 *  Copyright 2014 Sven Ewald
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

import static org.junit.Assert.*;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;

/**
 *
 */
@SuppressWarnings("javadoc")
public class TestPOMWriteIDOnRepo {

    public interface POM {
        
        @XBWrite("/project/repositories/repository/id")
        POM setID(String id);
        
        @XBWrite("/project/repositories/repository/url")
        POM setURL(String url);
        
        @XBWrite("/project/repositories/repository[id='{0}']/id")
        POM brokenSetter(String prevID, @XBValue String newID);
                
    }
        
    
    @Test
    public void testWriteRepo() {
        POM pom = createPOM("spring-libs-snapshot","http://repo.spring.io/libs");
//        System.out.println(pom);
        pom.brokenSetter("spring-libs-snapshot", "spring-libs-release");
//        System.out.println(pom);
        assertEquals(createPOM("spring-libs-release","http://repo.spring.io/libs"),pom);
    }

    private POM createPOM(String id, String url) {
        POM pom = new XBProjector(Flags.TO_STRING_RENDERS_XML).projectEmptyDocument(POM.class);
        return pom.setID(id).setURL(url);
    }
}
