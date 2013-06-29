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
package org.xmlbeam.tutorial.e04_maven;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tutorial.Tutorial;
import org.xmlbeam.tutorial.TutorialTestCase;
import org.xmlbeam.tutorial.e04_maven.MavenPOM.Artifact;

//START SNIPPET: Tutorial4
@Category(Tutorial.class)
public class TestMavenPOM extends TutorialTestCase {

/* START SNIPPET: TutorialDescription
~~     
 A Maven project has a group id, artifact id and a version.
 So does a Maven dependency.
 Now we reuse the same sub projection for unrelated, but similar parts of the document.
 This is possible because the structure of our projection does not need to follow the structure of the document.

 Second we define a simple setter in the projection interface to show how element values can be modified.  
END SNIPPET: TutorialDescription */
    
    @Test
    public void testProjectNameWriting() throws SAXException, IOException, ParserConfigurationException {
//START SNIPPET: TestMavenPOM
MavenPOM pom = new XBProjector().io().fromURLAnnotation(MavenPOM.class);
pom.setName("New name");
for (Artifact artifact:pom.getDependencies()) {
    if (artifact.equals(pom.getProjectId())) {
        System.out.println("Hmm... your project depends on itself!");
    }
}
//END SNIPPET: TestMavenPOM        
    }
}
