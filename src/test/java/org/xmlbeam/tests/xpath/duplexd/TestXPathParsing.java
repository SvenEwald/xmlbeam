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
package org.xmlbeam.tests.xpath.duplexd;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.util.intern.DOMHelper;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.BuildDocumentVisitor;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.ParseException;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.SimpleNode;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParser;

/**
 * @author sven
 */
@RunWith(Parameterized.class)
public class TestXPathParsing {

    @XBDocURL("resource://tests.xml")
    public interface Projection extends DOMAccess {
        @XBRead("/tests/test")
        List<Projection> getTests();

        @XBRead("./before/child::*")
        Projection getBefore();

        @XBRead("./after/child::*")
        Projection getAfter();

        @XBRead("./@id")
        String getTestId();

        @XBRead("./xpath")
        String getXPath();

    };

    private Document document;
    private final String testId;
    private final Projection before;
    private final String xpath;
    private final Projection after;

    private final static int RUN_ONLY = -1;

//    @Before
//    public void prepare() {
//        document = new DefaultXMLFactoriesConfig().createDocumentBuilder().newDocument();
//    }

    public TestXPathParsing(String id, Projection before, String xpath, Projection after) {
        this.testId = id;
        this.before = before;
        this.xpath = xpath;
        this.after = after;
    }

    @Test
    public void testXPathParsing() throws Exception {
        //String xpath = "let $incr :=       function($n) {$n+1}  \n return $incr(2)";
//        if ((before!=null) && (before.getDOMNode()!=null)) {
//            Node node = before.getDOMNode().cloneNode(true);
//            document.adoptNode(node);
//            document.appendChild(node);
//        }
        document = before.getDOMOwnerDocument();
        // String xpath = "/hoo[@id='wutz']/foo/loo";
        createByXParser(xpath);
        Projection result = new XBProjector(Flags.TO_STRING_RENDERS_XML).projectDOMNode(document, Projection.class);
        after.getDOMNode().normalize();
        DOMHelper.trim(after.getDOMNode());
//        DOMHelper.trim(after.getDOMNode());
        result.getDOMNode().normalize();
        DOMHelper.trim(result.getDOMNode());
        //   after.getDOMOwnerDocument().normalizeDocument();
        //result.getDOMOwnerDocument().normalizeDocument();
        DOMHelper.nodesAreEqual(after.getDOMNode(), result.getDOMNode());
        assertEquals(after, result);
    }

    @Parameters
    public static Collection<Object[]> tests() throws Exception {
        List<Object[]> params = new LinkedList<Object[]>();
        Projection testDefinition = new XBProjector(Flags.TO_STRING_RENDERS_XML).io().fromURLAnnotation(Projection.class);
        int count = 0;
        for (Projection test : testDefinition.getTests()) {
            final Object[] param = new Object[4];
            param[0] = "["+count+"] "+test.getTestId();
            param[1] = subProjectionToDocument(test.getBefore());
            param[2] = test.getXPath().trim();
            param[3] = subProjectionToDocument(test.getAfter());
            if ((count++ == RUN_ONLY)||(RUN_ONLY < 0)) {
                params.add(param);
            }
        }
        return params;
    }

    /**
     * @param test
     * @return
     */
    private static Projection subProjectionToDocument(Projection test) {
        Document document = new DefaultXMLFactoriesConfig().createDocumentBuilder().newDocument();

        if (test != null) {
            Node node = test.getDOMNode().cloneNode(true);
            document.adoptNode(node);
            document.appendChild(node);
        }

        return new XBProjector(Flags.TO_STRING_RENDERS_XML).projectDOMNode(document, Projection.class);
    }

    /**
     * @param xpath
     * @throws ParseException
     */
    private void createByXParser(String xpath) throws ParseException {
        XParser parser = new XParser(new StringReader(xpath));
        SimpleNode node = parser.START();
        System.out.println("-----------------------------------------");
        System.out.println(testId);
        System.out.println(xpath);
        node.dump("");

        node.jjtAccept(new BuildDocumentVisitor(), document);

        //print();
    }

    public void print() {
        final StringWriter writer = new StringWriter();
        try {
            new DefaultXMLFactoriesConfig().createTransformer().transform(new DOMSource(document), new StreamResult(writer));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        final String output = writer.getBuffer().toString();
        System.out.println(output);
    }
}
