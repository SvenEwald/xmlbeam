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
package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

import static org.junit.Assert.assertEquals;
import static org.xmlbeam.util.intern.duplexd.ExpressionType.ATTRIBUTE;
import static org.xmlbeam.util.intern.duplexd.ExpressionType.ELEMENT;
import static org.xmlbeam.util.intern.duplexd.ExpressionType.NODE;
import static org.xmlbeam.util.intern.duplexd.ExpressionType.VALUE;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xmlbeam.util.intern.duplexd.ExpressionType;
import org.xmlbeam.util.intern.duplexd.ExpressionTypeEvaluationVisitor;
import org.xmlbeam.util.intern.duplexd.SimpleNode;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.ParseException;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParser;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
@RunWith(Parameterized.class)
public class TestExpressionTypeDetection {

    private final ExpressionType expectedType;
    private final String xpath;

    public TestExpressionTypeDetection(final String xpath, final ExpressionType type) {
        this.xpath = xpath;
        this.expectedType = type;
    }

    @Parameters
    public static Collection<Object[]> tests() throws Exception {
        final Object[] params = new Object[] { //
        "/foo/bar", ELEMENT, //
                "/foo/foo2/@bar", ATTRIBUTE,//
                "name()", VALUE,//
                "root()", ELEMENT,//
                "//foo", ELEMENT,//
                "/bookstore/book/title | //price", ELEMENT,//
                "/bookstore/book/title | //@price", NODE,//
                "/bookstore/book/title | name()", VALUE,//
                "'String'", VALUE,//
                "1+2", VALUE,//
                "1<2", VALUE,//
                "price=9.80 or price=9.70", VALUE, //
                "5 mod 2", VALUE,//
                "/descendant::*", ELEMENT,//
                "/attribute::*", ATTRIBUTE,//
                "self::*", NODE,//
                "parent::*", ELEMENT,//
                "ancestor::*", ELEMENT,//
                "child::*/child::price", ELEMENT,//
                "child::node()", ELEMENT,//
                "//BBB[position() mod 2 = 0 ]", ELEMENT,//
                "self::node()", NODE,//
                ".", NODE,//
                "..", ELEMENT,//
                "/child::network/descendant-or-self::cname", ELEMENT,//
                "./@x", ATTRIBUTE,//
        };
        final List<Object[]> paramList = new LinkedList<Object[]>();
        for (int i = 0; i < params.length; i += 2) {
            paramList.add(Arrays.copyOfRange(params, i, i + 2));
        }
        return paramList;
    }

    @Test
    public void testXPathType() throws ParseException {
        XParser parser = new XParser(new StringReader(xpath));
        SimpleNode node = parser.START();
        System.out.println("-----------------------------------------");
        System.out.println(xpath);
        node.dump("");

        ExpressionType expressionType = node.firstChildAccept(new ExpressionTypeEvaluationVisitor(), null);
        assertEquals(expectedType, expressionType);
    }
}
