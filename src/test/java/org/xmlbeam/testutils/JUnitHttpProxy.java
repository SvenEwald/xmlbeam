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
package org.xmlbeam.testutils;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlbeam.util.IOHelper;

/**
 * This proxy allows unit tests to do external HTTP requests without generating huge traffic.
 * The content is fetched just once and then reused forever. This ensures independence of external 
 * server availability for the test results while still working with real live data.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class JUnitHttpProxy implements Runnable {

    private ServerSocket serverSocket;
    private Thread listenThread;
    private String origProxyHost;
    private String origProxyPort;

    /**
     * @param string
     */
    public JUnitHttpProxy() {
        try {
            this.serverSocket = new ServerSocket(0, 5, InetAddress.getLocalHost());
            this.listenThread = new Thread(this);
            this.listenThread.setDaemon(true);
            this.listenThread.setName(JUnitHttpProxy.class.getSimpleName() + ".listenThread");
            this.listenThread.start();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            while (true) {
                Socket accept = serverSocket.accept();
                try {
                    accept.setSoTimeout(15000);
                    String requestHeader = new Scanner(accept.getInputStream()).useDelimiter("(?m)\\r\\n\\r\\n").next();
                    swallow(accept.getInputStream());
                    String url = findURL(requestHeader);
                    File file = new File(JUnitHttpProxy.class.getSimpleName() + "." + URLEncoder.encode(url, "UTF-8") + ".tmp");
                    if (file.exists()) {
                        byte[] content = IOHelper.dropUTF8BOM(IOHelper.inputStreamToString(new FileInputStream(file), "UTF-8").getBytes("UTF-8"));
                        String header = "HTTP/1.1 200 OK\r\nConnection: Close\r\nContent-Type: application/xml\r\nContent-Length: " + content.length + "\r\n\r\n";
                        accept.getOutputStream().write(IOHelper.dropUTF8BOM(header.getBytes("UTF-8")));
                        accept.getOutputStream().write(content);
                        accept.getOutputStream().flush();
                        continue;
                    }
                    try {
                        restoreProxySettings();
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.setReadTimeout(15000);
                        String string = new Scanner(connection.getInputStream()).useDelimiter("\\A").next();
                        FileOutputStream fileStream = new FileOutputStream(file);
                        byte[] bytes = IOHelper.dropUTF8BOM(string.getBytes("UTF-8"));
                        fileStream.write(bytes);
                        fileStream.flush();
                        fileStream.close();
                        String header = "HTTP/1.1 200 OK\r\nConnection: Close\r\nContent-Type: application/xml\r\nContent-Length: " + bytes.length + "\r\n\r\n";
                        accept.getOutputStream().write(IOHelper.dropUTF8BOM(header.getBytes("UTF-8")));
                        accept.getOutputStream().write(bytes);
                    } finally {
                        setAsProxy();
                    }
                } finally {
                    try {
                        accept.close();
                    } catch (IOException e) {    
                        e.printStackTrace();
                    }                    
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param inputStream
     * @throws IOException
     */
    private void swallow(InputStream inputStream) throws IOException {
        while (inputStream.available() > 0) {
            inputStream.read();
        }
    }

    /**
     * @param request
     * @return
     */
    private String findURL(String request) {
        Matcher matcher = Pattern.compile("GET (.*) HTTP/1\\..").matcher(request);
        if (!matcher.find()) {
            throw new IllegalArgumentException();
        }
        return matcher.group(1);
    }

    public void restoreProxySettings() {
        if (origProxyHost == null) {
            System.clearProperty("http.proxyHost");
            System.clearProperty("http.proxyPort");
            return;
        }
        System.setProperty("http.proxyHost", origProxyHost);
        System.setProperty("http.proxyPort", origProxyPort);
        origProxyHost = null;
    }

    /**
     * 
     */
    public void stop() {
        restoreProxySettings();
        listenThread.interrupt();
    }

    public void setAsProxy() {
        origProxyHost = System.getProperty("http.proxyHost");
        origProxyPort = System.getProperty("http.proxyPort");
        System.setProperty("http.proxyHost", serverSocket.getInetAddress().getHostAddress());
        System.setProperty("http.proxyPort", Integer.toString(serverSocket.getLocalPort()));
    }

}
