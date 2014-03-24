/**
 *  Copyright 2013 Sven Ewald
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
package org.xmlbeam.tutorial.e08_api_mimicry;

import java.util.List;

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;

//START SNIPPET:Element
public interface Element {

    @XBRead(".")
    Element addAttribute(Attribute attribute);

    @XBWrite("@{1}")
    Element addAttribute(String name, @XBValue String value);

    @XBRead("@{0}")
    Attribute attribute(String name);

    @XBRead("count(@*)")
    int attributeCount();

    @XBRead("@*")
    List<Attribute> attributes();

    @XBRead("@{0}")
    String attributeValue(String attributeName);

    @XBRead("./{0}")
    Element element(String name);

    @XBRead("./*")
    List<Element> elements();

    @XBRead("name()")
    String getName();

    @XBRead(".")
    String getText();

}
//END SNIPPET:Element
