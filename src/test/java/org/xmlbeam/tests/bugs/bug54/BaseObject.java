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
package org.xmlbeam.tests.bugs.bug54;

import java.util.List;

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

public interface BaseObject {

    @XBRead ("/base/@id")
    public int getId();

    @XBWrite ("/base/@id")
    public void setId(int id);

    @XBRead ("/base/color")
    public int getColor();

    @XBWrite ("/base/color")
    public void setColor(int color);

    @XBRead ("/base/foo/bar/subs/subObject")
    public List< SubObject > getSubObjects();

    @XBWrite ("/base/foo/bar/subs/subObject")
    public void setSubObjects(List< SubObject > subObjects);

}
