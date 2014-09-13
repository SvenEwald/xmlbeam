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
package org.xmlbeam.tests.bugs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBRead;

/**
 * Testcase to cover bug report <a href="https://github.com/SvenEwald/xmlbeam/issues/18">#18</a>
 */
public class TestStartsWithXpathFunction {

    public interface Issue18 {
        @XBRead("starts-with('{0}','{1}')")
        boolean startsWith(String text, String prefix);
    }

    @Test
    public void testStartsWithXpathFunction() {
        Issue18 issue18 = new XBProjector().projectEmptyDocument(Issue18.class);

        assertTrue(issue18.startsWith("12345", "123"));
        assertFalse(issue18.startsWith("12345", "23"));
    }
}
