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

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.tutorial.e02_jenkins.model.Builder;
import org.xmlbeam.tutorial.e02_jenkins.model.Publisher;

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
@SuppressWarnings("javadoc")
//START SNIPPET: JenkinsJobConfigInterface
@org.xmlbeam.annotation.XBDocURL("resource://config.xml")
public interface JenkinsJobConfig {
    
    @XBRead("//publishers/*")
    List<Publisher> getPublishers();
    
    @XBRead("//prebuilders/* | //builders/* | //postbuilders/*")
    List<Builder> getAllBuilders();

}
//END SNIPPET: JenkinsJobConfigInterface