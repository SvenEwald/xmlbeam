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
package org.xmlbeam.tests.demo;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;

@SuppressWarnings("javadoc")
public class TestNodeContains {

    @XBDocURL("resource://book.xml")
    public interface Books {

        @XBRead("//verse[contains(text(),'{0}')]")
        String lookup(String string);

    }

    @Test
    public void testXpathContains() throws IOException {
        Books books = new XBProjector().io().fromURLAnnotation(Books.class);
        assertEquals("Some texts are here. This text is gonna be long paragraph word", books.lookup("word"));

    }
}
