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
package org.xmlbeam.util.intern;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sven
 */
public class Preprocessor {

    private final static Map<String, Integer> PARAM_INDEX_MAP = new HashMap<String, Integer>();

    static {
        for (int i = 0; i < 10; ++i) {
            PARAM_INDEX_MAP.put("ARG" + i, i);
            PARAM_INDEX_MAP.put("PARAM" + i, i);
            PARAM_INDEX_MAP.put(Integer.toString(i), i);
        }
    }

    /**
     * @param paramName
     * @return index of parameter or -1 if index could not determined
     */
    public static int getParameterIndex(final String paramName) {
        String uppercase = paramName.toUpperCase(Locale.ENGLISH);
        Integer index = PARAM_INDEX_MAP.get(uppercase);
        if (index != null) {
            return index.intValue();
        }
        String number = paramName.replaceFirst("PARAM|ARG", "");
        if (number.matches("[0-9]+")) {
            return Integer.parseInt(number);
        }
        return -1;
    }

    /**
     * @param args
     * @return a string with all place holders filled by given parameters
     */
    public static String applyParams(final String string, final Map<String, Integer> paramNameIndexMap, final Object[] args) {
        Matcher matcher = Pattern.compile("\\{[^\\{\\}]+\\}").matcher(string);
        if (matcher.matches()) {

        }
        return string;

//    }
//        int i = 0;
//        while (i < string.length()) {
//            int indexStart = string.indexOf('{', i);
//            if ((indexStart >= 0) && (indexStart < (string.length() - 1))) {
//
//
//            i = indexStart;
//            if (string.charAt(indexStart + 1) == '{') {
//                // Found escaped {
//                string = string.substring(0, indexStart) + string.substring(indexStart + 1, string.length());
//                i++;
//                continue;
//            }
//            int indexEnd = string.indexOf('}', i);
//            if ((indexEnd < 0) || (indexEnd >= (string.length() - 1))) {
//                return string;
//            }
//            i = indexEnd;
//            if (string.charAt(indexEnd + 1) == '}') {
//                // Found escaped }
//                string = string.substring(0, indexEnd) + string.substring(indexEnd + 1, string.length());
//                ++i;
//                continue;
//            }
//            String paramName = string.substring(indexStart, indexEnd);
//            int methodParameterIndex = getParameterIndex(paramName);
//            if ((methodParameterIndex < 0) || (methodParameterIndex > args.length)) {
//                throw new IllegalArgumentException("Illegal parameter '" + paramName + "' could not be applied.");
//            }
//            string = string.substring(0, indexStart) + args[methodParameterIndex] + string.substring(indexEnd, string.length());
//        }
//        return string;
    }
}
