/*
 * Copyright 2025 Sven Ewald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlbeam.tests.doc;

import java.util.List;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;

@SuppressWarnings("javadoc")
@XBDocURL("resource://XBProjector-Sheet-compact.graphml")
public interface GraphML {

    @XBRead("//xbdefaultns:edge[@target='{0}']/@source")
    String getParentOf(String node);

    @XBRead(value = "//xbdefaultns:edge[@source='{0}']/@target")
    List<String> getChildrenOf(String node);

    @XBRead("//xbdefaultns:node[@id='{0}']")
    Node getNode(String id);

    @XBRead("//xbdefaultns:node[normalize-space(//y:NodeLabel)='new XBProjector()']")
    Node getRootNode();
}
