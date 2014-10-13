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
package org.xmlbeam.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestParameterUnwrapping {

    public interface Projection {
        @XBRead("'{0}'")
        String getSomeValue(Callable<String> param);

        @XBWrite("/foo/bar")
        Projection setSomeValues(Collection<? extends Callable<String>> param);

        @XBRead("/foo/bar")
        String[] checkValues();
    }

    @Test
    public void testUnWrappCallable() throws Exception {
        Callable<String> callable = new Callable<String>() {

            @Override
            public String call() throws Exception {
                return "Nothing but a String";
            }
        };

        String result = new XBProjector().projectEmptyDocument(Projection.class).getSomeValue(callable);
        assertEquals(callable.call(), result);
    }

    @SuppressWarnings("unchecked")
    @Ignore
    // Nobody wants this, maybe we support this later on demand.
    public void testUnwrappInCollection() {
        List<Callable<String>> collection = Arrays.asList(new Callable<String>() {

            @Override
            public String call() throws Exception {
                return "A";
            }
        }, new Callable<String>() {

            @Override
            public String call() throws Exception {
                return "B";
            }
        }, new Callable<String>() {

            @Override
            public String call() throws Exception {
                return "C";
            }
        });

        Projection projection = new XBProjector(Flags.TO_STRING_RENDERS_XML).projectEmptyDocument(Projection.class);
        final String[] result = projection.setSomeValues(collection).checkValues();
        assertEquals("A", result[0]);
        assertEquals("B", result[1]);
        assertEquals("C", result[2]);
    }
}
