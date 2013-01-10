/**
 *  Copyright 2012 Sven Ewald
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
package org.xmlbeam.tutorial.e06_xhtml;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tutorial.Tutorial;

/**
 * Create and print some XHTML text. 
 * (Not that it would be productive to create a website this way, just a demonstration.)
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 *
 */
@Category(Tutorial.class)
public class TestCreationOfXHTMLDocument {

    private final XBProjector projector = new XBProjector();

    @Test
    public void testCreateWellFormedXHTML() {
        XHTML xhtml = projector.create().createEmptyDocumentProjection(XHTML.class);

        xhtml.setRootNameSpace("http://www.w3.org/1999/xhtml").setRootLang("en");
        xhtml.setTitle("This Is My Fine Title");
        xhtml.setBody("Here some text...");
        
                
        System.out.println(xhtml.toString());
    }
}
