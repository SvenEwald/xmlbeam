package org.xmlbeam.tutorial.e02_jenkins;
import java.util.List;

import org.xmlbeam.Xpath;
import org.xmlbeam.tutorial.e02_jenkins.model.Builder;
import org.xmlbeam.tutorial.e02_jenkins.model.Publisher;
import org.xmlbeam.tutorial.e02_jenkins.model.SCM;
import org.xmlbeam.tutorial.e02_jenkins.model.Trigger;

/**
 * This example demonstrates a more advanced capability of projections: A
 * projection to a not defined element by xpath wildcards. There are several
 * types of builders possible in a Jenkins configuration. A static binding would
 * lead to quite a number of builder type classes and would break if a new
 * builder would be introduced. Further more we have automatic conversion of
 * sequences to lists and arrays. Notice that you need to specify the generic
 * component type for Lists in contrast to arrays. This is because type erasure
 * hits here.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 * 
 */
@org.xmlbeam.URI("resource://config.xml")
public interface JenkinsJobConfig {

    @Xpath( "/project/description")
    String getDescription();

	@Xpath("//hudson.security.AuthorizationMatrixProperty/permission")
    String[] getPermissions();

	@Xpath("/project/scm")
	List<SCM> getSCMs();

	@Xpath(value = "/projects/triggers/*", targetComponentType = Trigger.class)
	List<Trigger> getTriggers();

	@Xpath(value = "/project/builders/*", targetComponentType = Builder.class)
	List<Builder> getBuilders();

	@Xpath(value = "/project/publishers/*", targetComponentType = Publisher.class)
	List<Publisher> getPublishers();

}
