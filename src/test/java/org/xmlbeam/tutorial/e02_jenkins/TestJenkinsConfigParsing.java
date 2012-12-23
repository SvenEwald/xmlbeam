package org.xmlbeam.tutorial.e02_jenkins;

import static org.junit.Assert.assertEquals;

import java.util.List;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.xmlbeam.XMLProjector;
import org.xmlbeam.tutorial.e02_jenkins.model.Builder;
import org.xmlbeam.tutorial.e02_jenkins.model.Publisher;

public class TestJenkinsConfigParsing {
	private JenkinsJobConfig config;

	@Before
	public void readJobConfig() throws IOException {
		config = new XMLProjector().readFromURIAnnotation(JenkinsJobConfig.class);
	}

	@Test
	public void testBuilderReading() {
		List<Builder> builders = config.getBuilders();
		assertEquals(1, builders.size());
		assertEquals("-Xmx1536m -Xms512m -XX:MaxPermSize=1024m", builders.get(0).getJVMOptions());
	}

	@Test
	public void testPermissions() {
		assertEquals(8, config.getPermissions().length);
		assertEquals("hudson.model.Run.Delete:vjuranek", config.getPermissions()[3]);
	}

	@Test
	public void testPublishers() {
		List<Publisher> publishers = config.getPublishers();
		for (Publisher p : publishers) {
			System.out.println(p.getName() + ":\n" + p);
		}
	}

}
