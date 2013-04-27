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
package org.xmlbeam.tutorial;

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

import org.xmlbeam.util.IOHelper;

/**
 * @author se
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
                    accept.setSoTimeout(5000);
                    String requestHeader = new Scanner(accept.getInputStream()).useDelimiter("(?m)\\r\\n\\r\\n").next();
                    String url = findURL(requestHeader);
                    File file = new File(JUnitHttpProxy.class.getSimpleName() + "." + URLEncoder.encode(url, "UTF-8") + ".tmp");
                    if (file.exists()) {
                        String content = IOHelper.inputStreamToString(new FileInputStream(file), "UTF-8");
                        accept.getOutputStream().write(content.getBytes());
                        continue;
                    }
                    try {
                        restoreProxySettings();
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.setReadTimeout(5000);
                        String string = new Scanner(connection.getInputStream()).useDelimiter("\\A").next();
                        FileOutputStream fileStream = new FileOutputStream(file);
                        fileStream.write(string.getBytes("UTF-8"));
                        fileStream.flush();
                        fileStream.close();
                        accept.getOutputStream().write(string.getBytes("UTF-8"));
                    } finally {
                        setAsProxy();
                    }
                } finally {
                    accept.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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