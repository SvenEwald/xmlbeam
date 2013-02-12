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
package org.xmlbeam.tutorial.e07_svg;

import java.util.Collections;
import java.util.List;

import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tutorial.Tutorial;
import org.xmlbeam.tutorial.TutorialTestCase;
import org.xmlbeam.tutorial.e07_svg.SVGDocument.GraphicElement;

/**
 * Demonstation of the concept of "projection mixins". While its not possible to
 * let your own classes implements projection interfaces without losing the
 * projection functionality, it is possible to define methods in a second
 * interface called a mixin interface. If you let a projection extend a mixin
 * interface, any object implementing the mixin interface can be registered to
 * handle the method calls of the mixin interface. In this example the interface
 * {@link Comparable} will be our mixin interface. Its possible to have a
 * projection extending multiple mixin interfaces. Notice: Because the method
 * call of a mixin method on a projection will not be executed by the projection
 * instance itself but by your specified mixin implementation, we need a field
 * to inject the current projection instance into. By convention this field must
 * me named "me" (to resemble the keyword "this") and have the type of the
 * projection. It may be private. <br>
 * (Yes, the result of this example could have been archived by specifying a
 * Comparator when calling Collections.sort().)
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 * 
 */
@Category(Tutorial.class)
public class TestSVGElementSorting extends TutorialTestCase {

    @Test
    public void testElements() throws IOException {
        //START SNIPPET:TestSVGElementSorting
        XBProjector xmlProjector = new XBProjector();
        SVGDocument svgDocument = xmlProjector.io().fromURLAnnotation(SVGDocument.class);
        xmlProjector.mixins().addProjectionMixin(GraphicElement.class, new Comparable<GraphicElement>() {

            private GraphicElement me;

            @Override
            public int compareTo(GraphicElement o) {
                return me.getYPosition().compareTo(o.getYPosition());
            }

        });
        List<GraphicElement> list = svgDocument.getGraphicElements();
        Collections.sort(list);
        svgDocument.setGraphicElements(list);
      //END SNIPPET:TestSVGElementSorting
        
    }
}
