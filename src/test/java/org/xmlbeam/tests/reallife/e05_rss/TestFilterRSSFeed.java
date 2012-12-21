package org.xmlbeam.tests.reallife.e05_rss;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlbeam.XMLProjector;
import org.xmlbeam.config.DefaultConfiguration;
import org.xmlbeam.tests.reallife.e05_rss.SlashdotRSSFeed.Item;

public class TestFilterRSSFeed {

	@Test 
	public void filterSomeArticles() throws SAXException, IOException, ParserConfigurationException {
		SlashdotRSSFeed feed = new XMLProjector().readFromURIAnnotation(SlashdotRSSFeed.class);
		for (Item item:feed.getItems()) {
			System.out.println(item.getTitle());
		}
		
		//System.out.println(feed.toString());
	}
}
