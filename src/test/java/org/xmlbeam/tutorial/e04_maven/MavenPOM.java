package org.xmlbeam.tutorial.e04_maven;

import org.xmlbeam.URI;
import org.xmlbeam.Xpath;

/**
 * This example demonstrates the reuse of a sub projection. This is possible
 * because an artifact and its dependencies share the same structure.
 * 
 * A simple setter is defined to modify a maven project name.
 * 
 */
@URI("resource://pom.xml")
public interface MavenPOM {

	@Xpath("/project/name")
	String getName();

	@Xpath("/project/name")
	void setName(String name);

	public interface Artifact {

		@Xpath("child::groupId")
		String getGroupId();

		@Xpath("child::artifactId")
		String getArtifactId();

		@Xpath("child::version")
		String getVersion();

	}

	@Xpath("/project")
	Artifact getProjectId();

	@Xpath("/project/depencencies/dependency")
	Artifact[] getDependencies();

}
