package org.xmlbeam.tests;

import static org.junit.Assert.assertNotSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.xmlbeam.XMLProjector;

/**
 * Tests to ensure that projections can be serialized.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class TestProjectionSerialization {

	public interface SerializeMe {

	}

	@Test
	public void testEmptyProjectionSerialization() throws IOException, ClassNotFoundException {
		SerializeMe projection = new XMLProjector().createEmptyDocumentProjection(SerializeMe.class);
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
