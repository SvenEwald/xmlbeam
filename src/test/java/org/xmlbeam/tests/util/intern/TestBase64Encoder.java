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
package org.xmlbeam.tests.util.intern;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.xmlbeam.util.Base64;

/**
 * @author sven
 *
 */
public class TestBase64Encoder {

    @SuppressWarnings("javadoc")
    @Test
    public void testBase64Encoder() {
        assertEquals("QUJDREU=",Base64.printBase64Binary("ABCDE".getBytes()));
        assertEquals("QUJDREVG",Base64.printBase64Binary("ABCDEF".getBytes()));
        assertEquals("QUJDREVGRw==",Base64.printBase64Binary("ABCDEFG".getBytes()));
        byte[] b=new byte[256];
        for (int i=0;i<256;++i) 
        {
            b[i]=(byte)i;
        }
        assertEquals("AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+/w==",Base64.printBase64Binary(b));
    }
}
