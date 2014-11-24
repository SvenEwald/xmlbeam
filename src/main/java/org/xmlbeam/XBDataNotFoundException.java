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
package org.xmlbeam;

/**
 * You may declare your reading projection methods throwing exceptions.
 * This would be a good candidate, especially for debugging purpose.
 * If declare any other exception, this one will be set as the cause.
 */
final public class XBDataNotFoundException extends XBException {

    private static final long serialVersionUID = -4993020872096092774L;
    private final String resolvedXPath;

    /**
     * @param xpath resolved XPath.
     */
    XBDataNotFoundException(String xpath) {
        super("Data that was expected to be found... was not");
        this.resolvedXPath=xpath;
    }

    /**
     * @return The XPath after pre-processing that did not select any data.
     */
    public String getResolvedXPath() {
        return resolvedXPath;
    }
}
