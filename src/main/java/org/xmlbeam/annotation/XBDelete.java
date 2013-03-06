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
package org.xmlbeam.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.xmlbeam.externalizer.Externalizer;
import org.xmlbeam.externalizer.NotExternalizedExternalizer;

/**
 * Define the projection function to delete elements or attributes.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XBDelete {

    /**
     * XPath expression to select XML nodes to be deleted.
     * 
     * @return
     */
    String value();

    
    Class<? extends Externalizer> externalizer() default NotExternalizedExternalizer.class;

}
