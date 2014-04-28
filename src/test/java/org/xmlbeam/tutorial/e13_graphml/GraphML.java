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
package org.xmlbeam.tutorial.e13_graphml;

import java.util.List;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;
@SuppressWarnings("javadoc")
//START SNIPPET: GraphML
@XBDocURL("resource://GraphMLTemplate.xml")
public interface GraphML {

    @XBWrite("/g:graphml/g:graph/g:node[@id='{0}']")
    GraphML addNode(String id, @XBValue Node node);

    @XBWrite("/g:graphml/g:graph/g:edge[@id='{0}']")
    GraphML addEdge(String id, @XBValue Edge edge);

    @XBRead("//g:edge[@target='{0}']/@source")
    String getParentOf(String node);

    @XBRead(value="//g:edge[@source='{0}']/@target")
    List<String> getChildrenOf(String node);

    @XBRead("//g:node[@id='{0}']")
    Node getNode(String id);
    
    @XBRead(value="//g:node")
    List<Node> getAllNodes();
    
    @XBRead("{0}")
    String xpath(String path);
    
}
//END SNIPPET: GraphML