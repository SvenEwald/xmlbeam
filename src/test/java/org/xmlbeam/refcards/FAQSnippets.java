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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Document;
import org.xmlbeam.XBProjector;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.XMLFactoriesConfig;
import org.xmlbeam.dom.DOMAccess;

public class FAQSnippets {
    
    /**
     * @author sven
     */
    public class MyOwnXpathFactory extends XPathFactory {

        /*
         * (non-Javadoc)
         * 
         * @see javax.xml.xpath.XPathFactory#isObjectModelSupported(java.lang.String)
         */
        @Override
        public boolean isObjectModelSupported(String objectModel) {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.xml.xpath.XPathFactory#setFeature(java.lang.String, boolean)
         */
        @Override
        public void setFeature(String name, boolean value) throws XPathFactoryConfigurationException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.xml.xpath.XPathFactory#getFeature(java.lang.String)
         */
        @Override
        public boolean getFeature(String name) throws XPathFactoryConfigurationException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.xml.xpath.XPathFactory#setXPathVariableResolver(javax.xml.xpath.XPathVariableResolver
         * )
         */
        @Override
        public void setXPathVariableResolver(XPathVariableResolver resolver) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.xml.xpath.XPathFactory#setXPathFunctionResolver(javax.xml.xpath.XPathFunctionResolver
         * )
         */
        @Override
        public void setXPathFunctionResolver(XPathFunctionResolver resolver) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.xml.xpath.XPathFactory#newXPath()
         */
        @Override
        public XPath newXPath() {
            // TODO Auto-generated method stub
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
    }
    //END SNIPPET: Projection
    
    {        
        //START SNIPPET: MixinRegistration
        Object mixin = new Object() {
            private DOMAccess me;
            @Override
            public String toString() {
                return "I'm a "+ me.getProjectionInterface().getSimpleName();
            };
        };        
        projector.mixins().addProjectionMixin(Object.class, mixin);
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
            public XPath createXPath(Document... document) {
                return super.createXPath(document);
            }
        };

        XBProjector projector = new XBProjector(myConfig);
        //END SNIPPET: OwnXPathImplementation
    }

}
