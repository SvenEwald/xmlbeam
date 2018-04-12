/**
 *  Copyright 2018 Sven Ewald
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
package org.xmlbeam.tests;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.xmlbeam.XBProjector;

/**
 * @author sven
 *
 */
public class SecurityTests {

    private final static String XML="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + 
            "  <!DOCTYPE foo [  \n" + 
            "   <!ELEMENT foo ANY >\n" + 
            "   <!ENTITY xxe SYSTEM \"file:///etc/passwd\" >]><foo>&xxe;</foo>";
    
    @Test
    public void badEntityResolver() {
        Map<String,String> map = new XBProjector().onXMLString(XML).createMapOf(String.class);
        assertTrue(map.isEmpty());
    }
}
