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
package org.xmlbeam.tutorial.e11_freemind;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

@XBDocURL("file:./doc/XMLBeam.mm")
public interface MindMap {

    public interface Node {
        @XBRead("./node")
        Node[] getSubNodes();

        @XBRead("@TEXT")
        String getText();

        @XBRead("count(./ancestor::node)")
        int getDepth();

        @XBRead("count(./preceding-sibling::node)")
        int getPosition();

        @XBRead("count(descendant::node)")
        int getChildNodeCount();

        @XBRead("@x")
        int getX();

        @XBRead("@y")
        int getY();

        @XBWrite("@x")
        Node setX(int x);

        @XBWrite("@y")
        Node setY(int y);
    }

    @XBRead("//node")
    Node[] getNodes();

    @XBRead("/map/node")
    Node getRootNode();

    @XBRead(value = "//node[@POSITION=''left'']/descendant-or-self::node")
    Node[] getLeftSubNodes();

    @XBRead("//node[@POSITION=''right'']/descendant-or-self::node")
    Node[] getRightSubNodes();

    @XBRead(value = "//node[@POSITION=''left'']")
    Node[] getLeftNodes();
}
