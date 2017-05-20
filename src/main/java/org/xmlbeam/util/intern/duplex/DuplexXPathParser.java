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

import java.util.Map;

import java.io.StringReader;

import org.xmlbeam.exceptions.XBException;

/**
 * @author sven
 */
public class DuplexXPathParser {

    private final Map<String, String> userDefinedMapping;

    /**
     * @param path
     * @return DuplexExpression
     * @throws XBPathParsingException
     */
    public DuplexExpression compile(final CharSequence path) throws XBPathParsingException {
        final XParser parser = new XParser(new StringReader(path.toString()));
        try {
            SimpleNode node = parser.START();
            return new DuplexExpression(node, path, userDefinedMapping);
        } catch (ParseException e) {
            throw new XBException("Can not parse xpath:'" + path + "'", e);
        }
    }

    /**
     * @param userDefinedMapping
     */
    public DuplexXPathParser(final Map<String, String> userDefinedMapping) {
        this.userDefinedMapping = userDefinedMapping;
    }
}
