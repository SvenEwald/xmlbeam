/**
 *  Copyright 2013 Sven Ewald
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
package org.xmlbeam.tests.externalizer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;
import org.xmlbeam.externalizer.PropertyFileExternalizer;

/**
 *
 */
@SuppressWarnings("javadoc")
public class TestPropertyExternalizer {

    private File propFile = new File("test.properties");

    @After
    public void removeFile() {
        propFile.deleteOnExit();
        propFile.delete();
    }

    @Test
    public void testExternalizer() throws SecurityException, NoSuchMethodException, IOException {
        createPropFile("org.xmlbeam.tests.externalizer.TestPropertyExternalizer.testExternalizer");
        validate();
    }

    @Test
    public void testExternalizerB() throws SecurityException, NoSuchMethodException, IOException {
        createPropFile("TestPropertyExternalizer.testExternalizer");
        validate();
    }

    @Test
    public void testExternalizerC() throws SecurityException, NoSuchMethodException, IOException {
        createPropFile("testExternalizer");
        validate();
    }

    @Test
    public void testExternalizerD() throws SecurityException, NoSuchMethodException, IOException {
        createPropFile("key");
        validate();
    }

    private void validate() throws NoSuchMethodException {
        PropertyFileExternalizer externalizer = new PropertyFileExternalizer(propFile);
        String resolveURL = externalizer.resolveURL("key", TestPropertyExternalizer.class.getMethod("testExternalizer", (Class<?>[]) null), null);
        assertEquals("Huhu", resolveURL);
    }

    private void createPropFile(String name) throws IOException {
        FileOutputStream stream = new FileOutputStream(propFile);
        stream.write((name + "=Huhu\n").getBytes("ISO8859-1"));
        stream.close();
    }
}
