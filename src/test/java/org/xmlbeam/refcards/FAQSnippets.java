/**
 *  Copyright 2013 Sven Ewald
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
package org.xmlbeam.refcards;

import org.xmlbeam.XBProjector;
import org.xmlbeam.dom.DOMAccess;

public class FAQSnippets {
    
    XBProjector projector = new XBProjector();
    
    {
        
    }    

    //START SNIPPET: MixinOverridingToString
    public interface MixinOverridingToString {
        @Override
        String toString();
    }
    //END SNIPPET: MixinOverridingToString

    //START SNIPPET: Projection    
    public interface Projection extends MixinOverridingToString {
        // Your projection methods here
    }
    //END SNIPPET: Projection
    
    {        
        //START SNIPPET: MixinRegistration
        Object mixin = new Object() {
            private DOMAccess me;
            @Override
            public String toString() {
                return "I'm a "+ me.getProjectionInterface().getSimpleName();
            };
        };        
        projector.mixins().addProjectionMixin(Object.class, mixin);
        //END SNIPPET: MixinRegistration  
    }

}
