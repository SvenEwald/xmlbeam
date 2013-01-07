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
import org.xmlbeam.XBRead;


@org.xmlbeam.DocumentURL("resource://testsuite.xml")
public interface XMLBeamTestSuite {

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement1")
    String getStringContent();

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    byte getbyteContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Byte getByteContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    short getshortContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Short getShortContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    int getintContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Integer getIntegerContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    long getlongContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Long getLongContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    float getfloatContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Float getFloatContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    double getdoubleContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement{0}")
    Double getDoubleContent(int index);

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement6")
    boolean getbooleanContent();

    @XBRead("/gluerootnode/intermediate/firstelement/innerElement6/@boolAttribute")
    boolean getbooleanAttributeContent();

    @XBRead("nothing")
    void notSupportedReturnType();
    
    @XBRead("nothing/")
    void setterWithTrailingSlash(String param);

    @XBRead("//NonExistingElement")
    int getNonExistingIntContent();

    @XBRead("//NonExistingElement")
    Integer getNonExistingIntegerContent();

    @XBRead("/gluerootnode/intermediate/firstelement/@glueattribute")
    String getAttributeValue();

    @XBRead(value = "/gluerootnode/intermediate/secondElement/list/entry", targetComponentType = String.class)
    List<String> getAStringList();

    @XBRead(value = "/gluerootnode/intermediate/secondElement/list/entry", targetComponentType = String.class)
    String[] getAStringArray();

    @XBRead(value = "//thirdElement/@*", targetComponentType = String.class)
    List<String> getAttributeValuesAsStringlist();

    interface InnerStructure {
        @XBRead("child::innerStructureA")
        String getA();

        @XBRead(value = "child::innerStructureB/item", targetComponentType = String.class)
        List<String> getB();
    }

    @XBRead("/gluerootnode/intermediate/fourthElement[1]")
    InnerStructure getFirstInnerStructure();

    @XBRead(value = "/gluerootnode/intermediate/fourthElement", targetComponentType = InnerStructure.class)
    List<InnerStructure> getAllInnerStructures();

    interface Setting {
        @XBRead("@name")
        String getName();

        @XBRead("@value")
        String getValue();

        @XBRead("child::option")
        boolean hasOption();
    }

    @DocumentURL("resource://external_document.{0}")
    @XBRead(value = "/settings/setting", targetComponentType = Setting.class)
    List<Setting> getExternalSettings(String externsion);

    @XBRead("/description")
    void setDescription(String description);
}
