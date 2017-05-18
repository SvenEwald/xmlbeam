/**
 *  Copyright 2016 Sven Ewald
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
package org.xmlbeam.tutorial.e27_cdlib;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;
import org.xmlbeam.tutorial.TutorialTestCase;
import org.xmlbeam.types.XBAutoList;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestAutoCDList extends TutorialTestCase {

    interface CDLib {
        interface CD {
            @XBWrite("./TITLE")
            CD title(String title);

            @XBWrite("./ARTIST")
            CD artist(String artist);

            @XBWrite("./COUNTRY")
            CD country(String country);

            @XBWrite("./COMPANY")
            CD company(String company);

            @XBWrite("./PRICE")
            CD price(float price);

            @XBWrite("./YEAR")
            CD year(int year);
        }

        @XBRead("/CATALOG/CD")
        XBAutoList<CD> list();

    }

    @Test
    public void testAddCDToList() throws IOException {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
        CDLib cdlib = projector.io().url("res://cdlib.xml").read(CDLib.class);
        CDLib.CD cd = projector.projectEmptyElement("CD", CDLib.CD.class);
        cdlib.list().add(cd.title("title").artist("artist").price(15.99f).year(2015));
        System.out.println(cdlib.toString());
    }
}
