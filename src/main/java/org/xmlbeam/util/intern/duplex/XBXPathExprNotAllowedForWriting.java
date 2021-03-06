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
package org.xmlbeam.util.intern.duplex;

/**
 * Exception for the case that a xpath was declared in @XBWrite that is not allowed for writing.
 */
public class XBXPathExprNotAllowedForWriting extends XBPathParsingException { // NO_UCD (use default)

    private static final long serialVersionUID = 8944815612805655746L;

    /**
     * Constructor.
     *
     * @param node
     * @param reason
     */
    public XBXPathExprNotAllowedForWriting(final SimpleNode node, final String reason) { // NO_UCD (use default)
        super(reason + ": Node " + node.toString() + " is not supported for writing expressions", node.beginLine, node.beginColumn, node.endColumn, node.endLine);
        //node.parser.token_source.input_stream.inputStream.reset().
    }
}
