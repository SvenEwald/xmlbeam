/**
 *  Copyright 2015 Sven Ewald
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
package org.xmlbeam.evaluation;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;

import org.xmlbeam.XBProjector;
import org.xmlbeam.util.intern.MethodParamVariableResolver;
import org.xmlbeam.util.intern.duplex.DuplexExpression;

public class InvocationContext {
    public String getResolvedXPath() {
        return resolvedXPath;
    }

    public XPath getxPath() {
        return xPath;
    }

    public XPathExpression getxPathExpression() {
        return xPathExpression;
    }

    public DuplexExpression getDuplexExpression() {
        return duplexExpression;
    }

    public InvocationContext(final String resolvedXPath, final XPath xPath, final XPathExpression xPathExpression, final DuplexExpression duplexExpression, final MethodParamVariableResolver resolver, final Class<?> targetComponentType, final XBProjector projector) {
        this.resolvedXPath = resolvedXPath;
        this.xPath = xPath;
        this.xPathExpression = xPathExpression;
        this.duplexExpression = duplexExpression;
        this.resolver = resolver;
        this.targetComponentType = targetComponentType;
        this.projector = projector;
    }

    final String resolvedXPath;
    final XPath xPath;
    final XPathExpression xPathExpression;
    final DuplexExpression duplexExpression;
    final MethodParamVariableResolver resolver;
    final Class<?> targetComponentType;
    final XBProjector projector;

    /**
     * @param resolvedXpath
     * @return true if this invocation context may be reused for given path
     */
    public boolean isStillValid(final String resolvedXpath) {
        return resolvedXpath.equals(this.resolvedXPath);
    }

    /**
     * @return Format pattern for the expression within with this context.
     */
    public String getExpressionFormatPattern() {
        return duplexExpression.getExpressionFormatPattern();
    }

    public void updateMethodArgs(final Object[] args) {
        if (resolver != null) {
            resolver.updateArgs(args);
        }
    }

    public Class<?> getTargetComponentType() {
        return targetComponentType;
    }

    public XBProjector getProjector() {
        return projector;
    }
}