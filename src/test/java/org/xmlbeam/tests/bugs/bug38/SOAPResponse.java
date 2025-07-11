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
package org.xmlbeam.tests.bugs.bug38;

import org.xmlbeam.annotation.XBRead;

@SuppressWarnings("javadoc")
public interface SOAPResponse {

    @XBRead("/soap:Envelope/soap:Body")
    Body getBody();

    interface Body {
//        @XBRead("./ns1:storeTokenResponse/ns1:result")
        @XBRead("./ns1:storeTokenResponse/ns1:result")
        Result getResult();
    }

    interface Result {

        @XBRead("./ns1:alias")
        String getAlias();

        @XBRead("./ns1:aliasType")
        String getAliasType();

        @XBRead("./ns1:pspReference")
        String getPspReference();

        @XBRead("./ns1:recurringDetailReference")
        String getRecurringDetailReference();

        @XBRead("./ns1:result")
        String getResult();

    }
}
