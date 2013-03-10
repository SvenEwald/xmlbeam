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
package org.xmlbeam.dom;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Every Projection will be automatically implementing this interface.
 * You may cast your Projection instances to this type and get
 * access to the DOM behind it.
 * 
 * You may even let your projection interfaces extend this interface
 * for a convenient access to the underlying DOM. 
 */
public interface DOMAccess extends Serializable {
    /**
     * Getter for the projection interface.
     * @return the projection interface of this projection.
     */
    Class<?> getProjectionInterface();

    /**
     * Getter for the underlying DOM node holding the data.
     * @return the projections DOM node. Could be Document or Element.
     */
    Node getDOMNode();

    /**
     * Getter for the XML Document owning the node for this projection.
     * If this projection node is a document, it is returned. 
     * @return the projections (parent) document.
     */
    Document getDOMOwnerDocument();

    /**
     * Getter for the "root" element of this projection.
     * 
     * @return the document root element if this is a projection or the base element if this is a
     *         subprojection.
     */
    Element getDOMBaseElement();
}