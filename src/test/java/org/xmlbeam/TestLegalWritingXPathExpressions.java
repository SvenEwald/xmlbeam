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
package org.xmlbeam;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

/**
 */
@SuppressWarnings("javadoc")
public class TestLegalWritingXPathExpressions {

    private final static List<String> LEGAL_EXPRESSIONS = Arrays.asList(".", "/a", "/a/b/asdfZRc/d", "/a/b/@c", "@xasd","/a/../b","/a[b='e']","/a.a");
    private final static List<String> ILLEGAL_EXPRESSIONS = Arrays.asList("", "/", "@", "function()", "/trailing/slash/", "//double/slash", "/a/@b/c", "/a/b/.","/.a");

    private Pattern pattern;

    @Before
    public void getPattern() throws Exception, Exception {
        Field declaredField = ProjectionInvocationHandler.class.getDeclaredField("LEGAL_XPATH_SELECTORS_FOR_SETTERS");
        declaredField.setAccessible(true);
        pattern = (Pattern) declaredField.get(null);
    }

    @Test
    public void testLegalExpressions() {
        for (String expr:LEGAL_EXPRESSIONS) {
            assertTrue("'" + expr + "' should be a valid expression, but it is not", pattern.matcher(expr).matches());
        }
    }

    @Test
    public void testIllegalExpression() {
        for (String expr : ILLEGAL_EXPRESSIONS) {
            assertFalse("'" + expr + "' should not be a valid expression, but it is", pattern.matcher(expr).matches());
        }
    }
    
    @Test
    public void testElementStartChars() throws Exception {
        Field declaredField = ProjectionInvocationHandler.class.getDeclaredField("XML_NAME_START_CHARS");
        declaredField.setAccessible(true);
        String pattern = "["+ ((String) declaredField.get(null)) +"]";
//        pattern=":A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02ff\\u0370-\\u037d"
//                + "\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f\\u2c00-\\u2fef\\u3001-\\ud7ff"
        pattern= "\uf900-\ufdcf\ufdf0-\ufffd"+String.valueOf(Character.toChars(0x10000))+"-"+String.valueOf(Character.toChars(0xEFFFF));
                //\x10000-\xEFFFF";
        
        //Integer.toHexString("@".codePointAt(0))
        assertFalse("@".matches("["+pattern+"]"));
    }
}
