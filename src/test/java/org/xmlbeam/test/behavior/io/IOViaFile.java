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
package org.xmlbeam.test.behavior.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.xmlbeam.XBProjector;

/**
 *
 */
public class IOViaFile {

    public void ensureFileReading() throws Exception {
// new XBProjector().create().fromScratch(projectionInterface.class);
// new XBProjector().create().fromString(xmlString,projectionInterface.class);
// new XBProjector().create().fromFile(file,projectionInterface.class);
// new XBProjector().create().fromURL(url,projectionInterface.class);
// new XBProjector().create().fromURLAnnotation(projectionInterface.class);
// new XBProjector().create().fromURLStream(stream,projectionInterface.class);
// new XBProjector().create().fromDOM(node,projectionInterface.class);
//
// new XBProjector().project(projectionInterface);
// new XBProjector().project().url(...).to(projectionInterface)
// new XBProjector().project().nothing().to(projectionInterface)
// new XBProjector().project().file(...).to(projectionInterface)
// new XBProjector().project().domNode().to(projectionInterface)
//
// new XBProjector().writeXML().toFile(file).appending().from(projection)
// new XBProjector().writeXML().toUrl(url).withRequestProperties(props).from(projection);
// new XBProjector().writeXML().from(projection)
//
// new XBProjector().write(projection).withRequestProperties(props).toUrl(url);
// new XBProjector().write(projection).appending().toFile();
//
//

        File file = null;
        Class<Object> projectionInterface = null;
        Object projection = null;
        String a = null, b = null;
        InputStream is = null;
        OutputStream os = null;
        String url = null;
        Object[] replacements = null;
        Map<String, String> params;
        String name=null;
        Object node=null;
        
        new XBProjector().create().createEmptyDocumentProjection(projectionInterface);

        new XBProjector().create().createEmptyElementProjection(name, projectionInterface);
//        new XBProjector().create().parseXMLString("<xml/>", projectionInterface);
//        
//        new XBProjector().create().fromDOM(node, projectionInterface);

        new XBProjector().io().file(file).read(projectionInterface);
        new XBProjector().io().file(file).setAppend(true).write(projection);

        new XBProjector().io().fromURLAnnotation(projectionInterface, replacements);
        new XBProjector().io().toURLAnnotationViaPOST(projectionInterface, replacements);

        new XBProjector().io().url(url).read(projectionInterface);
        new XBProjector().io().url(url).addRequestParam(a, b).read(projectionInterface);
        new XBProjector().io().url(url).write(projectionInterface);

        new XBProjector().io().stream(is).read(projectionInterface);
        new XBProjector().io().stream(os).write(projection);

// //&&new XBProjector().io().url().addRequestParams(params).
// new XBProjector().project().fromDOM()
// new XBProjector().createProjection().fromFile()
// Map<String, String> params;
// XBUrlIO urlIO = new XBProjector().io().url().addRequestParams(params);
// Object projection = urlIO.getFromURL(uri, projectionInterface);
// urlIO.postToURL(projection, httpurl);
    }

}
