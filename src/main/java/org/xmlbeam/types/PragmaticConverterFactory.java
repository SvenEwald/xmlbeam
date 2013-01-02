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
package org.xmlbeam.types;

import java.util.HashMap;
import java.util.Map;



/**
 * 
 */
public class PragmaticConverterFactory {
    
    public static Map<Class<?>, Conversion<?>> getConversions()
    { 
        Map<Class<?>, Conversion<?>> map = new HashMap<Class<?>, Conversion<?>>();
        map.put(Boolean.class, new ConversionWithDefault<Boolean>(false) {
            @Override
            public Boolean convert(final String data) {
                return Boolean.valueOf(data);
            }
        });
        map.put(Boolean.TYPE, map.get(Boolean.class));
        map.put(Byte.class, new ConversionWithDefault<Byte>((byte) 0) {
            @Override
            public Byte convert(final String data) {
                return Byte.valueOf(data);
            }
        });
        map.put(Byte.TYPE, map.get(Byte.class));
        map.put(Float.class, new ConversionWithDefault<Float>(0F) {
            @Override
            public Float convert(final String data) {
                return Float.valueOf(data);
            }
        });
        map.put(Float.TYPE, map.get(Float.class));
        map.put(Double.class, new ConversionWithDefault<Double>(0D) {
            @Override
            public Double convert(final String data) {
                return Double.valueOf(data);
            }
        });
        map.put(Double.TYPE, map.get(Double.class));
        map.put(Short.class, new ConversionWithDefault<Short>((short) 0) {
            @Override
            public Short convert(final String data) {
                return Short.valueOf(data);
            }
        });
        map.put(Short.TYPE, map.get(Short.class));
        map.put(Integer.class, new ConversionWithDefault<Integer>(0) {
            @Override
            public Integer convert(final String data) {
                return Integer.valueOf(data);
            }
        });
        map.put(Integer.TYPE, map.get(Integer.class));
        map.put(Long.class, new ConversionWithDefault<Long>(0L) {
            @Override
            public Long convert(final String data) {
                return Long.valueOf(data);
            }
        });
        map.put(Long.TYPE, map.get(Long.class));
        map.put(String.class, new ConversionWithDefault<String>("") {
            @Override
            public String convert(final String data) {
                return data;
            }
        });
        
        return map;
    }
};


