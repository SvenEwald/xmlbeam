/**
 *  Copyright 2017 Sven Ewald
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
package org.xmlbeam.tests.bugs.bug39;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

@SuppressWarnings("javadoc")
public class NestedPathInPredicateTest {

    public interface NestedPathInPredicate {

        @XBWrite("Root[X/Y='Passed']/Child")
        void setConditionNestedWithChild(String value);

        @XBWrite("Root/Intermediate[X='Passed']/Child")
        void setConditionSingleLevelOnIntermediateWithChild(String value);

        @XBWrite("Root[X='Passed']/Child")
        void setConditionSingleLevelOnRootWithChild(String value);

        @XBWrite("Root/Intermediate[X/Y='Passed']")
        void setConditionNestedNoChild(String value);

        @XBWrite("Root/Intermediate[X='Passed']")
        void setConditionSingleLevelOnIntermediateNoChild(String value);

        @XBWrite("Root[X='Passed']")
        void setConditionSingleLevelOnRootNoChild(String value);

        @XBWrite("someroot/elements[with/subelement='oink']/element3")
        void setPathInPredicate2(String value);

        @XBRead("someroot/elements[with/subelement='oink']/element3")
        String getPathInPredicate2();
    }

    @Test
    public void testConditionNestedWithChild() {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

        NestedPathInPredicate test = projector.projectEmptyDocument(NestedPathInPredicate.class);
        test.setConditionNestedWithChild("Value");

        System.out.print(test);

        assertEquals(("<Root>\n" + "   <X>\n" + "      <Y>Passed</Y>\n" + "   </X>\n" + "   <Child>Value</Child>\n" + "</Root>\n").replaceAll("\\s", ""), test.toString().replaceAll("\\s", ""));

        // Fails: XBXPathExprNotAllowedForWriting: Not expetced here.: Node PathExpr is not supported for writing expressions in line 1 col 18 to 21

    }

    @Test
    public void testConditionSingleLevelOnIntermediateWithChild() {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

        NestedPathInPredicate test = projector.projectEmptyDocument(NestedPathInPredicate.class);
        test.setConditionSingleLevelOnIntermediateWithChild("Value");

        System.out.print(test);

        assertEquals(("<Root>\n" + "<Intermediate>\n" + "<X>Passed</X>\n" + "<Child>Value</Child>\n" + "</Intermediate>\n" + "</Root>\n").replaceAll("\\s", ""), test.toString().replaceAll("\\s", ""));

        // Passes: Creates all elements
    }

    @Test
    public void testConditionSingleLevelOnRootWithChild() {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

        NestedPathInPredicate test = projector.projectEmptyDocument(NestedPathInPredicate.class);
        test.setConditionSingleLevelOnRootWithChild("Value");

        System.out.print(test);

        assertEquals(("<Root>\n" + "   <X>Passed</X>\n" + "   <Child>Value</Child>\n" + "</Root>\n").replaceAll("\\s", ""), test.toString().replaceAll("\\s", ""));

        // Passes: Creates all elements
    }

    @Test
    public void testConditionNestedNoChild() {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

        NestedPathInPredicate test = projector.projectEmptyDocument(NestedPathInPredicate.class);
        test.setConditionNestedNoChild("Value");

        System.out.print(test);

        assertEquals(("<Root>\n" + "   <Intermediate>\n" + "      <X>\n" + "         <Y>Passed</Y>\n" + "      </X>\n" + "      Value\n" + "   </Intermediate>\n" + "</Root>\n").replaceAll("\\s", ""), test.toString().replaceAll("\\s", ""));

        // Fails: XBXPathExprNotAllowedForWriting: Not expetced here.: Node PathExpr is not supported for writing expressions in line 1 col 18 to 21
    }

    @Test
    public void testConditionSingleLevelOnIntermediateNoChild() {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

        NestedPathInPredicate test = projector.projectEmptyDocument(NestedPathInPredicate.class);
        test.setConditionSingleLevelOnIntermediateNoChild("Value");

        System.out.print(test);

        assertEquals(("<Root>\n" + "   <Intermediate>\n" + "      <X>Passed</X>\n" + "      Value\n" + "   </Intermediate>\n" + "</Root>\n").replaceAll("\\s", ""), test.toString().replaceAll("\\s", ""));

        // Fails: Doesn't create <X>Passed</X> element
    }

    @Test
    public void testConditionSingleLevelOnRootNoChild() {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

        NestedPathInPredicate test = projector.projectEmptyDocument(NestedPathInPredicate.class);
        test.setConditionSingleLevelOnRootNoChild("Value");

        System.out.print(test);

        assertEquals(("<Root>\n" + "   <X>Passed</X>\n" + "   Value\n" + "</Root>\n").replaceAll("\\s", ""), test.toString().replaceAll("\\s", ""));

        // Fails: Doesn't create <X>Passed</X> element
    }

    @Test
    public void testConditionNestedNoChild2() {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

        NestedPathInPredicate test = projector.projectEmptyDocument(NestedPathInPredicate.class);
        test.setPathInPredicate2("Value");

        assertEquals("Value", test.getPathInPredicate2());

        // System.out.print(test);

        assertEquals(("<someroot>\n" + "  <elements>\n" + "    <with>\n" + "      <subelement>oink</subelement>\n" + "    </with>\n" + "    <element3>Value</element3>\n" + "  </elements>\n" + "</someroot>").replaceAll("\\s", ""), test.toString().replaceAll("\\s", ""));

    }

}
