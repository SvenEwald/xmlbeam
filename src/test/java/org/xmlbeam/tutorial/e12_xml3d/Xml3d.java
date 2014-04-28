/**
 *  Copyright 2013 Sven Ewald
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
package org.xmlbeam.tutorial.e12_xml3d;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;
@SuppressWarnings("javadoc")
//START SNIPPET: Xml3d
@XBDocURL("resource://xml3d_template.html")
public interface Xml3d {

    @XBRead("/html/body/xml3d/mesh/int[@name='index']")
    String getIndexes();

    @XBWrite("/html/body/xml3d/mesh/int[@name='index']")
    Xml3d setIndexes(String indexes);

    @XBRead("/html/body/xml3d/mesh/float3[@name='position']")
    String getPositions();

    @XBWrite("/html/body/xml3d/mesh/float3[@name='position']")
    Xml3d setPositions(String positions);

    @XBRead("/html/body/xml3d/mesh/float3[@name='normal']")
    String getNormals();

    @XBWrite("/html/body/xml3d/mesh/float3[@name='normal']")
    Xml3d setNormals(String normals);
}
//END SNIPPET: Xml3d