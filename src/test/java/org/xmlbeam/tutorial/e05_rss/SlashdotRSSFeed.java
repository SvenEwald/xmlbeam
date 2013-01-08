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

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

/**
 * This example is to demonstrate how to modify a XML document. The Slashdot RSS
 * feed is projected to this interface and the stories will be accessible via a
 * subprojection. This time there is a setter for a collection of Stories which
 * will replace the existing sequence of rss items.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
@XBDocURL("http://rss.slashdot.org/Slashdot/slashdot")
public interface SlashdotRSSFeed {

    /**
     * We like to access each story as a java object, so we define a sub
     * projection here. Notice that this does not have to be an inner interface,
     * thats just to compact this tutorial.
     */
    interface Story {
        @XBRead("child::title")
        String getTitle();

        @XBRead("child::pubDate")
        String getDate();

        @XBRead("child::description")
        String getDescription();
    }

    /**
     * Our getter method with an XPath expression to select all RSS items. The
     * target type definition specifies this getter as returning a list of sub
     * projections.
     * 
     * @return List of all stories
     */
    @XBRead(value = "/rss/channel/item", targetComponentType = Story.class)
    List<Story> getAllItems();

    /**
     * Our setter uses exact the same XPath expression as the getter. Thus it
     * will replace the items returned by getAllItems(). Notice that it will
     * only replace XML elements named "item", because that is exactly what the
     * XPath is selecting. Other child elements of the channel element won't be
     * touched.
     * 
     * Notice that we could define another setter which could project a story to
     * other elements than "item" in the document hierarchy. I just have no real
     * life example for this right now...
     * 
     * @param items
     */
    @XBWrite("/rss/channel/item")
    void setAllItems(Collection<Story> items);

    /**
     * This is not part of this lesson about modifying documents. Just to
     * demonstrate the flexibility of projections. There is no need to strictly
     * keep the object oriented way to the stories. We just define a getter for
     * all creator elements of all stories without a sub projection.
     * 
     * @return A list of all creators.
     */
    @XBRead(value = "//dc:creator", targetComponentType = String.class)
    List<String> getCreators();

    /**
     * Another getter not part of this lesson. This time we let the projection
     * declaration do some filtering. Usually you would have to code this in
     * java.
     * 
     * @return A filtered list of stories.
     */
    @XBRead(value = "/rss/channel/item[dc:subject=opensource]", targetComponentType = Story.class)
    List<Story> getOpenSourceStories();
}
