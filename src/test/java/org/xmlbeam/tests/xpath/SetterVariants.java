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
package org.xmlbeam.tests.xpath;

import java.util.Collection;

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

/**
 *
 */
@SuppressWarnings("javadoc")
public interface SetterVariants {

    public interface SubProjection {
        @XBRead(".")
        int getValue();

        @XBWrite(".")
        SubProjection setValue(int value);
    }

    /**
     * Create <a><b>...</b><a> "Create or update the content of one element named b."
     */
    @XBWrite("/a/b")
    void setSingleElementContent(int content);

    /**
     * Create <a></a> for empty array, else <a><b>...</b><b>...</b><b>...<b></a>
     */
    @XBWrite("/a/b")
    void setMultipleElementContent(int[] content);

    /**
     * Create <a></a> for empty collection, else <a><b>...</b><b>...</b><b>...<b></a>
     */
    @XBWrite("/a/b")
    void setMultipleElementContent(Collection<Integer> content);

    @XBWrite("/a/@att")
    void setRootAttribute(String att);

    @XBWrite("/a/b/c/@att")
    void setDeeperAttribute(String att);

    /**
     * Create or replace all existing /a[1]/b[1]/c Create <a><b><c></c></b></a>.
     */
    @XBWrite("/a/b/c")
    void setSingleSubProjection(SubProjection content);

    /**
     *
     */
    @XBWrite("/a/b/c")
    void setMultipleSubProjectionArray(SubProjection[] content);

    @XBWrite("/a/b/c")
    void setMultipleSubProjectionCollection(Iterable<SubProjection> content);

    @XBWrite("/a/b/*")
    void setMultipleSubProjectionWildcardCollection(Iterable<SubProjection> content);

    @XBWrite("/a/b/d")
    void setMultipleSubProjectionRenamingCollection(Iterable<SubProjection> content);

}
