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

import org.xmlbeam.DocumentURL;
import org.xmlbeam.XPathProjection;

/**
 * This example demonstrates the reuse of a sub projection. This is possible
 * because an artifact and its dependencies share the same structure.
 * 
 * A simple setter is defined to modify a maven project name.
 * 
 */
@DocumentURL("resource://pom.xml")
public interface MavenPOM {

    @XPathProjection("/project/name")
    String getName();

    @XPathProjection("/project/name")
    void setName(String name);

    public interface Artifact {

        @XPathProjection("child::groupId")
        String getGroupId();

        @XPathProjection("child::artifactId")
        String getArtifactId();

        @XPathProjection("child::version")
        String getVersion();

    }

    @XPathProjection("/project")
    Artifact getProjectId();

    @XPathProjection("/project/depencencies/dependency")
    Artifact[] getDependencies();

}
