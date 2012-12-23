package org.xmlbeam.tutorial.e02_jenkins.model;

import org.xmlbeam.Xpath;

public interface Publisher {

	@Xpath("name()")
	String getName();
}
