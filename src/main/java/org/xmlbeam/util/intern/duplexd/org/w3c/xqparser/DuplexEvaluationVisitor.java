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
package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTABBREVFORWARDSTEP;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTABBREVREVERSESTEP;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTADDITIVEEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTANDEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTANYFUNCTIONTEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTANYKINDTEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTATOMICTYPE;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTATTRIBNAMEORWILDCARD;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTATTRIBUTEDECLARATION;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTATTRIBUTENAME;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTATTRIBUTETEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTCASTABLEEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTCASTEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTCOMMENTTEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTCOMPARISONEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTCONTEXTITEMEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTDECIMALLITERAL;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTDOCUMENTTEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTDOUBLELITERAL;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTDYNAMICFUNCTIONINVOCATION;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTELEMENTDECLARATION;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTELEMENTNAME;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTELEMENTNAMEORWILDCARD;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTELEMENTTEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTENCLOSEDEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTFOREXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTFORWARDAXIS;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTFUNCTIONCALL;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTFUNCTIONITEMEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTFUNCTIONQNAME;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTFUNCTIONTEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTIFEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTINLINEFUNCTION;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTINSTANCEOFEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTINTEGERLITERAL;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTINTERSECTEXCEPTEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTITEMTYPE;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTLBRACE;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTLETEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTLITERALFUNCTIONITEM;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTMINUS;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTMULTIPLICATIVEEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTNAMESPACENODETEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTNAMETEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTNCNAME;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTNCNAMECOLONSTAR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTNODETEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTOCCURRENCEINDICATOR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTOREXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPARAM;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPARAMLIST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPARENTHESIZEDEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPARENTHESIZEDITEMTYPE;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPATHEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPITEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPLUS;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPREDICATE;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPREDICATELIST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTQNAME;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTQUANTIFIEDEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTRANGEEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTRBRACE;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTREVERSEAXIS;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSCHEMAATTRIBUTETEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSCHEMAELEMENTTEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSEQUENCETYPE;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSIMPLEFORBINDING;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSIMPLELETBINDING;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSIMPLELETCLAUSE;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSINGLETYPE;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSLASH;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSLASHSLASH;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSTARCOLONNCNAME;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSTART;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSTEPEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSTRINGLITERAL;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTTEXTTEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTTREATEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTTYPEDECLARATION;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTTYPEDFUNCTIONTEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTTYPENAME;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTUNARYEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTUNIONEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTVARNAME;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTVOID;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTWILDCARD;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTXPATH;

import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlbeam.XBException;
import org.xmlbeam.util.intern.DOMHelper;

/**
 * @author sven
 */
class DuplexEvaluationVisitor implements INodeEvaluationVisitor<List<org.w3c.dom.Node>> {

    private final static INodeEvaluationVisitor<String> getStringValueVisitor = new ExtractValueVisitor();

