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
package org.xmlbeam.tests.format;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.junit.Test;
import org.xmlbeam.XBProjector;

/**
 * @author sven
 */
public class testFormatOptions {

    @Test
    public void testFormatOptions() {
        Locale[] locales = NumberFormat.getAvailableLocales();
        double myNumber = -1234.56;
        NumberFormat form;
        for (int j = 0; j < 4; ++j) {
            System.out.println("FORMAT");
            for (int i = 0; i < locales.length; ++i) {
                if (locales[i].getCountry().length() == 0) {
                    continue; // Skip language-only locales
                }
                System.out.print(locales[i].getDisplayName());
                switch (j) {
                case 0:
                    form = NumberFormat.getInstance(locales[i]);
                    break;
                case 1:
                    form = NumberFormat.getIntegerInstance(locales[i]);
                    break;
                case 2:
                    form = NumberFormat.getCurrencyInstance(locales[i]);
                    break;
                default:
                    form = NumberFormat.getPercentInstance(locales[i]);
                    break;
                }
                if (form instanceof DecimalFormat) {
                    System.out.print(": " + ((DecimalFormat) form).toPattern());
                }
                System.out.print(" -> " + form.format(myNumber));
                try {
                    System.out.println(" -> " + form.parse(form.format(myNumber)));
                } catch (ParseException e) {
                }
            }
        }
        ProjectionWithFormats projection = new XBProjector().projectEmptyDocument(ProjectionWithFormats.class);
        projection.getDate();
    }

}
