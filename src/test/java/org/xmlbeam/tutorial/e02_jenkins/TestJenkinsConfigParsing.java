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

import java.util.List;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tutorial.Tutorial;
import org.xmlbeam.tutorial.TutorialTestCase;
import org.xmlbeam.tutorial.e02_jenkins.model.Builder;
import org.xmlbeam.tutorial.e02_jenkins.model.Publisher;

/* START SNIPPET: TutorialDescription      
~~
 In the last example we demonstrated a sub projection pretending a non existing element.
 
 This time we like to define some model classes for a Jenkins job configuration and project existing elements to them.
 But there is one little hurdle: We can not know the exact XML structure, because Jenkins plugins contribute new elements with different names.
 Our model would have to include one class for each contributed element, but we like to keep the number of model classes low.
 
 The solution to this shows: 
  
 * Usage of XPath wildcards.
   Mapping of varying elements to one Java object. 
 
 * Automatic conversion of sequences to lists and arrays utilizing Java generics to provide a static typed API.
   
 * Declarative document origins.
   Just add a source URL via annotation, let the framework get the document.
   
 * Inheritance in projection interface.
   Java interface inheritance is still supported in projection interfaces.     

 []
  
 Instead of defining one model class for each element, we project all elements doing the same stuff to the same model object. 
 This is done by using XPath wildcards that will simply select all elements in a defined subtree. 
 Of course we define a getter method to give us the element name, so we can use our model to find out what which elements are really in there. 

 END SNIPPET: TutorialDescription
 */

@Category(Tutorial.class)
//START SNIPPET: JenknsCode
public class TestJenkinsConfigParsing extends TutorialTestCase {
    private JenkinsJobConfig config;

    @Before
    public void readJobConfig() throws IOException {
        config = new XBProjector().io().fromURLAnnotation(JenkinsJobConfig.class);
    }

    @Test
    public void testBuilderReading() {
        for (Builder builder: config.getAllBuilders()) {
            System.out.println("Builder: "+builder.getName()+" executes "+builder.getTargetsOrCommands() );
        }
    }

    @Test
    public void testPublishers() {
        List<Publisher> publishers = config.getPublishers();
        for (Publisher p : publishers) {
            System.out.println("Publisher:"+ p.getName() + " contributed by plugin "+p.getPlugin());
        }
    }

}
//END SNIPPET: JenknsCode