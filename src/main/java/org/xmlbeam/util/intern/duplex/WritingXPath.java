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

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Node;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.ApplyValuesVisitor;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.BuildDocumentVisitor;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.ParseException;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.SimpleNode;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParser;

/**
 * @author sven
 */
public class WritingXPath {

    /**
     * @author sven
     */
    public static class WritingXPathEvaluator {
        private final SimpleNode xpathSyntaxTreeStart;

        protected WritingXPathEvaluator(final SimpleNode node) {
            this.xpathSyntaxTreeStart = node;
        }

        public Object evaluateOrCreate(final Node target,final Collection<?> values) {
            return  xpathSyntaxTreeStart.jjtAccept(new ApplyValuesVisitor(values), target);
        }
        
    }

    /**
     * @param xpath
     * @return compiled WritingXPath
     * @throws ParseException
     */
    public static WritingXPathEvaluator compile(final String xpath) throws ParseException {
        XParser parser = new XParser(new StringReader(xpath));
        SimpleNode node = parser.START();
        return new WritingXPathEvaluator(node);
    }
}
