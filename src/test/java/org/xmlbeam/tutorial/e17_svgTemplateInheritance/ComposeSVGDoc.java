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
package org.xmlbeam.tutorial.e17_svgTemplateInheritance;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.tutorial.e17_svgTemplateInheritance.SVG.Circle;
import org.xmlbeam.tutorial.e17_svgTemplateInheritance.SVG.Ellipse;
import org.xmlbeam.tutorial.e17_svgTemplateInheritance.SVG.Rect;
import org.xmlbeam.tutorial.e17_svgTemplateInheritance.SVG.Shape;

public class ComposeSVGDoc {

    @Test
    public void testSVGComposition() throws IOException {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

        Rect rect = projector.io().fromURLAnnotation(Rect.class);
        Circle circle = projector.io().fromURLAnnotation(Circle.class);
        Ellipse ellipse = projector.io().fromURLAnnotation(Ellipse.class);

        List<Shape> shapes = new LinkedList<Shape>();
        shapes.add(rect.setX(10).setY(120));
        shapes.add(circle.setX(60).setY(60));
        shapes.add(ellipse.setX(180).setY(120));

        SVG svgDoc = projector.io().fromURLAnnotation(SVG.class);
        svgDoc.setShapes(shapes);

        System.out.println(svgDoc.toString());

    }
}
