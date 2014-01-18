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
package org.xmlbeam.tutorial.e10_wikipedia;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;

/**
 *
 */
//START SNIPPET:ProgrammingLanguage
@XBDocURL("http://en.wikipedia.org/wiki/{0}_(programming_language)")
public interface ProgrammingLanguage {

    @XBRead("//b[1]")
    String getName();

    @XBRead("normalize-space(//td[../th = \"Designed by\"])")
    String getCreator();

}
//END SNIPPET:ProgrammingLanguage
