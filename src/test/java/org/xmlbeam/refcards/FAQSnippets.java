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
package org.xmlbeam.refcards;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.DefaultXMLFactoriesConfig.NamespacePhilosophy;
import org.xmlbeam.config.XMLFactoriesConfig;
import org.xmlbeam.externalizer.ExternalizerAdapter;

@SuppressWarnings({ "unused", "serial", "javadoc" })
public class FAQSnippets {

    /**
     * @author sven
     */
    public class MyOwnXpathFactory extends XPathFactory {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isObjectModelSupported(final String objectModel) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setFeature(final String name, final boolean value) throws XPathFactoryConfigurationException {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean getFeature(final String name) throws XPathFactoryConfigurationException {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setXPathVariableResolver(final XPathVariableResolver resolver) {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setXPathFunctionResolver(final XPathFunctionResolver resolver) {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XPath newXPath() {
            return null;
        }

    }

    XBProjector projector = new XBProjector();

    {

    }

    //START SNIPPET: MixinOverridingToString
    public interface MixinOverridingToString {
        @Override
        String toString();
    }
    //END SNIPPET: MixinOverridingToString

    //START SNIPPET: Projection
    public interface Projection extends MixinOverridingToString {
        // Your projection methods here
        @XBRead("...")
        String getSomeValue();
    }
    //END SNIPPET: Projection

    {
        //START SNIPPET: MixinRegistration
        Object mixin = new Object() {
            private Projection me;
            @Override
            public String toString() {
                return "I have value "+me.getSomeValue();
            };
        };
        projector.mixins().addProjectionMixin(Projection.class, mixin);
        //END SNIPPET: MixinRegistration
    }

    {
        //START SNIPPET: OwnXPathFactoriesImplementation
        XMLFactoriesConfig myConfig = new DefaultXMLFactoriesConfig() {
            {
                // We should change the behavior of the config to not
                // interfere with the name space handling of an
                // unknown XPath implementation.
                setNamespacePhilosophy(NamespacePhilosophy.AGNOSTIC);
            }

            /**
             * Override this method to inject your own factory.
             */
            @Override
            public XPathFactory createXPathFactory() {
                return new MyOwnXpathFactory();
            }
        };

        XBProjector projector = new XBProjector(myConfig);
        //END SNIPPET: OwnXPathFactoriesImplementation
        projector.hashCode();
    }
    {
        //START SNIPPET: OwnXPathImplementation
        XMLFactoriesConfig myConfig = new DefaultXMLFactoriesConfig() {
            {
                // We should change the behavior of the config to not
                // interfere with the name space handling of an
                // unknown XPath implementation.
                setNamespacePhilosophy(NamespacePhilosophy.AGNOSTIC);
            }

            /**
             * Or override this to bypass the factory and create your own XPath implementation here.
             */
            @Override
            public XPath createXPath(final Document... document) {
                return super.createXPath(document);
            }
        };

        XBProjector projector = new XBProjector(myConfig);
        //END SNIPPET: OwnXPathImplementation
        projector.hashCode();
    }

    public interface ExampleProjection {
        @XBRead
        List<String> getDepartmentUsersName();
    }

    @Test
    public void UnCamelCaseTest() {
        XBProjector projector = new XBProjector();
        projector.config().setExternalizer(new ExternalizerAdapter() {
            @Override
            public String resolveXPath(final String annotationValue, final Method method, final Object[] args) {
                // Simplest conversion of camel case getter to xpath expression.
                return method.getName().substring(3).replaceAll("[A-Z]", "/$0");
            }
        });
        List<String> departmentUsers = projector.projectXMLString("<Department><Users><Name>John Doe</Name><Name>Tommy Atkins</Name></Users></Department>", ExampleProjection.class).getDepartmentUsersName();
        assertTrue(departmentUsers.size() == 2);
        assertEquals("John Doe", departmentUsers.get(0));
        assertEquals("Tommy Atkins", departmentUsers.get(1));
    }

}
