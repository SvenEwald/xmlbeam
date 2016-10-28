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
package org.xmlbeam.tests.projectionvalidation;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBDelete;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;

/**
 *
 */
@SuppressWarnings("javadoc")
public class TestProjectionValidation {

    public interface A {
        @XBRead
        void m();
    }

    public interface B {
        @XBWrite
        void m();
    }

    private interface C {
    }

    public interface D {
        @XBRead
        @XBWrite
        void m();
    }

    public interface E {
        @XBRead
        @XBDelete
        void m();
    }

    public interface F {
        @XBWrite
        @XBDelete
        void m();
    }

    public interface G {
        @XBRead
        void m(@XBValue String param);
    }

    public interface H {
        @XBWrite
        void m(@XBValue String param, int b, @XBValue String param2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullInterface() {
        project(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonInterface() {
        project(Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnnotationInterface() {
        project(XBRead.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadAnnotationOnVoidMethod() {
        project(A.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteAnnotationOnVoidMethod() {
        project(B.class);
    }

    @Test
    public void testPrivateInterface() {
        project(C.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultipleAnnotationsD() {
        project(D.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultipleAnnotationsE() {
        project(E.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultipleAnnotationsF() {
        project(F.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleAnnotationsG() {
        project(G.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleAnnotationsH() {
        project(H.class);
    }    

    private void project(Class<?> projectionInterface) {
        new XBProjector().projectEmptyDocument(projectionInterface);
    }

}
