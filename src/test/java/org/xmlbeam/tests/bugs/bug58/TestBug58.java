/**
 *  Copyright 2020 Sven Ewald
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
package org.xmlbeam.tests.bugs.bug58;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.junit.Test;
import org.xmlbeam.XBProjector;

public class TestBug58 {
    @SuppressWarnings("unused")
    @Test
    public void test() throws IOException {

        Charset UTF_8 = Charset.forName("UTF-8");
        String xml = IOUtilstoString(getClass().getResourceAsStream("/org/xmlbeam/tests/bugs/bug58/Konditionen_Bank.xml"), UTF_8);

        KfwKonditionsDokument konditionen = new XBProjector().projectXMLString(xml, KfwKonditionsDokument.class);

        assertEquals(1948, konditionen.getEintraege().size());

        KfwKonditionsDokument konditionen2 = new XBProjector().projectXMLString(xml, KfwKonditionsDokument.class); // fails since 1.4.15

    }

    /**
     * @param resourceAsStream
     * @param uTF_8
     * @return
     */
    private String IOUtilstoString(InputStream resourceAsStream, Charset uTF_8) {
        return new Scanner(resourceAsStream).useDelimiter("\\Z").next();
    }
}
