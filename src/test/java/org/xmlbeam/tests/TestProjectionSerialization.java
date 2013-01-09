/**
 *  Copyright 2012 Sven Ewald
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
package org.xmlbeam.tests;

import static org.junit.Assert.assertNotSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlbeam.XBProjector;

/**
 * Tests to ensure that projections can be serialized.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class TestProjectionSerialization {

    @BeforeClass
    public static void setExtendedDebugInfo() {
        System.setProperty("sun.io.serialization.extendedDebugInfo", "true");
    }

    public interface SerializeMe {

    }

    @Test
    public void testEmptyProjectionSerialization() throws IOException, ClassNotFoundException {
        SerializeMe projection = new XBProjector().createEmptyDocumentProjection(SerializeMe.class);
        SerializeMe squishedProjection = cloneBySerialization(projection);
        assertNotSame(projection, squishedProjection);
    }

    private <T> T cloneBySerialization(T object, Class<T>... clazz) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return (T) objectInputStream.readObject();
    }

}
