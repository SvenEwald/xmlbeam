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
package org.xmlbeam;

import javax.xml.xpath.XPathExpressionException;

import org.xmlbeam.exceptions.XBException;
import org.xmlbeam.intern.DOMChangeListener;

/**
 *
 */
abstract class DomChangeTracker implements DOMChangeListener {

    private boolean needRefresh = true;

    @Override
    public void domChanged() {
        needRefresh = true;
    }

    void refreshForReadIfNeeded() {
        if (needRefresh) {
            invokeRefresh(false);
        }
    }

    abstract void refresh(boolean b) throws XPathExpressionException;

    void refreshForWriteIfNeeded() {
        if (needRefresh) {
            invokeRefresh(true);
        }
    }

    private void invokeRefresh(boolean b) {
        try {
            refresh(b);
            needRefresh = false;
        } catch (XPathExpressionException e) {
            needRefresh = true;
            throw new XBException("Unexpected error during evaluation.", e);
        }
    }
}
