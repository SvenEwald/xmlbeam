/**
 *  Copyright 2012 Sven Ewald
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
package org.xmlbeam.tutorial.e09_dreambox;

import org.xmlbeam.annotation.XBRead;

/**
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 *
 */
public interface Movie {

    @XBRead("child::e2servicereference")
    String getServiceReference();

    @XBRead("child::e2time")
    long getTime();

    @XBRead("child::e2length")
    long getLength();

    @XBRead("child:e2filesize")
    long getFileSize();

    @XBRead("child::e2title")
    String getTitle();

    @XBRead("child::e2description")
    String getDescription();

    @XBRead("child::e2descriptionextended")
    String getDescriptionExtended();

    @XBRead("child::e2tags")
    String getTags();

}
