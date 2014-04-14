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
package org.xmlbeam.tests.types;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.types.DefaultTypeConverter;
import org.xmlbeam.types.DefaultTypeConverter.Conversion;

@SuppressWarnings({"serial","javadoc"})
public class TestCustomTypeConverter {

    public static class HexToLongConversion extends Conversion<Long> {

        private HexToLongConversion(final Long defaultValue) {
            super(defaultValue);
        }

        @Override
        public Long convert(final String data) {
            return Long.parseLong(data, 16);
        }
    }

    public interface Projection {
        @XBRead("/foo")
        long getData();
    }

    @Test
    public void testAutoHexConversion() {
        XBProjector projector = new XBProjector();
        DefaultTypeConverter converter = projector.config().getTypeConverterAs(DefaultTypeConverter.class);
        converter.setConversionForType(Long.TYPE, new HexToLongConversion(0L));
        Projection projection = projector.projectXMLString("<foo>CAFEBABE</foo>", Projection.class);
        assertEquals(3405691582L, projection.getData());
    }
}
