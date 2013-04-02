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
package org.xmlbeam.tests.concurrent;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

/**
 *
 */
public class TestConcurrentProjectionAccess {

    private final static int count = 200;

    public interface Projection {

        @XBWrite("/a/b")
        Projection setSingleB(String b);

        @XBRead("/a/b")
        Projection getSingleB();

        @XBWrite("/a/x{0}/b")
        Projection setB(int index, String value);

        @XBRead(value = "/a/x{0}/b", targetComponentType = Projection.class)
        String getB(int index);

        @XBRead("count(//b)")
        int countB();

    }

    @Test
    public void testConcurrentProjectionAccess() throws InterruptedException {
        final Projection projection = new XBProjector(Flags.SYNCHRONIZE_ON_DOCUMENTS, Flags.TO_STRING_RENDERS_XML).projectEmptyDocument(Projection.class);
// final Projection projection = new
// XBProjector(Flags.TO_STRING_RENDERS_XML).projectEmptyDocument(Projection.class);
        final Projection b = projection.setSingleB("Huhu").getSingleB();
        assertEquals("<b>Huhu</b>", b.toString().trim());
        List<Thread> threads = new LinkedList<Thread>();
        for (int i = 0; i < count; ++i) {
            final int t = i;
            threads.add(new Thread() {
                {
                    setDaemon(true);
                    start();
                }

                @Override
                public void run() {
                    projection.setB(t, "Thread " + t);
                }
            });
        }
        for (Thread t : threads) {
            t.join();
        }
        // System.out.println(projection.toString());
        assertEquals(count + 1, projection.countB());
    }
}
