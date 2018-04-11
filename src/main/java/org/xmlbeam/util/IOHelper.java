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
package org.xmlbeam.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlbeam.XBProjector;
import org.xmlbeam.exceptions.XBDocumentParsingException;
import org.xmlbeam.exceptions.XBException;
import org.xmlbeam.exceptions.XBIOException;

/**
 * A set of tiny helper methods used in the projection framework and free to use for framework
 * clients. This methods are part of the public framework API and will not change in minor version
 * updates.
 *
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public final class IOHelper {

    private static final String[] RESOURCE_PROTO_NAMES = new String[] { "resource://", "res://" };

    /**
     * Copies request properties to a connection.
     *
     * @param requestProperties
     *            (if null, connection will not be changed)
     * @param connection
     */
    private static void addRequestProperties(final Map<String, String> requestProperties, final HttpURLConnection connection) {
        if (requestProperties != null) {
            for (Entry<String, String> entry : requestProperties.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Create HTTP Basic credentials to be used in HTTP get or post methods.
     *
     * @param username
     * @param password
     * @return Map containing
     */
    public static Map<String, String> createBasicAuthenticationProperty(final String username, final String password) {
        Map<String, String> map = new TreeMap<String, String>();
        try {
            String base64Binary = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes("US-ASCII"));
            map.put("Authorization", "Basic " + base64Binary);
        } catch (UnsupportedEncodingException e) {
            // unreachable code
            throw new RuntimeException(e);
        }
        return map;
    }

    /**
     * Simple http get imlementation. Supports HTTP Basic authentication via request properties. You
     * may want to use {@link #createBasicAuthenticationProperty} to add authentication.
     *
     * @param httpurl
     *            get url
     * @param requestProperties
     *            optional http header fields (key-&gt;value)
     * @return input stream of response
     * @throws IOException
     */
    public static InputStream httpGet(final String httpurl, final Map<String, String>... requestProperties) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(httpurl).openConnection();
        for (Map<String, String> props : requestProperties) {
            addRequestProperties(props, connection);
        }
        return connection.getInputStream();
    }

    /**
     * Simple http post implementation. Supports HTTP Basic authentication via request properties.
     * You may want to use {@link #createBasicAuthenticationProperty} to add authentication.
     *
     * @param httpurl
     *            target url
     * @param data
     *            String with content to post
     * @param requestProperties
     *            optional http header fields (key-&gt;value)
     * @return input stream of response
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public static InputStream httpPost(final String httpurl, final String data, final Map<String, String>... requestProperties) throws IOException {
        byte[] bytes = data.getBytes("utf-8");
        java.net.URL url = new java.net.URL(httpurl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(bytes.length));
        for (Map<String, String> props : requestProperties) {
            addRequestProperties(props, connection);
        }
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        return connection.getInputStream();
    }

    /**
     * @param inputStream
     * @param optionalCharsetName
     *            optional parameter
     * @return String with stream content
     */
    public static String inputStreamToString(final InputStream inputStream, final String... optionalCharsetName) {
        Scanner scanner = (optionalCharsetName == null) || (optionalCharsetName.length == 0) || (optionalCharsetName[0] == null) ? new Scanner(inputStream) : new Scanner(inputStream, optionalCharsetName[0]);
        // return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        String content = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        return content;
    }

    /**
     * Silently drop UTF8 BOM
     *
     * @param source
     * @return data without UTF8 BOM
     */
    public static byte[] dropUTF8BOM(final byte[] source) {
        if (source == null) {
            return null;
        }
        if (source.length < 3) {
            return source;
        }
        if ((source[0] == (byte) 0xef) && (source[1] == (byte) 0xbb) && (source[2] == (byte) 0xbf)) {
            return Arrays.copyOfRange(source, 3, source.length);
        }
        return source;
    }

    /**
     * @param projector 
     * @param is
     *            anonymous input stream
     * @return Document
     */
    public static Document loadDocument(final XBProjector projector, final InputStream is) {
        final DocumentBuilder documentBuilder = projector.config().createDocumentBuilder();
        try {
            return documentBuilder.parse(is, "");
        } catch (SAXException e) {
            throw new XBDocumentParsingException(e);
        } catch (IOException e) {
            throw new XBIOException("Error during document loading",e);
        }
    }

    /**
     * @param documentBuilder
     * @param url
     * @param requestProperties
     * @param resourceAwareClasses
     *            Try useing this classes to load resource with resource protocol.
     * @return new document instance
     * @throws IOException
     */
    @SuppressWarnings({ "unchecked", "resource" })
    public static Document getDocumentFromURL(final DocumentBuilder documentBuilder, final String url, final Map<String, String> requestProperties, final Class<?>... resourceAwareClasses) throws IOException {
        try {
            for (String resProto : RESOURCE_PROTO_NAMES) {
                if (url.startsWith(resProto)) {
                    final String resourceName = url.substring(resProto.length());
                    InputStream is = null;
                    // If possible use class loader of given classes.
                    if (resourceAwareClasses != null) {
                        for (Class<?> clazz : resourceAwareClasses) {
                            if (clazz == null) {
                                continue;
                            }
                            is = clazz.getResourceAsStream(resourceName);
                            if (is != null) {
                                break;
                            }
                        }
                    }

                    // Fallback to context class loader
                    if (is == null) {
                        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
                    }

                    if (is == null) {
                        throw new IOException("The resource '" + url + "' could not be found");
                    }
                    InputSource source = new InputSource(is);
                    // source.setEncoding("MacRoman");
                    return documentBuilder.parse(source);
                }
            }
            if (url.startsWith("http:") || url.startsWith("https:")) {
                return documentBuilder.parse(IOHelper.httpGet(url, requestProperties), url);
            }
            Document document = documentBuilder.parse(url);
            if (document == null) {
                throw new IOException("Document could not be created form uri " + url);
            }
            return document;
        } catch (SAXException e) {
            throw new XBDocumentParsingException(e);
        }
    }

    /**
     * @param url
     * @return true if the protocol needs a class loader
     */
    public static boolean isResourceProtocol(final String url) {
        if (url == null) {
            return false;
        }
        for (String proto : RESOURCE_PROTO_NAMES) {
            if (url.startsWith(proto)) {
                return true;
            }
        }
        return false;
    }
}
