/**
 *  Copyright 2022 Sven Ewald
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
package org.xmlbeam.tests.bugs.bug62;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.Scanner;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.util.intern.ReflectionHelper;

/**
 * @author sven
 */
@SuppressWarnings({ "javadoc", "resource" })
public class TestBug62 {

	interface Test62 {}

	@BeforeClass
	public static void info() {
		System.out.println(ReflectionHelper.JAVA_VERSION);
	}

	@Test
	public void test() throws IOException {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
		projector.config().as(DefaultXMLFactoriesConfig.class).setPrettyPrinting(false);
        Test62 xml = projector.io().url("resource://xmlwithns.xml").read(Test62.class);
		System.out.println(xml.toString());

        String orig = new Scanner(TestBug62.class.getResourceAsStream("xmlwithns.xml")).useDelimiter("\\Z").next();
		assertEquals(orig, xml.toString());
	}

	@Test
	public void testPretty() throws IOException {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
		projector.config().as(DefaultXMLFactoriesConfig.class).setPrettyPrinting(true);
        Test62 xml = projector.io().url("resource://xmlwithns.xml").read(Test62.class);
		System.out.println(xml.toString());

        String orig = new Scanner(TestBug62.class.getResourceAsStream("xmlwithns.xml")).useDelimiter("\\Z").next();
		assertNotEquals(orig, xml.toString());
        assertEquals(orig, xml.toString().replaceAll("(?s)(  +)|(\n\\Z)", "").trim());
	}

}
