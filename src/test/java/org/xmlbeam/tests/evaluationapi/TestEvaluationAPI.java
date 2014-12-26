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
package org.xmlbeam.tests.evaluationapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlbeam.XBProjector;

/**
 * @author sven
 */
public class TestEvaluationAPI {

    @Ignore
    public void testAPI() throws IOException {
//        List<Integer> li = new XBProjector().evaluateXMLString("<xml><foo>123</foo></xml>", "/xml/foo").asList().ofIntegers();
//        int[] ia = new XBProjector().evaluateXMLString("<xml><foo>123</foo></xml>", "/xml/foo").asArray().ofInts();
//        Integer[] iia = new XBProjector().evaluateXMLString("<xml><foo>123</foo></xml>", "/xml/foo").asArray().ofIntegers();
//        int i = new XBProjector().evaluateXMLString("<xml><foo>123</foo></xml>", "/xml/foo").asInt();
//        assertEquals(Integer.valueOf(123), i);
//
//        new XBProjector().io().stream(stream).read(Agent.class);

        String as = new XBProjector().io().file(new File("")).evalXPath("//foo").as(String.class);
        List<String> asListOf = new XBProjector().io().file(new File("")).evalXPath("//foo").asListOf(String.class);
        String[] asArrayOf = new XBProjector().io().file(new File("")).evalXPath("//foo").asArrayOf(String.class);
        String url2String = new XBProjector().io().url("res://test.xml").evalXPath("//foo").as(String.class);
        as.toString();
        asListOf.toString();
        asArrayOf.toString();
        url2String.toString();
    }

    @Test
    public void testEvaluateOnXMLString() {
        String stringResult = new XBProjector().evalXPathOnXMLString("//bar", "<foo><bar>value</bar></foo>").as(String.class);
        assertEquals("value", stringResult);

        int intResult = new XBProjector().evalXPathOnXMLString("//bar", "<foo><bar>13</bar></foo>").as(Integer.TYPE);
        assertEquals(13, intResult);

        Integer integerResult = new XBProjector().evalXPathOnXMLString("//bar", "<foo><bar>-113</bar></foo>").as(Integer.TYPE);
        assertEquals(-113, integerResult.intValue());

        boolean boolResult = new XBProjector().evalXPathOnXMLString("//bar", "<foo><bar>true</bar></foo>").as(Boolean.TYPE);
        assertTrue(boolResult);

        Date date = new XBProjector().evalXPathOnXMLString("//bar using dd.MM.yyyy", "<foo><bar>1.4.2004</bar></foo> ").as(Date.class);
        assertEquals("04", new SimpleDateFormat("MM").format(date));
    }
}
