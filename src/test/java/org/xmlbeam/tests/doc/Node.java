/*
 * Copyright 2025 Sven Ewald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlbeam.tests.doc;

import org.xmlbeam.annotation.XBRead;

@SuppressWarnings("javadoc")
public interface Node {

    @XBRead("./xbdefaultns:data[@key='d4']")
    String getURL();

    @XBRead("normalize-space(./xbdefaultns:data/y:ShapeNode/y:NodeLabel)")
    String getLabel();

    @XBRead("./@id")
    String getID();

}
