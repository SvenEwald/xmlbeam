/**
 *  Copyright 2016 Sven Ewald
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
package org.xmlbeam.types;

import java.util.List;

import org.xmlbeam.annotation.XBAutoBind;


/**
 * A List implementation that automatically changes the DOM when instances of this type are changed.
 * It can be used by declaring a reading projection method returning this type.
 * If the XBAutoBind annotation is used, returning List instances will implement this interface.
 * @see XBAutoValue
 * @see XBAutoBind
 * @author sven
 * @param <E> Component type of this collection
 */
public interface XBAutoList<E> extends List<E> {

}
