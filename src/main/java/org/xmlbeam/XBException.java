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
 * XMLBeam root exception. The base of all exceptions thrown during invoking
 * projection methods. Needs to be a RuntimeException to avoid
 * declaring exceptions on each projection method.
 */
public class XBException extends RuntimeException {

    private static final long serialVersionUID = 4989019505068594914L;
    
    public XBException(String msg, Throwable e) {
        super(msg,e);
    }



}
