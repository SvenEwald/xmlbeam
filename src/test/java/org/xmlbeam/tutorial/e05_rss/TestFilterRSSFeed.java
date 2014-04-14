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

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tutorial.Tutorial;
import org.xmlbeam.tutorial.TutorialTestCase;
import org.xmlbeam.tutorial.e05_rss.SlashdotRSSFeed.Story;
/* START SNIPPET: TutorialDescription
~~
 This example is to demonstrate how to modify a XML document. The Slashdot RSS
 feed is projected to this interface and the stories will be accessible via a
 subprojection. This time there is a setter for a collection of Stories which
 will replace the existing sequence of rss items. 
 Instead of just setting an elements value like in the last example, we now change a sequence of elements.
 Notice that there are name spaces in the source XML document and that we work with them intuitively.
END SNIPPET: TutorialDescription */

@Category(Tutorial.class)
@SuppressWarnings("javadoc")
//START SNIPPET:TestFilterRSSFeed
public class TestFilterRSSFeed extends TutorialTestCase{

    private static SlashdotRSSFeed feed;

    @BeforeClass
    public static void readFeed() throws IOException {
        XBProjector projector = new XBProjector();
        feed = projector.io().fromURLAnnotation(SlashdotRSSFeed.class);
    }

    @Test
    public void printSomeStats() {
        Set<String> creators = new HashSet<String>(feed.getCreators());
        System.out.println("There are " + feed.getAllItems().size() + " stories by " + creators.size() + " different creators.");
    }

    /**
     * Remove all but the first three stories from a Slashdot RSS feed. Result
     * is formatted by standard Transformer capabilities.
     */
    @Test
    public void filterSomeArticles() throws IOException {
        List<Story> filteredItems = new LinkedList<Story>();
        for (Story item : feed.getAllItems()) {
            filteredItems.add(item);
            if (filteredItems.size() == 3) {
                break;
            }
        }

        // This call removes all but the given items. Other child elements stay
        // untouched.
        feed.setAllItems(filteredItems);

        assertEquals(3,feed.getItemCount());
        
        // System.out.println(feed.toString());
    }
}
//END SNIPPET:TestFilterRSSFeed
