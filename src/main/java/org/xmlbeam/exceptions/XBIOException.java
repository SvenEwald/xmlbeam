/**
 *  Copyright 2018 Sven Ewald
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
package org.xmlbeam.exceptions;

import java.io.IOException;

/**
 * Wraper for IOException.
 *
 */
public class XBIOException extends XBException {

    /**
     * 
     */
    private static final long serialVersionUID = -4098241369287487356L;

    /**
     * @param e
     */
    public XBIOException(IOException e) {
        super("IO Error:"+e.getMessage(), e);
    }

    /**
     * @param msg
     * @param e
     */
    public XBIOException(String msg, IOException e) {
        super(msg,e);
    }

}
