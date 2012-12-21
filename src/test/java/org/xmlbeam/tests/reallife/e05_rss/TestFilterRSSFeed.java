package org.xmlbeam.tests.reallife.e05_rss;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlbeam.XMLProjector;
import org.xmlbeam.config.DefaultFactoriesConfiguration;
import org.xmlbeam.tests.reallife.e05_rss.SlashdotRSSFeed.Story;

public class TestFilterRSSFeed {

	/**
	 * Remove all but the first three stories from a Slashdot RSS feed. Result
	 * is formatted by standard Transformer capabilities.
	 */
	@Test
	public void filterSomeArticles() throws  IOException			 {
		XMLProjector projector = new XMLProjector();
		SlashdotRSSFeed feed = projector
				.readFromURIAnnotation(SlashdotRSSFeed.class);
		List<Story> filteredItems = new LinkedList<Story>();
		for (Story item : feed.getItems()) {
			filteredItems.add(item);
			if (filteredItems.size() == 3) {
				break;
			}
		}

		// This call removes all but the given items. Other child elements stay
		// untouched.
		feed.setItems(filteredItems);

		// Enable some pretty printing of the resulting xml.
		projector.getTransformer().setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		projector.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");

		System.out.println(feed.toString());
	}
}
