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
