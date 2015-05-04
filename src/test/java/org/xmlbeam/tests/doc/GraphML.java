/************************************************************************
 *                                                                      *
 *  DDDD     SSSS    AAA        Daten- und Systemtechnik Aachen GmbH    *
 *  D   D   SS      A   A       Pascalstrasse 28                        *
 *  D   D    SSS    AAAAA       52076 Aachen-Oberforstbach, Germany     *
 *  D   D      SS   A   A       Telefon: +49 (0)2408 / 9492-0           *
 *  DDDD    SSSS    A   A       Telefax: +49 (0)2408 / 9492-92          *
 *                                                                      *
 *                                                                      *
 *  (c) Copyright by DSA - all rights reserved                          *
 *                                                                      *
 ************************************************************************
 *
 * Initial Creation:
 *    Author      se
 *    Created on  08.04.2013
 *
 ************************************************************************/
package org.xmlbeam.tests.doc;

import java.util.List;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;

@SuppressWarnings("javadoc")
@XBDocURL("resource://XBProjector-Sheet-compact.graphml")
public interface GraphML {

    @XBRead("//xbdefaultns:edge[@target='{0}']/@source")
    String getParentOf(String node);

    @XBRead(value = "//xbdefaultns:edge[@source='{0}']/@target")
    List<String> getChildrenOf(String node);

    @XBRead("//xbdefaultns:node[@id='{0}']")
    Node getNode(String id);

    @XBRead("//xbdefaultns:node[normalize-space(//y:NodeLabel)='new XBProjector()']")
    Node getRootNode();
}
