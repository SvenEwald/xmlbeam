package org.xmlbeam.tests.bugs.bug54;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.xmlbeam.XBProjector;

public class XBTest {

    @Test
    public void xml2Object() throws IOException {

        XBProjector projector = new XBProjector();
        BaseObject fromFile = projector.io().file("src/test/java/org/xmlbeam/tests/bugs/bug54/xb.xml").read(BaseObject.class);

        assertEquals(1, fromFile.getId());
        assertEquals(5, fromFile.getColor());

        SubObject subObject = fromFile.getSubObjects().get(0);

        assertEquals(2, subObject.getId());
        assertEquals("The description", subObject.getText());
    }

    @Test
    public void object2Xml() {

        XBProjector projector = new XBProjector();

        BaseObject baseObject = projector.projectEmptyDocument(BaseObject.class);

        baseObject.setId(1);
        baseObject.setColor(123);

        SubObject subObject = projector.projectEmptyElement("SubObject", SubObject.class);
        subObject.setId(456);
        subObject.setText("The text");

        List< SubObject > subs = new ArrayList<SubObject>();
        subs.add(subObject);
        baseObject.setSubObjects(subs);

        String xml = projector.asString(baseObject);

        System.out.println(xml);

        assertTrue(xml.contains("123")); // OK
        assertTrue(xml.contains("456")); // fails!

    }

}
