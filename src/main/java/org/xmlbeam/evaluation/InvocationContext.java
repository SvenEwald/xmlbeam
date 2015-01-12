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

/**
 * Context of an projection method invocation. May be cached if the the same method is called again.
 */
public class InvocationContext {
    /**
     * @return resolved XPath string. 
     */
    public String getResolvedXPath() {
        return resolvedXPath;
    }

    /**
     * @return currently used XPath instance
     */
    public XPath getxPath() {
        return xPath;
    }

    /**
     * @return compiled XPath expression
     */
    public XPathExpression getxPathExpression() {
        return xPathExpression;
    }

    /**
     * @return compiled duplex expression
     */
    public DuplexExpression getDuplexExpression() {
        return duplexExpression;
    }

    /**
     * Constructor.
     * @param resolvedXPath
     * @param xPath
     * @param xPathExpression
     * @param duplexExpression
     * @param resolver
     * @param targetComponentType
     * @param projector
     */
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

    /**
     * A context may be reused even when method parameters change. But the change needs to be reflected to XPath variable bindings.
     * @param args
     */
    public void updateMethodArgs(final Object[] args) {
        if (resolver != null) {
            resolver.updateArgs(args);
        }
    }

    /**
     * @return target type of evaluation
     */
    public Class<?> getTargetComponentType() {
        return targetComponentType;
    }

    /**
     * Projector used when the projection was created.
     * @return the projector instance.
     */
    public XBProjector getProjector() {
        return projector;
    }
}