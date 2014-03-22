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
package org.xmlbeam.tests.util.intern;

import org.junit.Test;
import org.xmlbeam.util.intern.ASMHelper;

/**
 * @author sven
 *
 */
public class TestASMProxy {

    public interface ProxyMe {

        int invokeMePlz(String a, String b, String c);

        //String invokeWithParam(String s);

    }


    @Test
    public void testProxy() {

        Object proxyObeject = new ProxyMe() {

            ProxyMe handler = new ProxyMe() {

                @Override
                public int invokeMePlz(final String a, final String b, final String c) {

                    return a.length() + b.length() + c.length();
                }

            };

            @Override
            public int invokeMePlz(final String a, final String b, final String c) {
                return handler.invokeMePlz(a, b, c);
            }

        };

//        AsmProxyInvocationHandler myHandler = new AsmProxyInvocationHandler() {
//
//            @Override
//            public Object asmInvoke(final Method method, final Object[] args) {
//                System.out.println(method);
//                return Integer.valueOf(23);
//            }
//        };
        ProxyMe proxyMe = ASMHelper.create(ProxyMe.class, proxyObeject);
        System.out.println(proxyMe.invokeMePlz("n.mn", "", ""));
    }

}
