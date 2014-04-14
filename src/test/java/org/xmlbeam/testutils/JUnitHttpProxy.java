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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlbeam.util.IOHelper;

/**
 * This proxy allows unit tests to do external HTTP requests without generating huge traffic. The
 * content is fetched just once and then reused forever. This ensures independence of external
 * server availability for the test results while still working with real live data.
 * This class cannot be subclassed (Thread.start in constructor).
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public final class JUnitHttpProxy implements Runnable {

    private ServerSocket serverSocket;
    private Thread listenThread;
    private String origProxyHost;
    private String origProxyPort;

    /**
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
                final Socket accept = serverSocket.accept();
                new Thread() {
                    {
                        setDaemon(true);
                    }
                    public void run() {
                        
                        try {
                            accept.setSoTimeout(15000);
                            String requestHeader = new Scanner(accept.getInputStream()).useDelimiter("(?m)\\r\\n\\r\\n").next();
                            swallow(accept.getInputStream());
                            String url = findURL(requestHeader);
                            byte[] content = resolveURLContent(url);
                            dropToHTTPClient(accept, content);
                        } catch (SocketException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                accept.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        
                    };
                }.start();
                
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
     * @param url
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     */
    private byte[] resolveURLContent(String url) throws FileNotFoundException, IOException, MalformedURLException, UnsupportedEncodingException {
        final File file = new File(JUnitHttpProxy.class.getSimpleName() + "." + URLEncoder.encode(url, "UTF-8") + ".tmp");
        byte[] content;
        if (file.exists()) {
            content = new byte[ (int)file.length()];
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(content);
            inputStream.close();
          //  System.out.println("Load " + content.length + " bytes");
            return content;
        }try {
            restoreProxySettings();
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(15000);
            connection.getResponseCode();
            content = inputStreamToByteArray(connection.getInputStream());
           // System.out.println("Download " + content.length + " bytes " + " encoding:" + connection.getContentEncoding());
            FileOutputStream fileStream = new FileOutputStream(file);
            fileStream.write(content);
            fileStream.flush();
            fileStream.close();
            return content;
        } finally {
            setAsProxy();
        }
        
    }

    /**
     * @param inputStream
     * @return
     * @throws IOException
     */
    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bos= new ByteArrayOutputStream();        
        while (true) {
            int count = inputStream.read(buffer);  
            if (count<0) {
                break;
            }            
            if (count>0) {                
                bos.write(buffer, 0, count);                
            }
            if ((count==buffer.length)&&(buffer.length<64*1024)) {
                buffer=new byte[buffer.length*2];
            }
        }                
        return bos.toByteArray();
    }

    /**
     * @param accept
     * @param bytes
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private void dropToHTTPClient(Socket accept, byte[] content) throws IOException, UnsupportedEncodingException {
    //    System.out.println("Dropping " + content.length + " bytes.");
        String header = "HTTP/1.1 200 OK\r\nConnection: Close\r\nContent-Type: application/xml\r\nContent-Length: " + content.length + "\r\n\r\n";
        accept.getOutputStream().write(IOHelper.dropUTF8BOM(header.getBytes("UTF-8")));
        accept.getOutputStream().write(content);
        accept.getOutputStream().flush();
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
    
    /**
     * 
     */
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

    /**
     * 
     */
    public void setAsProxy() {
        origProxyHost = System.getProperty("http.proxyHost");
        origProxyPort = System.getProperty("http.proxyPort");
        System.setProperty("http.proxyHost", serverSocket.getInetAddress().getHostAddress());
        System.setProperty("http.proxyPort", Integer.toString(serverSocket.getLocalPort()));
    }

}
