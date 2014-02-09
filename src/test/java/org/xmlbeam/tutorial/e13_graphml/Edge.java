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
 *    Created on  12.04.2013
 *
 ************************************************************************/
package org.xmlbeam.tutorial.e13_graphml;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

//START SNIPPET: Edge
@XBDocURL("resource://EdgeTemplate.xml")
public interface Edge {
    @XBRead("/edge")
    Edge rootElement();
    
    @XBWrite("/g:edge/@id")
    Edge setID(String id);

    @XBWrite("/g:edge/@source")
    Edge setSource(String id);

    @XBWrite("/g:edge/@target")
    Edge setTarget(String id);

    @XBRead("/g:edge/@id")
    String getID();

}
//END SNIPPET: Edge