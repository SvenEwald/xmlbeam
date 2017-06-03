/**
 *  Copyright 2017 Sven Ewald
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
package org.xmlbeam.tests.util.intern;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.xmlbeam.util.UnionIterator;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestUnionIterator {

    @Test
    public void testUnionIterator() {
        execTest(new Integer[] { 1, 2, 3, 4, 5 }, new Integer[] { 6, 7, 8, 9, 0 });
    }

    @Test
    public void testCornerCase1() {
        execTest(new Integer[] {}, new Integer[] { 6, 7, 8, 9, 0 });
    }

    @Test
    public void testCornerCase2() {
        execTest(new Integer[] { 6, 7, 8, 9, 0 }, new Integer[] {});
    }

    @Test
    public void testCornerCase3() {
        execTest(new Integer[] {}, new Integer[] {});
    }

    static private <T> void execTest(final T[] a, final T[] b) {
        List<T> firstList = Arrays.asList(a);
        List<T> secondList = Arrays.asList(b);
        List<T> unionList = new LinkedList<T>(firstList);
        unionList.addAll(secondList);
        UnionIterator<T> unionIterator = new UnionIterator<T>(firstList.iterator(), secondList.iterator());
        List<T> resultList = new LinkedList<T>();
        for (; unionIterator.hasNext();) {
            resultList.add(unionIterator.next());
        }
        assertEquals(resultList, unionList);
    }

}
