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

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tutorial.TutorialTestCase;

public class TestWikiAccess extends TutorialTestCase{

    final private String[] PROGRAMMING_LANGUAGES = new String[] { "Java", "C++", "C", "Scala" };
    
    @Test
    public void wikiIt() throws IOException {
        for (String name : PROGRAMMING_LANGUAGES) {
            ProgrammingLanguage lang = new XBProjector().io().fromURLAnnotation(ProgrammingLanguage.class,name);    
            System.out.println(lang.getCreator() + " designed " + lang.getName());
         }
    }
}
