package org.xmlbeam.tutorial.e07_svg;

import java.util.Collections;
import java.util.List;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XMLProjector;
import org.xmlbeam.tutorial.e07_svg.SVGDocument.GraphicElement;

public class TestSVGElementSorting {

	@Test
	public void testElements() throws IOException {
		XMLProjector xmlProjector = new XMLProjector();
		SVGDocument svgDocument = xmlProjector.readFromURIAnnotation(SVGDocument.class);
		xmlProjector.addProjectionMixin(GraphicElement.class, new Comparable<GraphicElement>() {

			private GraphicElement me;

			@Override
			public int compareTo(GraphicElement o) {
				assert me != null;
				return me.getPosition().compareTo(o.getPosition());
			}


		});
		List<GraphicElement> list = svgDocument.getGraphicElements();
		Collections.sort(list);
		svgDocument.setGraphicElements(list);
		System.out.println(svgDocument.toString());
	}
}
