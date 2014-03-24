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
package org.xmlbeam.tutorial.e15_plist;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.externalizer.ExternalizerAdapter;
import org.xmlbeam.tutorial.TutorialTestCase;

//START SNIPPET: Tutorial15

/* START SNIPPET: TutorialDescription
~~
 In this tutorial we address quite a few challenges in accessing the data in plist files.
 XML plists (XML Property Lists) files are key/value based data storages frequently used by OS X
 applications. The challenge is that the data is not stored in the key element, but besides.
 A key element is followed by an additional element holding the data. The name of the data containing element
 depends on the type of data. So we need some real XPath magic to cover this :)
 The second challenge is that we don't like to have XPath expressions specified for all data in the file.
 Instead we like to derive the key value of the expression from the method name.
 Last but not least we show how to switch the DTD validation of, in case your not on an OS X system.
END SNIPPET: TutorialDescription */

@SuppressWarnings("serial")
//START SNIPPET: TestPlistAccess
public class TestPlistAccess extends TutorialTestCase {

    /**
     * Every plist file has a DTD referenced which might be unavailable if you are on a non MacOs system.
     * So we tweak the XBProjector configuration to ignore the DTD.
     */
    private final class NonValidatingXMLFactoriesConfig extends DefaultXMLFactoriesConfig {
        @Override
        public DocumentBuilderFactory createDocumentBuilderFactory()  {
            try {
            final DocumentBuilderFactory factory = super.createDocumentBuilderFactory();
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            return factory;
            }  catch (final ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The projection in this tutorial does not define any XPath expression to the data.
     * Instead we like to derive the XPath from the method name. Although this could be done
     * in a single XPath expression, we split it up for two cases for readability:
     * - Selecting all children of the following element of the key element, if an array is expected
     * and
     * - Selecting the first following element of the key element, in all other cases.
     */
    public class PListExternalizer extends ExternalizerAdapter {

        @Override
        public String resolveXPath(final String annotationValue, final Method method, final Object[] args) {
            final String keyValue = method.getName().substring(3);
            if (method.getReturnType().isArray()) {
                return  "/plist/dict/key[.=\""+keyValue+"\"]/following-sibling::*[1]/child::*";
            }
           return  "/plist/dict/key[.=\""+keyValue+"\"]/following-sibling::*[1]";
        }

    }

    @Test
    public void testReadPList() throws IOException {
        final XBProjector projector = new XBProjector(new NonValidatingXMLFactoriesConfig());
        projector.config().setExternalizer(new PListExternalizer());
        final PList plist = projector.io().fromURLAnnotation(PList.class);
        System.out.println(plist.getAuthor()+" ("+plist.getBirthdate()+")");
        for (final String line:plist.getLines()) {
            System.out.println(line);
        }
    }
}
//END SNIPPET: TestPlistAccess
//END SNIPPET: Tutorial15
