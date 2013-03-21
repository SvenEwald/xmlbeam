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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tutorial.e11_freemind.MindMap.Node;

/**
 *
 */
public class TestDumpMindMap {

    private static final Comparator<? super String> STRING_LENGTH_COMPARATOR = new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            return Integer.valueOf(o1.length()).compareTo(o2.length());
        }
    };

    @Test
    public void dump() throws IOException {
        MindMap mindMap = new XBProjector().io().fromURLAnnotation(MindMap.class);
        // dumpNode("", mindMap.getRootNode());
        List<String> leftLines = new LinkedList<String>();
        dumpLeft("", mindMap.getLeftNodes(), leftLines);
        int maxLength = Collections.max(leftLines, STRING_LENGTH_COMPARATOR).length();
        List<String> lines = new LinkedList<String>();
        int i = 0;
        for (String s : leftLines) {
            String line = reverse(String.format("%1$-" + maxLength + "s", s));
            if (++i == leftLines.size() / 2) {
                line += mindMap.getRootNode().getText();
            }
            System.out.println(line);
        }

    }

    /**
     * @param leftNodes
     */
    private void dumpLeft(String prefix, Node[] leftNodes, List<String> lines) {
        for (Node n : leftNodes) {
            lines.add((prefix + reverse(n.getText())));
            dumpLeft(prefix + "         ", n.getSubNodes(), lines);
        }

    }

    /**
     * @param rootNode
     */
    private void dumpNode(String prefix, Node node) {
        System.out.println(prefix + node.getText());
        for (Node subnode : node.getSubNodes()) {
            dumpNode(prefix + "  ", subnode);
        }
    }

    private String reverse(final String s) {
        return new StringBuffer(s).reverse().toString();
    }
}