    @Override
    public List<org.w3c.dom.Node> visit(final SimpleNode node, final org.w3c.dom.Node data) {

        switch (node.getID()) {
        case JJTSTART:
        case JJTXPATH:
        case JJTEXPR:
        case JJTFUNCTIONCALL:
            return node.firstChildAccept(this, data);
        case JJTPATHEXPR:
            return node.allChildrenAccept(this, data);
        case JJTSLASH:
            return DOMHelper.<Node> asList(DOMHelper.getOwnerDocumentFor(data));
        case JJTSLASHSLASH:
            throw new XBXPathExprNotAllowedForWriting(node, "Ambiguous target path. You can not use '//' for writing.");
        case JJTSTEPEXPR:
            List<? extends org.w3c.dom.Node> possibleNodes = node.firstChildAccept(this, data);
            return null;
        case JJTABBREVFORWARDSTEP:
            String name = node.firstChildAccept(getStringValueVisitor, data);
            if ("@".equals(node.getValue())) {
                assertNodeType(data, org.w3c.dom.Node.ELEMENT_NODE);
                return DOMHelper.<Node> asList(((Element) data).getAttributeNode(name));
            }

            return DOMHelper.getChildrendByName(data, name);
        case JJTFUNCTIONQNAME:
        case JJTUNIONEXPR:
            throw new XBXPathExprNotAllowedForWriting(node, "You need to specify a singel XPath expression.");
        case JJTSTRINGLITERAL:
        case JJTINTEGERLITERAL:
        case JJTDECIMALLITERAL:
        case JJTDOUBLELITERAL:
        case JJTADDITIVEEXPR:
        case JJTMULTIPLICATIVEEXPR:
        case JJTCOMPARISONEXPR:
        case JJTOREXPR:
        case JJTANDEXPR:
        case JJTFORWARDAXIS:
        case JJTREVERSEAXIS:
        case JJTABBREVREVERSESTEP:
        case JJTCONTEXTITEMEXPR:
        case JJTPARAMLIST:
        case JJTPARAM:
        case JJTENCLOSEDEXPR:
        case JJTLBRACE:
        case JJTRBRACE:
        case JJTVOID:
        case JJTFOREXPR:
        case JJTSIMPLEFORBINDING:
        case JJTLETEXPR:
        case JJTSIMPLELETCLAUSE:
        case JJTSIMPLELETBINDING:
        case JJTQUANTIFIEDEXPR:
        case JJTIFEXPR:
        case JJTRANGEEXPR:
        case JJTINTERSECTEXCEPTEXPR:
        case JJTINSTANCEOFEXPR:
        case JJTTREATEXPR:
        case JJTCASTABLEEXPR:
        case JJTCASTEXPR:
        case JJTUNARYEXPR:
        case JJTMINUS:
        case JJTPLUS:
        case JJTNODETEST:
        case JJTNAMETEST:
        case JJTWILDCARD:
        case JJTNCNAMECOLONSTAR:
        case JJTSTARCOLONNCNAME:
        case JJTPREDICATELIST:
        case JJTPREDICATE:
        case JJTVARNAME:
        case JJTPARENTHESIZEDEXPR:
        case JJTFUNCTIONITEMEXPR:
        case JJTLITERALFUNCTIONITEM:
        case JJTINLINEFUNCTION:
        case JJTDYNAMICFUNCTIONINVOCATION:
        case JJTSINGLETYPE:
        case JJTTYPEDECLARATION:
        case JJTSEQUENCETYPE:
        case JJTOCCURRENCEINDICATOR:
        case JJTITEMTYPE:
        case JJTATOMICTYPE:
        case JJTANYKINDTEST:
        case JJTDOCUMENTTEST:
        case JJTTEXTTEST:
        case JJTCOMMENTTEST:
        case JJTNAMESPACENODETEST:
        case JJTPITEST:
        case JJTATTRIBUTETEST:
        case JJTATTRIBNAMEORWILDCARD:
        case JJTSCHEMAATTRIBUTETEST:
        case JJTATTRIBUTEDECLARATION:
        case JJTELEMENTTEST:
        case JJTELEMENTNAMEORWILDCARD:
        case JJTSCHEMAELEMENTTEST:
        case JJTELEMENTDECLARATION:
        case JJTATTRIBUTENAME:
        case JJTELEMENTNAME:
        case JJTTYPENAME:
        case JJTFUNCTIONTEST:
        case JJTANYFUNCTIONTEST:
        case JJTTYPEDFUNCTIONTEST:
        case JJTPARENTHESIZEDITEMTYPE:
        case JJTNCNAME:
        case JJTQNAME:
        default:
            break;
        }
        throw new IllegalStateException("Unknown Node " + node);

    }

    /**
     * @param data
     * @param type
     */
    private void assertNodeType(final Node data, final short type) {
        if (data.getNodeType() != type) {
            throw new XBException("Can not evaluate xpath", new XPathExpressionException("Expected node type " + type + " but got " + data.getNodeType()));
        }

    }
}
