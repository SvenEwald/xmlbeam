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
     * @param string
     * @param paramNameIndexMap
     * @param args
     * @return a string with all place holders filled by given parameters
     */
    public static String applyParams(final String string, final Map<String, Integer> paramNameIndexMap, final Object[] args) {
        if ((string == null) || (string.length() == 0)) {
            return string;
        }
        final StringBuilder applied = new StringBuilder(), paramNameBuilder = new StringBuilder();
        char a,b;
        boolean inVarname = false;
        for (int i = 0; i <= string.length() - 1; ++i) {
            a = string.charAt(i);
            b = i < string.length() - 1 ? string.charAt(i + 1) : 'X';
            if ((a == '{') && (b == '{')) {
                applied.append('{');
                ++i;
                continue;
            }
            if ((a == '}') && (b == '}')) {
                applied.append('}');
                ++i;
                continue;
            }
            if (a != '{') {
                applied.append(a);
                continue;
            }
            inVarname = true;
            while ((i < string.length() - 2) && (b != '}')) {
                paramNameBuilder.append(b);
                i++;
                b = string.charAt(i + 1);
            }
            inVarname = b != '}';
            ++i;
            applied.append(resolveParameter(paramNameBuilder.toString(), paramNameIndexMap, args));
        }

        if (inVarname) {
            throw new IllegalArgumentException("Unmatched '{'");
        }
        return applied.toString();
    }

    private static String resolveParameter(final String paramName, final Map<String, Integer> paramNameIndexMap, final Object[] args) {
        if (paramNameIndexMap != null) {
            Integer index = paramNameIndexMap.get(paramName);
            if (index != null) {
                return args[index.intValue()].toString();
            }
        }
        int index = getParameterIndex(paramName);
        if (index >= 0) {
            return args[index].toString();
        }
        throw new IllegalArgumentException("Can not find argument for parameter '" + paramName + "'");
    }
}
