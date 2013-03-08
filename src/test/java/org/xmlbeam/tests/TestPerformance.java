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
package org.xmlbeam.tests;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

/**
 */
public class TestPerformance {

    public interface PerformanceProjection {

        @XBWrite("/a/b/c/d/e")
        void setElemetns(List<Integer>number);
        
        @XBRead(value="/a/b/c/d/e",targetComponentType=Integer.class)
        List<Integer> getElements();
    }

    private PerformanceProjection projection;
    
    @Test
    public void createXMLDoc() throws IOException {
        projection = new XBProjector().projectEmptyDocument(PerformanceProjection.class);
        for (int i=0; i<100;++i) {
            List<Integer> elements = projection.getElements();
            elements.add(i);
            projection.setElemetns(elements);
        }
        System.out.println(projection.toString());
    }
}
