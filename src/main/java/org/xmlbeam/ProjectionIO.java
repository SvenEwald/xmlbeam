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
package org.xmlbeam;

import java.net.URISyntaxException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.io.XBFileIO;
import org.xmlbeam.io.XBStreamInput;
import org.xmlbeam.io.XBStreamOutput;
import org.xmlbeam.io.XBUrlIO;

/**
 * A ProjectionIO is responsible for every IO operation related to projections. Before coding your
 * own IO implementation, you should have a look at the tutorials for usage examples.
 */
public interface ProjectionIO {

    /**
     * Get access to the file IO.
     * @param file
     * @return a XBFileIO for this file.
     */
    XBFileIO file(File file);

    
    /**
     * Get access to the file IO.
     * @param fileName
     * @return a XBFileIO for this filename.
     */
    XBFileIO file(String fileName);

    /**
     * Get access to the url IO.
     * @param url
     * @return a XBUrlIO for this url
     */
    XBUrlIO url(String url);
    
    /**
     * Get access to the stream IO
     * @param is
     * @return a XBStreamInput for this InputStream
     */
    XBStreamInput stream(InputStream is);

    /**
     * Get access to the stream IO
     * @param os
     * @return a XBStreamOutput for this OutputStream
     */
    XBStreamOutput stream(OutputStream os);

    /**
     * Create a new projection using a {@link XBDocURL} annotation on this interface. When the
     * XBDocURL starts with the protocol identifier "resource://" the class loader of the projection
     * interface will be used to read the resource from the current class path.
     * 
     * @param projectionInterface
     *            a public interface.
     * @param optionalParams 
     * @return a new projection instance
     * @throws IOException
     */
    <T> T fromURLAnnotation(final Class<T> projectionInterface, Object... optionalParams) throws IOException;

    /**
     * Write projection document to url (file or http post) of {@link XBDocURL} annotation.
     * 
     * @param projection
     * @param optionalParams 
     * @return response of http post or null for file urls.
     * @throws IOException
     * @throws URISyntaxException
     */
    String toURLAnnotationViaPOST(final Object projection, Object... optionalParams) throws IOException, URISyntaxException;

}