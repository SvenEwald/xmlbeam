/**
 *  Copyright 2017 Sven Ewald
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
package org.xmlbeam.refcards.xgml;

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.types.XBAutoValue;

/**
 *
 */
public interface Section {

    @XBRead("./@name")
    XBAutoValue<String> name();
    
    @XBRead("./attribute[@key='id'][@type='int']")
    XBAutoValue<Integer> id();
    
    @XBRead("./attribute[@key='text'][@type='String']")
    XBAutoValue<String>  text();
    
    @XBRead("./section[@name='LabelGraphics']/attribute[@key='text'][@type='String']")
    XBAutoValue<String> label();
    
    @XBRead("./attribute[@key='source'][@type='int']")
    XBAutoValue<Integer> source();
     
    @XBRead("./attribute[@key='target'][@type='int']")
    XBAutoValue<Integer> target();
    
    @XBRead("./section[@name='graphics']/attribute[@key='targetArrow'][@type='String']")
    XBAutoValue<String> graphics();
    
    @XBRead("./section[@name='graphics']/attribute[@key='fill'][@type='String']")
    XBAutoValue<String> fill();
    
    @XBRead("./section[@name='graphics']/attribute[@key='w'][@type='double']")
    XBAutoValue<Double> widh();
           

}

