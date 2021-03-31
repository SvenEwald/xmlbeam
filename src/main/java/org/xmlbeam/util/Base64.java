/**
 *  Copyright 2021 Sven Ewald
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
package org.xmlbeam.util;

/**
 * @author sven
 */
public class Base64 {

    private static final char[] chars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

    @SuppressWarnings("javadoc")
    public static String printBase64Binary(byte[] input) {
        char[] buf = new char[4*((input.length + 2) / 3)];
        int out = 0;
        for (int i = 0; i + 2 < input.length; i += 3) {
            buf[out++] = encodeFirst(input[i]);
            buf[out++] = encodeSecond(input[i], input[i + 1]);
            buf[out++] = encodeThird(input[i + 1], input[i + 2]);
            buf[out++] = encodeFourth(input[i + 2]);
        }
        if (input.length % 3 == 1) {
            buf[out++] = encodeFirst(input[input.length-1]);
            buf[out++] = encodeSecond(input[input.length-1], (byte) 0);
            buf[out++] = '=';
            buf[out++] = '=';
        }
        if (input.length % 3 == 2) {
            buf[out++] = encodeFirst(input[input.length-2]);
            buf[out++] = encodeSecond(input[input.length-2], input[input.length - 1]);
            buf[out++] = encodeThird(input[input.length - 1], (byte) 0);
            buf[out++] = '=';
        }
        return new String(buf);
    }

    private static char encodeFourth(byte d) {
        return chars[(d & 0x3F)];
    }

    private static char encodeThird(byte b, byte c) {
        return chars[(((b & 0xF) << 2) | ((c >> 6) & 0x3)) & 0x3F];
    }

    private static char encodeSecond(byte a, byte b) {
        return chars[(((a & 0x3) << 4) | ((b >> 4) & 0xF)) & 0x3F];
    }

    private static char encodeFirst(byte a) {
        return chars[(a >> 2) & 0x3F];
    }

}
