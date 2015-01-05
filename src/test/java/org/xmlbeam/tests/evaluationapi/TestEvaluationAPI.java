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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.dom.DOMAccess;

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

        //new XBProjector().onXMLString("asfafd").evalXPath("//foo").as(Projection.class);
        //new XBProjector().onXMLString("sdfsf").createProjection(Foo.class);

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
    public void testSimpleEvaluateOnXMLString() {
        String stringResult = new XBProjector().onXMLString("<foo><bar>value</bar></foo>").evalXPath("//bar").as(String.class);
        assertEquals("value", stringResult);

        int intResult = new XBProjector().onXMLString("<foo><bar>13</bar></foo>").evalXPath("//bar").as(Integer.TYPE);
        assertEquals(13, intResult);

        Integer integerResult = new XBProjector().onXMLString("<foo><bar>-113</bar></foo>").evalXPath("//bar").as(Integer.TYPE);
        assertEquals(-113, integerResult.intValue());

        boolean boolResult = new XBProjector().onXMLString("<foo><bar>true</bar></foo>").evalXPath("//bar").as(Boolean.TYPE);
        assertTrue(boolResult);

        Date date = new XBProjector().onXMLString("<foo><bar>1.4.2004</bar></foo> ").evalXPath("//bar using dd.MM.yyyy").as(Date.class);
        assertEquals("04", new SimpleDateFormat("MM").format(date));
    }

    public interface Projection extends DOMAccess {
    };

    @Test
    public void testProjectionCreation() {
        Projection projection = new XBProjector().onXMLString("<foo><bar>value</bar></foo>").evalXPath("//bar").as(Projection.class);
        assertEquals("<bar>value</bar>", projection.asString().trim());
    }
    
    @Test
    public void testMultiEvaluationOnXMLString() {
        List<String> strings  = new XBProjector().onXMLString("<foo><bar>value1</bar><bar>value2</bar></foo>").evalXPath("//bar").asListOf(String.class);
        assertEquals(Arrays.asList("value1","value2"), strings);
    }
    @Test
    public void testMultiEvaluationOnXMLString2() {
        String[] strings  = new XBProjector().onXMLString("<foo><bar>value1</bar><bar>value2</bar></foo>").evalXPath("//bar").asArrayOf(String.class);
        assertEquals(new String[]{"value1","value2"}, strings);
    }
}
