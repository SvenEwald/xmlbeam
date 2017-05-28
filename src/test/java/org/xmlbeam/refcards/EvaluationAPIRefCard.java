/**
 *  Copyright 2016 Sven Ewald
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
package org.xmlbeam.refcards;

import java.util.Date;
import java.util.List;
import java.util.Map;

import java.io.IOException;

import org.xmlbeam.XBProjector;
import org.xmlbeam.types.XBAutoMap;

@SuppressWarnings({ "javadoc", "unused" })
public class EvaluationAPIRefCard {

    {
        if (false) {
            try {
        //START SNIPPET: EvaluationAPIRefCardExample
        XBProjector projector =new XBProjector();

        // Read a single String value from file 'filename.xml'
        String value = projector.io().file("filename.xml").evalXPath("/some/path").as(String.class);

        // Read a sequence of integer values
        List<Integer> entries = projector.io().file("filename.xml").evalXPath("/some/list/entry").asListOf(Integer.class);

        // Read xml from url to a map
        XBAutoMap<String> map = projector.io().url("http://url").asMapOf(String.class);

        String string = map.get("/some/path"); // returns value at path
        int intValue = map.get("/other/path",Integer.class); // returns other value converted to int.
        Date date = map.get("/date/path using YYYYMMDD",Date.class); // returns other value converted to Date using a format pattern.

        //Create document from scratch
        Map<String,Object> doc = projector.autoMapEmptyDocument(Object.class);
        doc.put("/path/to/value", "value");
        doc.put("/path/to/floatValue", 15.0f);
        projector.io().file("example.xml").write(doc);

        //END SNIPPET: EvaluationAPIRefCardExample
            } catch (IOException e) {
                throw new IllegalStateException("This code is not to be executed");
            }
        }
    }

}
