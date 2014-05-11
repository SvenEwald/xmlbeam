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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.BuildDocumentVisitor;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.SimpleNode;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParser;

/**
 * @author sven
 */
public class TestXPathParsing {

    private Document document;

    @Before
    public void prepare() {
        document = new DefaultXMLFactoriesConfig().createDocumentBuilder().newDocument();
    }

    @Test
    public void testXPathParsing() throws Exception {
        //String xpath = "let $incr :=       function($n) {$n+1}  \n return $incr(2)";
        String xpath = "/hoo[@id='wutz']/foo/loo";
        XParser parser = new XParser(new StringReader(xpath));
        SimpleNode node = parser.START();
        node.dump("");

        node.jjtAccept(new BuildDocumentVisitor(), document);

        print();
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
