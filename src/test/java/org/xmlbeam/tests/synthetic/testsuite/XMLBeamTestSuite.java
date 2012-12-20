package org.xmlbeam.tests.synthetic.testsuite;

import java.util.List;

import org.xmlbeam.URI;
import org.xmlbeam.Xpath;


@org.xmlbeam.URI("resource://testsuite.xml")
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

    @URI("resource://external_document.{0}")
    @org.xmlbeam.Xpath(value="/settings/setting",targetComponentType=Setting.class)
    List<Setting> getExternalSettings(String externsion);

    @Xpath("/description")
    void setDescription(String description);
}
