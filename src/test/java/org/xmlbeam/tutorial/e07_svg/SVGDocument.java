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

import java.util.List;

import org.xmlbeam.URL;
import org.xmlbeam.Xpath;

/**
 * This example shows the modification of a SVG Graphic. A list of elements
 * should be sorted by an user defined criteria. We like to archive this by
 * adding behavior to our projections (made them comparable).
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 * 
 */
@URL("resource://svg.xml")
public interface SVGDocument {
	
	/**
	 * We define a sub projection here to reflect the XML {@code <rect>} element
	 * behind it. Notice: Although we only define a getter to one attribute, we
	 * will work with the complete element when changing the order of the
	 * rectangles.
	 */
	public interface GraphicElement extends Comparable<GraphicElement> {
		@Xpath("@y")
		Integer getYPosition();
	}
	
	@Xpath(value = "/svg/rect", targetComponentType = GraphicElement.class)
	List<GraphicElement> getGraphicElements();

	@Xpath("/svg/rect")
	SVGDocument setGraphicElements(List<GraphicElement> elements);

}
