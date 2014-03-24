/**
 *  Copyright 2014 Sven Ewald
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
package org.xmlbeam.tutorial.e16_mondial;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.tutorial.TutorialTestCase;

/**
 * @author sven
 */
public class TestMondialAccess extends TutorialTestCase {

    @XBDocURL("http://www.dbis.informatik.uni-goettingen.de/Mondial/mondial.xml")
    public interface Mondial {

        @XBRead("/mondial/country/name()")
        List<String> getSubs();

        @XBRead("count(//*)")
        int getNodeCount();
    }

    @Test
    public void testStructure() throws IOException {
        final long start = System.currentTimeMillis();
        final Mondial mondial = new XBProjector().io().fromURLAnnotation(Mondial.class);
        System.out.println(mondial.getNodeCount());
        //   System.out.println(new HashSet<String>(mondial.getSubs()));
        final long end = System.currentTimeMillis();
        System.out.println("Test run:" + (end - start) + "ms");
        System.out.println(mondial.getNodeCount());
    }

}
