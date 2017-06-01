/**
 *  Copyright 2016 Sven Ewald
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
package org.xmlbeam.refcards;

import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.types.XBAutoValue;

@SuppressWarnings({ "javadoc", "unused" })
public class XBAutoValueRefCardTest {

    Example example = new XBProjector().projectXMLString("<foo @bar='value'/>", Example.class);

    //START SNIPPET: ProjectedValueRefCardExample
    public interface Example {

        @XBRead("/foo/@bar")
        XBAutoValue<String> getBar();

    }
    {
        // read the value
        String value = example.getBar().get();

        // sets the value
        example.getBar().set("new value");

        // checks for existence
        if (example.getBar().isPresent()) {

        }

        // removes attribute from foo element
        example.getBar().remove();

        // check for existence and get the value
        for (String bar:example.getBar()) {
            // only invoked if attribute is present
        }

        // get the name of the element or attribute holding the value
        String attributeName =example.getBar().getName(); // "bar" in this example

        // rename the XML attribute 'bar' to 'bar2' in the XML
        example.getBar().rename("bar2");
    }
    //END SNIPPET: ProjectedValueRefCardExample

}
