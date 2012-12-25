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

import java.util.LinkedList;
import java.util.List;

import java.io.IOException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import org.junit.Test;
import org.xmlbeam.XMLProjector;
import org.xmlbeam.config.DefaultFactoriesConfiguration;
import org.xmlbeam.tutorial.e05_rss.SlashdotRSSFeed.Story;

public class TestFilterRSSFeed {

	/**
	 * Remove all but the first three stories from a Slashdot RSS feed. Result
	 * is formatted by standard Transformer capabilities.
	 */
	@Test
	public void filterSomeArticles() throws  IOException			 {
		XMLProjector projector = new XMLProjector(new DefaultFactoriesConfiguration() {
			@Override
			public Transformer createTransformer() {
				Transformer transformer = super.createTransformer();
				// Enable some pretty printing of the resulting xml.
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				return transformer;

			}
		});
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

		System.out.println(feed.toString());
	}
}
