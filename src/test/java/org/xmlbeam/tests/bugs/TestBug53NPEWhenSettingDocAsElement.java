/**
 *  Copyright 2018 Sven Ewald
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
package org.xmlbeam.tests.bugs;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBWrite;

/**
 * @author Sven
 *
 */
public class TestBug53NPEWhenSettingDocAsElement {

    public interface Child {
        @XBWrite("ChildData")
        void setData(String data);
    }
    
    public interface ParentWithRootElement {
        @XBWrite("Root/ParentData")
        void setData(String data);
        
        @XBWrite("Root/Child")
        void setChild(Child child);
    }

    @Test
    public void testSetEmptyChildNonEmptyParent() {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
        
        ParentWithRootElement parent = projector.projectEmptyDocument(ParentWithRootElement.class);
        parent.setData("parent data...");

        Child child = projector.projectEmptyElement("Child",Child.class);
//        Child child = projector.projectEmptyDocument(Child.class);
//      child.setData(""); // Uncommenting fixes NPE error (assertion will fail of course, but that's fine)
        
        parent.setChild(child);
        
        /* Throws
        java.lang.NullPointerException
            at org.xmlbeam.ProjectionInvocationHandler$WriteInvocationHandler.invokeProjection(ProjectionInvocationHandler.java:659)
            at org.xmlbeam.ProjectionInvocationHandler$ProjectionMethodInvocationHandler.invoke(ProjectionInvocationHandler.java:206)
            at org.xmlbeam.ProjectionInvocationHandler.invoke(ProjectionInvocationHandler.java:879)
            at com.sun.proxy.$Proxy5.setChild(Unknown Source)
         */
        
//        assertEquals(
//                  "<Root>\n"
//                + "   <ParentData>parent data...</ParentData>\n"
//                + "</Root>",
//                parent.toString());
//        
        System.out.println("Result: " + parent);
    }
    
}
