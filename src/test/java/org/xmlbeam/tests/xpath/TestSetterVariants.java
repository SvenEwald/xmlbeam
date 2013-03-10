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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.tests.xpath.SetterVariants.SubProjection;

/**
 *
 */
public class TestSetterVariants {

    private final XBProjector projector = new XBProjector();
    private SetterVariants emptyProjection;
    
    @Before
    public void before(){
    projector.config().as(DefaultXMLFactoriesConfig.class).setPrettyPrinting(false);
    emptyProjection = projector.projectEmptyDocument(SetterVariants.class);
    }

    @Test
    public void testSingleContent() {       
        emptyProjection.setSingleElementContent(1);
        assertEquals("<a><b>1</b></a>",emptyProjection.toString());
    }
    
    @Test
    public void testSetMultipleElementContent() {
        emptyProjection.setMultipleElementContent(new int[]{1,2,3});
        assertEquals("<a><b>1</b><b>2</b><b>3</b></a>",emptyProjection.toString());
    }

    @Test
    public void testSetMultipleElementContentViaCollection() {
        emptyProjection.setMultipleElementContent(Arrays.asList(1, 2, 3));
        assertEquals("<a><b>1</b><b>2</b><b>3</b></a>", emptyProjection.toString());
    }

    @Test
    public void testSetSingleSubProjection() {
        emptyProjection.setSingleSubProjection(projector.projectEmptyElement("c", SubProjection.class).setValue(1));
        assertEquals("<a><b><c>1</c></b></a>", emptyProjection.toString());
    }
}
