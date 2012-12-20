package org.xmlbeam.tests.reallife.e05_rss;

import java.util.List;

import org.xmlbeam.URI;
import org.xmlbeam.Xpath;

@URI("http://rss.slashdot.org/Slashdot/slashdot")
public interface SlashdotRSSFeed {

	interface Item {
		@Xpath("child::title")
		String getTitle();
	}
	
	@Xpath(value="/rss/channel/item",targetComponentType=Item.class)
	List<Item> getItems();
}
