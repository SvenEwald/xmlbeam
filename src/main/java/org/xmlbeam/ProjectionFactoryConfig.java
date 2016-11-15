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
package org.xmlbeam;

import org.xmlbeam.XBProjector.ConfigBuilder;
import org.xmlbeam.config.XMLFactoriesConfig;
import org.xmlbeam.externalizer.Externalizer;
import org.xmlbeam.types.TypeConverter;

/**
 */
interface ProjectionFactoryConfig extends XMLFactoriesConfig {

    /**
     * Getter for the current type converter.
     * You may implement you own type converting mechanism and return it here.
     * @return type converter.
     */
    TypeConverter getTypeConverter();

    /**
     * Setter for type converter. This method allows to plug in other type converting mechanisms.
     * @param converter
     * @return this for Convenience
     */
    ConfigBuilder setTypeConverter(TypeConverter converter);

    /**
     * Every String literal used in a annotation may be externalized (e.g. to a property file). You
     * may register a Externalizer instance here and reference it in a projection definition.
     * 
     * @param e10r
     * @return this for convenience.
     */
    ConfigBuilder setExternalizer(Externalizer e10r);

    /**
     * Getter for current {@link Externalizer}.
     * @return the currently used {@link Externalizer}
     */
    Externalizer getExternalizer();

}