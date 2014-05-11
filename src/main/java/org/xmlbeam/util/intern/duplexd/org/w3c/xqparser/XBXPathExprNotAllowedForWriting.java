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
package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

import org.xmlbeam.util.intern.duplex.XBPathParsingException;

/**
 *
 */
public class XBXPathExprNotAllowedForWriting extends XBPathParsingException {
    
    private static final long serialVersionUID = 8944815612805655746L;

    public XBXPathExprNotAllowedForWriting(SimpleNode node,String reason) {
        super (reason+": Node "+node.toString()+" is not supported for writing expressions",node.beginLine,node.beginColumn,node.endColumn,node.endLine);
        //node.parser.token_source.input_stream.inputStream.reset().
        }
}
