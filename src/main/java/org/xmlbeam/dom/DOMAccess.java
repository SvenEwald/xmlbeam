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
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;

/**
 * Every Projection will be automatically implementing this interface. You can cast your Projection
 * instances to this type and get access to the DOM behind it. You can even let your projection
 * interfaces extend this interface for a convenient access to the underlying DOM.
 */
public interface DOMAccess extends Serializable {
    /**
     * Getter for the projection interface.
     *
     * @return the projection interface of this projection.
     */
    Class<?> getProjectionInterface();

    /**
     * Getter for the underlying DOM node holding the data.
     *
     * @return the projections DOM node. Could be Document or Element.
     */
    Node getDOMNode();

    /**
     * Getter for the XML Document owning the node for this projection. If this projection node is a
     * document, it is returned.
     *
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

    /**
     * This method may be used to get a string representation of the projected document or element.
     *
     * @return DOM node as XML string.
     */
    String asString();

    /**
     * Create an element or attribute with given path and value.
     *
     * @param path
     * @param value
     * @return this for convenience
     */
    @XBWrite("{0}")
    DOMAccess create(final String path, @XBValue Object value);

    /**
     * Returns true if the DOM was changed by any projection. This covers only changes done by
     * projections. If you manually change the DOM, you can use {@link #setModified(boolean)}.
     *
     * @return true if and only if any change operation was done.
     */
    boolean isModified();

    /**
     * Marks this projection as modified. This method will be called automatically when a write
     * operation changed the DOM.
     *
     * @param b
     */
    void setModified(boolean b);

}