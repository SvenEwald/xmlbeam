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
import java.util.Locale;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathVariableResolver;

import org.xmlbeam.types.StringRenderer;
import org.xmlbeam.util.intern.duplex.DuplexExpression;

/**
 *
 */
public class MethodParamVariableResolver implements XPathVariableResolver {

    private final XPathVariableResolver originalResolver;
    private Object[] args;
    private final Method method;
    private final DuplexExpression expression;
    private final StringRenderer stringRenderer;

    /**
     * @param method
     * @param args
     * @param expression
     * @param stringRenderer
     * @param originalResolver
     */
    public MethodParamVariableResolver(final Method method, final Object[] args, final DuplexExpression expression, final StringRenderer stringRenderer, final XPathVariableResolver originalResolver) {
        this.method = method;
        this.args = args;
        this.originalResolver = originalResolver;
        this.expression = expression;
        this.stringRenderer = stringRenderer;
    }

    @Override
    public Object resolveVariable(final QName variableName) {
        if ((variableName != null) && (variableName.getLocalPart() != null)) {
            final String uppercaseName = variableName.getLocalPart().toUpperCase(Locale.ENGLISH);
            Integer index = ReflectionHelper.getMethodParameterIndexes(method).get(uppercaseName);
            if (index != null) {
                return stringRenderer.render(args[index].getClass(), args[index], expression.getVariableFormatPattern(variableName.getLocalPart()));
            }
            int preprocessorIndex = Preprocessor.getParameterIndex(uppercaseName);
            if (preprocessorIndex >= 0) {
                return stringRenderer.render(args[preprocessorIndex].getClass(), args[preprocessorIndex], expression.getVariableFormatPattern(variableName.getLocalPart()));
            }
        }
        if (originalResolver == null) {
            return null;
        }
        return originalResolver.resolveVariable(variableName);
    }

    /**
     * @param args
     */
    public void updateArgs(final Object[] args) {
        this.args = args;
    }
}
