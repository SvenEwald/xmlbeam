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
package org.xmlbeam.tests.bugs.bug38;

import org.junit.Assert;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.tests.bugs.bug38.SOAPResponse.Result;

@SuppressWarnings("javadoc")
public class TestSoapReading {

    private final static XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

    @Test
    public void canParseXML() throws Exception {

        SOAPResponse soapResponse = projector.io().url("res://soap.xml").read(SOAPResponse.class);
        Assert.assertNotNull(soapResponse.getBody());

        Result result = soapResponse.getBody().getResult();
        Assert.assertNotNull(result);

        Assert.assertEquals("B133243153928547", result.getAlias());
        Assert.assertEquals("Default", result.getAliasType());
        Assert.assertEquals("8514873382743402", result.getPspReference().trim());
        Assert.assertEquals("8414873382748121", result.getRecurringDetailReference().trim());
        Assert.assertEquals("Success", result.getResult());

    }
}
