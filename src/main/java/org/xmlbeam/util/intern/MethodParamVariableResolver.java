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

import java.lang.reflect.Method;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathVariableResolver;

/**
 *
 */
public class MethodParamVariableResolver implements XPathVariableResolver {

    private final XPathVariableResolver originalResolver;
    private final Object[] args;
    private final Method method;

    /**
     * @param method
     * @param args 
     * @param originalResolver
     */
    public MethodParamVariableResolver(final Method method,final Object[] args,final XPathVariableResolver originalResolver) {
        this.method = method;
        this.args = args;
        this.originalResolver = originalResolver;
    }

    @Override
    public Object resolveVariable(QName variableName) {
        int c=-1;
        for (String name:ReflectionHelper.getMethodParameterNames(method)) {
            ++c;
            if (QName.valueOf(name).equals(variableName)) {
                return args[c];
            }
        }
        if (originalResolver==null) {
            return null;
        }
        return originalResolver.resolveVariable(variableName);
    }

}
