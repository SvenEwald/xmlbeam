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

import org.xmlbeam.Value;
import org.xmlbeam.Xpath;

public interface Element {

    @Xpath(".")
    Element addAttribute(Attribute attribute);

    @Xpath("@{1}")
    Element addAttribute(String name, @Value String value);

    @Xpath("name()")
    String getName();

    @Xpath(".")
    String getText();

    @Xpath("@{0}")
    Attribute attribute(String name);

    @Xpath("@*")
    List<Attribute> attributes();

    @Xpath("./{0}")
    Element element(String name);

    @Xpath(value = "./*", targetComponentType = Element.class)
    List<Element> elements();



}
