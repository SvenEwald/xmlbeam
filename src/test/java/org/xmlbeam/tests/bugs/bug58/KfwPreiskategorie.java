/**
 *  Copyright 2020 Sven Ewald
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
package org.xmlbeam.tests.bugs.bug58;


import java.util.List;

import org.xmlbeam.annotation.XBRead;

public interface KfwPreiskategorie {

    @XBRead("../../@nummer")
    public int getProgrammNummer();
    @XBRead("../../@name")
    public String getProgrammName();
    @XBRead("../../@auszahlungskurs")
    public String getProgrammAuszahlungskurs();
    @XBRead("../../@provision")
    public String getProgrammProvision();
    @XBRead("../@laufzeit")
    public int getVarianteLaufzeit();
    @XBRead("../@tilgungsfreie_anlaufjahre")
    public int getVarianteTilgunsfreieJahre();
    @XBRead("../@zinsbindung")
    public int getVarianteZinsbindungJahre();
    @XBRead("../@haftungsfreistellung")
    public int getVarianteHaftungsfreistellung();
    @XBRead("./@key")
    public String getKey();
    @XBRead("./@gueltig_ab")
    public String getGueltigAb();
    @XBRead("./anmerkungen/anmerkung/text()")
    public List<String> getAnmerkungen();
    @XBRead("./ekn/zinssatz/@nominal")
    public String getNominalZins();
    @XBRead("./ekn/zinssatz/@effektiv")
    public String getEffektivZins();


}