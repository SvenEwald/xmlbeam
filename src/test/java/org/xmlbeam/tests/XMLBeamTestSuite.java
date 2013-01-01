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

import org.xmlbeam.URL;
import org.xmlbeam.Xpath;


@org.xmlbeam.URL("resource://testsuite.xml")
public interface XMLBeamTestSuite {

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement1")
    String getStringContent();

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    byte getbyteContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Byte getByteContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    short getshortContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Short getShortContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    int getintContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Integer getIntegerContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    long getlongContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Long getLongContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    float getfloatContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Float getFloatContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    double getdoubleContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Double getDoubleContent(int index);

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement6")
    boolean getbooleanContent();

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/innerElement6/@boolAttribute")
    boolean getbooleanAttributeContent();

    @org.xmlbeam.Xpath("nothing")
    void notSupportedReturnType();
    
    @org.xmlbeam.Xpath("nothing/")
    void setterWithTrailingSlash(String param);

    @org.xmlbeam.Xpath("//NonExistingElement")
    int getNonExistingIntContent();

    @org.xmlbeam.Xpath("//NonExistingElement")
    Integer getNonExistingIntegerContent();

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/firstelement/@glueattribute")
    String getAttributeValue();

    @org.xmlbeam.Xpath(value="/gluerootnode/intermediate/secondElement/list/entry",targetComponentType=String.class)
    List<String> getAStringList();

    @org.xmlbeam.Xpath(value="/gluerootnode/intermediate/secondElement/list/entry",targetComponentType=String.class)
    String[] getAStringArray();

    @org.xmlbeam.Xpath(value="//thirdElement/@*",targetComponentType=String.class)
    List<String> getAttributeValuesAsStringlist();

    interface InnerStructure {
        @org.xmlbeam.Xpath("child::innerStructureA")
        String getA();
        @org.xmlbeam.Xpath(value="child::innerStructureB/item",targetComponentType=String.class)
        List<String> getB();
    }

    @org.xmlbeam.Xpath("/gluerootnode/intermediate/fourthElement[1]")
    InnerStructure getFirstInnerStructure();

    @org.xmlbeam.Xpath(value="/gluerootnode/intermediate/fourthElement",targetComponentType=InnerStructure.class)
    List<InnerStructure> getAllInnerStructures();

    interface Setting {
        @org.xmlbeam.Xpath("@name")
        String getName();
        @org.xmlbeam.Xpath("@value")
        String getValue();
        @org.xmlbeam.Xpath("child::option")
        boolean hasOption();
    }

    @URL("resource://external_document.{0}")
    @org.xmlbeam.Xpath(value="/settings/setting",targetComponentType=Setting.class)
    List<Setting> getExternalSettings(String externsion);

    @Xpath("/description")
    void setDescription(String description);
}
