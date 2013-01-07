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
package org.xmlbeam.tutorial.e02_jenkins.model;

import org.xmlbeam.XBRead;

//START SNIPPET: JenkinsPublisherInterface
public interface Publisher extends ModelElement{

    /**
     * The Plugin name is located in an attribute of the configuration element.
     * @return The plugin name which contributed this element.
     */
    @XBRead("@plugin")
    String getPlugin();
    
}
//END SNIPPET: JenkinsPublisherInterface