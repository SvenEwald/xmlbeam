/**
 *  Copyright 2012 Sven Ewald
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
package org.xmlbeam.tests;

import java.util.List;

import org.xmlbeam.DocumentURL;
import org.xmlbeam.XPathProjection;


@org.xmlbeam.DocumentURL("resource://testsuite.xml")
public interface XMLBeamTestSuite {

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement1")
    String getStringContent();

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    byte getbyteContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Byte getByteContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    short getshortContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Short getShortContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    int getintContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Integer getIntegerContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    long getlongContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Long getLongContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    float getfloatContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Float getFloatContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    double getdoubleContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Double getDoubleContent(int index);

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement6")
    boolean getbooleanContent();

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/innerElement6/@boolAttribute")
    boolean getbooleanAttributeContent();

    @org.xmlbeam.XPathProjection("nothing")
    void notSupportedReturnType();
    
    @org.xmlbeam.XPathProjection("nothing/")
    void setterWithTrailingSlash(String param);

    @org.xmlbeam.XPathProjection("//NonExistingElement")
    int getNonExistingIntContent();

    @org.xmlbeam.XPathProjection("//NonExistingElement")
    Integer getNonExistingIntegerContent();

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/firstelement/@glueattribute")
    String getAttributeValue();

    @org.xmlbeam.XPathProjection(value="/gluerootnode/intermediate/secondElement/list/entry",targetComponentType=String.class)
    List<String> getAStringList();

    @org.xmlbeam.XPathProjection(value="/gluerootnode/intermediate/secondElement/list/entry",targetComponentType=String.class)
    String[] getAStringArray();

    @org.xmlbeam.XPathProjection(value="//thirdElement/@*",targetComponentType=String.class)
    List<String> getAttributeValuesAsStringlist();

    interface InnerStructure {
        @org.xmlbeam.XPathProjection("child::innerStructureA")
        String getA();
        @org.xmlbeam.XPathProjection(value="child::innerStructureB/item",targetComponentType=String.class)
        List<String> getB();
    }

    @org.xmlbeam.XPathProjection("/gluerootnode/intermediate/fourthElement[1]")
    InnerStructure getFirstInnerStructure();

    @org.xmlbeam.XPathProjection(value="/gluerootnode/intermediate/fourthElement",targetComponentType=InnerStructure.class)
    List<InnerStructure> getAllInnerStructures();

    interface Setting {
        @org.xmlbeam.XPathProjection("@name")
        String getName();
        @org.xmlbeam.XPathProjection("@value")
        String getValue();
        @org.xmlbeam.XPathProjection("child::option")
        boolean hasOption();
    }

    @DocumentURL("resource://external_document.{0}")
    @org.xmlbeam.XPathProjection(value="/settings/setting",targetComponentType=Setting.class)
    List<Setting> getExternalSettings(String externsion);

    @XPathProjection("/description")
    void setDescription(String description);
}
