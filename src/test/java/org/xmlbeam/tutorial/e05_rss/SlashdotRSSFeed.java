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
package org.xmlbeam.tutorial.e05_rss;

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
