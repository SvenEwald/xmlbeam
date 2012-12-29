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
package org.xmlbeam.tutorial.e02_jenkins;

import static org.junit.Assert.assertEquals;

import java.util.List;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xmlbeam.XMLProjector;
import org.xmlbeam.tutorial.Tutorial;
import org.xmlbeam.tutorial.e02_jenkins.model.Builder;
import org.xmlbeam.tutorial.e02_jenkins.model.Publisher;

@Category(Tutorial.class)
//START SNIPPET: JenknsCode
public class TestJenkinsConfigParsing {
	private JenkinsJobConfig config;

	@Before
	public void readJobConfig() throws IOException {
		config = new XMLProjector().readFromURIAnnotation(JenkinsJobConfig.class);
	}

	@Test
	public void testBuilderReading() {
		for (Builder builder: config.getBuilders()) {
		    System.out.println("Builder:"+builder.getName());
		}
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
//START SNIPPET: JenknsCode