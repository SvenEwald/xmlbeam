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
package org.xmlbeam.util.intern.duplex.org.w3c.xqparser;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author se
 *
 */
public class ByIds implements Transformer<SimpleNode>{

    private final Set<Integer> ids= new TreeSet<Integer>();
    
    @Override
    public SimpleNode transform(SimpleNode node) {        
        return ids.contains(node.getID()) ? node : null;
    }
    
    
    public ByIds(int... ids) {
        for (int id:ids) {
            this.ids.add(id);
        }
    }

}
