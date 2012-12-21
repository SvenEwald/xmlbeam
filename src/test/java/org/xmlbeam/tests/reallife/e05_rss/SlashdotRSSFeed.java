package org.xmlbeam.tests.reallife.e05_rss;

import java.util.Collection;
import java.util.List;

import org.xmlbeam.URI;
import org.xmlbeam.Xpath;

/**
 * This example is to demonstrate how to modify a XML document. The Slashdot RSS
 * feed is projected to this interface and the stories will be accessible via a
 * subprojection. This time there is a setter for a collection of Stories which
 * will replace the existing sequence of rss items.
 * 
 *  @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
@URI("http://rss.slashdot.org/Slashdot/slashdot")
public interface SlashdotRSSFeed {

	interface Story {
		@Xpath("child::title")
		String getTitle();

		@Xpath("child::pubDate")
		String getDate();
	}

	@Xpath(value = "/rss/channel/item", targetComponentType = Story.class)
	List<Story> getItems();

	@Xpath(value = "/rss/channel/item")
	void setItems(Collection<Story> items);
}
