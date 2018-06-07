package org.xmlbeam.tests.bugs.bug55;

import static org.junit.Assert.*;

import org.junit.Test;

import org.w3c.dom.Node;

import org.xmlbeam.XBProjector;
import org.xmlbeam.types.XBAutoMap;

public class SimpleTest {

    @Test
    public void test1() {
        // fetch the node, this works fine
        String xml2 = "<a><b><![CDATA[c]]></b></a>";
        XBAutoMap<Node> map2 = new XBProjector().onXMLString(xml2).createMapOf(Node.class);
        String c2 = map2.get("//a/b").getTextContent();
        assertEquals("c", c2);
    }

    @Test
    public void test2() {
        // no cdata, works with String
        String xml = "<a><b>c</b></a>";
        XBAutoMap<String> map = new XBProjector().onXMLString(xml).createMapOf(String.class);
        String c = map.get("//a/b");
        assertEquals("c", c);
    }

    @Test
    public void test2b() {
        // but this doesn't work, it should
        String xml = "<a><b>c</b></a>";
        XBAutoMap<String> map2 = new XBProjector().onXMLString(xml).createMapOf(String.class);
        String c2 = map2.get("//a/b/text()");
        assertEquals("c", c2);
    }

    @Test
    public void test3() {
        // this fails
        String xml = "<a><b><![CDATA[c]]></b></a>";
        XBAutoMap<String> map = new XBProjector().onXMLString(xml).createMapOf(String.class);
        String c = map.get("//a/b");
        assertEquals("c", c);
    }
}
