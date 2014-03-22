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

import java.io.Serializable;

public class AsmProxy implements Serializable{
    private final AsmProxyInvocationHandler handler;

    public AsmProxy(final AsmProxyInvocationHandler handler) {
        this.handler = null;
    }

    public int methodWithReturnCode() {
        return (Integer) handler.asmInvoke(null, new Object[0]);
        //    Object[] params = new Object[] {param1,param2};
        //    return (String) handler.asmInvoke(null, params);
    }
}