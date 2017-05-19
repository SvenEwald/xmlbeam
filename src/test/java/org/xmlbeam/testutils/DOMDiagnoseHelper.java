/**
 *  Copyright 2014 Sven Ewald
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
package org.xmlbeam.testutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("javadoc")
public class DOMDiagnoseHelper {

    private final static String[] ATT_NAMES = new String[] { "NodeType", "NodeName", "LocalName", "NamespaceURI", "Prefix", "NodeValue" };

    public enum DiffType {
        SAME, ONEISNULL, ATTRIBUTES, NODEMAPS, CHILDREN, NONE;

        public DiffType withDetails(final String details) {

            return this;
        }
    }

    public static DiffType diff(final Node a, final Node b) {
        if (a == b) {
            return DiffType.SAME;
        }
        if ((a == null) || (b == null)) {
            return DiffType.ONEISNULL;
        }
        Comparable<Object>[] attributesA = getNodeAttributes(a);
        Comparable<Object>[] attributesB = getNodeAttributes(b);
        if (!Arrays.equals(attributesA, attributesB)) {
            for (int i = 0; (i < ATT_NAMES.length); ++i) {
                if (attributesA[i].compareTo(attributesB[i]) != 0) {
                    return DiffType.ATTRIBUTES.withDetails(ATT_NAMES[i]);
                }
            }
            assert false;
        }
        if (!namedNodeMapsAreEqual(a.getAttributes(), b.getAttributes())) {
            return DiffType.NODEMAPS;
        }
        if (!nodeListsAreEqual(a.getChildNodes(), b.getChildNodes())) {
            return DiffType.CHILDREN;
        }
        return DiffType.NONE;
    }

    @SuppressWarnings("unchecked")
    private static Comparable<Object>[] getNodeAttributes(final Node node) {

        return new Comparable[] { Short.valueOf(node.getNodeType()), node.getNodeName(), node.getLocalName(), node.getNamespaceURI(), node.getPrefix(), node.getNodeValue() };
    }

    private static boolean namedNodeMapsAreEqual(final NamedNodeMap a, final NamedNodeMap b) {
        if (a == b) {
            return true;
        }
        if ((a == null) || (b == null)) {
            return false;
        }
        if (a.getLength() != b.getLength()) {
            return false;
        }

        List<Node> listA = new ArrayList<Node>(a.getLength());
        List<Node> listB = new ArrayList<Node>(a.getLength());

        for (int i = 0; i < a.getLength(); ++i) {
            listA.add(a.item(i));
            listB.add(b.item(i));
        }

        Collections.sort(listA, ATTRIBUTE_NODE_COMPARATOR);
        Collections.sort(listB, ATTRIBUTE_NODE_COMPARATOR);
        for (Node n1 : listA) {
            if (DiffType.NONE != diff(n1, listB.remove(0))) {
                return false;
            }
        }
        return true;
    }

    private static boolean nodeListsAreEqual(final NodeList a, final NodeList b) {
        if (a == b) {
            return true;
        }
        if ((a == null) || (b == null)) {
            return false;
        }
        if (a.getLength() != b.getLength()) {
            return false;
        }
        for (int i = 0; i < a.getLength(); ++i) {
            if (DiffType.NONE != diff(a.item(i), b.item(i))) {
                return false;
            }
        }
        return true;
    }

    private static final Comparator<? super Node> ATTRIBUTE_NODE_COMPARATOR = new Comparator<Node>() {
        private int compareMaybeNull(final Comparable<Object> a, final Object b) {
            if (a == b) {
                return 0;
            }
            if (a == null) {
                return -1;
            }
            if (b == null) {
                return 1;
            }
            return a.compareTo(b);
        }

        @Override
        public int compare(final Node o1, final Node o2) {
            Comparable<Object>[] c1 = getNodeAttributes(o1);
            Comparable<Object>[] c2 = getNodeAttributes(o2);
            assert c1.length == c2.length;
            for (int i = 0; i < c1.length; ++i) {
                int result = compareMaybeNull(c1[i], c2[i]);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }
    };
    
    public static void assertXMLStringsEquals(final String expected,final String result) {
        if (expected==null) {
            if (result==null) {
                return;
            }
            throw new AssertionError("Null was expected, but got: '"+result+"'");
        }
        if (result==null) {
            assertEquals(expected,result);
            return;
        }
        if (expected.replaceAll("\\s", "").equals(result.replaceAll("\\s", ""))) {
            return;
        }
        assertEquals(expected,result);
    }

}
