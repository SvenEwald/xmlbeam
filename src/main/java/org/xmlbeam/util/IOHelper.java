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

import java.net.HttpURLConnection;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class IOHelper {

    /**
     * @param inputStream
     * @return
     */
    public static String inputStreamToString(InputStream inputStream, String encoding) {
        return new Scanner(inputStream, encoding).useDelimiter("\\A").next();
    }

    
    public static String httpPost(String httpurl, String data, Map<String, String> requestProperties) throws IOException {
        byte[] bytes = data.getBytes("utf-8");
        java.net.URL url = new java.net.URL(httpurl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(bytes.length));
        if (requestProperties != null) {
            for (Entry<String, String> entry : requestProperties.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        String response = IOHelper.inputStreamToString(connection.getInputStream(), connection.getContentEncoding());
        connection.disconnect();
        return response;
    }
}
