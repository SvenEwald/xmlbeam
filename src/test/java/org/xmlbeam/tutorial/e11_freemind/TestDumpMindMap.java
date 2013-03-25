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

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tutorial.e11_freemind.MindMap.Node;

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
        List<String> leftLines = new LinkedList<String>();
        List<String> rightLines= new LinkedList<String>();
        walkNodes("", mindMap.getLeftNodes(), leftLines,true);        
        walkNodes("",mindMap.getRightNodes(),rightLines,false);
        int maxLength = Collections.max(leftLines, STRING_LENGTH_COMPARATOR).length();
        String rootText = mindMap.getRootNode().getText();
        System.out.println(String.format("%1$-" + maxLength + "s", "")+rootText);
        String midSpace=rootText.replaceAll(".", " ");
        for (String s : leftLines) {
            String right=rightLines.isEmpty()?"":rightLines.get(0);
            if (!rightLines.isEmpty()){rightLines.remove(0);};
            String line = reverse(String.format("%1$-" + maxLength + "s", s))+midSpace+right;
            System.out.println(line);
        }
        for (String s:rightLines) {
            String line=String.format("%1$-" + maxLength + "s", "")+midSpace+s;
            System.out.println(line);
        }
    }

    private void walkNodes(String prefix, Node[] leftNodes, List<String> lines,boolean reverse) {
        for (Node n : leftNodes) {
            lines.add((prefix + (reverse ?  reverse(n.getText()) : n.getText()) ));
            walkNodes(prefix + (n.getText()).replaceAll(".", " "), n.getSubNodes(), lines,reverse);
        }
    }

    private String reverse(final String s) {
        return new StringBuffer(s).reverse().toString();
    }
}
