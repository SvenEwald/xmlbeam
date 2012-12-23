package org.xmlbeam.tutorial.e02_jenkins.model;

import org.xmlbeam.Xpath;

public interface Builder {
	@Xpath("child::jvmOptions")
    String getJVMOptions();

	@Xpath("child::properties")
	String getProperties();

	@Xpath("child:targets")
	String getTargets();
}
