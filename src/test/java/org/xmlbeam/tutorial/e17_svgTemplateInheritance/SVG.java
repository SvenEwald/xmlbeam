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

import java.util.List;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBWrite;

@SuppressWarnings("javadoc")
@XBDocURL("resource://svg_document_template.svg")
public interface SVG {

    public interface Shape {
        @XBWrite("./@x")
        Shape setX(int x);

        @XBWrite("./@y")
        Shape setY(int y);
    }

    @XBDocURL("resource://rect_template.svg")
    public interface Rect extends Shape {
    }

    @XBDocURL("resource://circle_template.svg")
    public interface Circle extends Shape {
    }

    @XBDocURL("resource://ellipse_template.svg")
    public interface Ellipse extends Shape {
    }

    @XBWrite("/svg/g/*")
    SVG setShapes(List<Shape> shapes);
}
