package org.xmlbeam.tutorial.e07_svg;

import java.util.List;

import org.xmlbeam.URI;
import org.xmlbeam.Xpath;

/**
 * This example shows the modification of a SVG Graphic. A list of elements
 * should be sorted by an user defined criteria. We like to archive this by
 * adding behavior (made them compareable) to our projections.
 * 
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 * 
 */
@URI("resource://svg.xml")
public interface SVGDocument {
	
	public interface GraphicElement extends Comparable<GraphicElement> {
		@Xpath("@y")
		Integer getPosition();
	}
	
	@Xpath(value = "/svg/rect", targetComponentType = GraphicElement.class)
	List<GraphicElement> getGraphicElements();

	@Xpath("/svg/rect")
	SVGDocument setGraphicElements(List<GraphicElement> elements);

}
