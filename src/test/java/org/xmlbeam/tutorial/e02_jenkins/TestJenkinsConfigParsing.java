package org.xmlbeam.tutorial.e02_jenkins;

import static org.junit.Assert.assertEquals;

import java.util.List;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlbeam.XMLProjector;

public class TestJenkinsConfigParsing {

    @Test
    public void testBuilderReading() throws SAXException, IOException, ParserConfigurationException {
		JenkinsJobConfig config = new XMLProjector().readFromURIAnnotation(JenkinsJobConfig.class);
        List<Builder> builders = config.getBuilders();
        assertEquals(1, builders.size());
        assertEquals("-Xmx1536m -Xms512m -XX:MaxPermSize=1024m", builders.get(0).getJVMOptions());
    }

    @Test
    public void testPermissions() throws Exception {
		JenkinsJobConfig config = new XMLProjector().readFromURIAnnotation(JenkinsJobConfig.class);
        assertEquals(8, config.getPermissions().length);
        assertEquals("hudson.model.Run.Delete:vjuranek",config.getPermissions()[3]);
    }
}
