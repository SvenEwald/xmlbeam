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
package org.xmlbeam.tutorial.e12_xml3d;

import java.io.File;
import java.io.IOException;

/**
 * @author sven
 *
 */
public class RunExample extends NanoHTTPD {

    /**
     * @param port
     * @param wwwroot
     * @throws IOException
     */
    public RunExample() throws IOException {
        super(8088, new File("/Users/sven/git/xmlbeam/src/test/java/org/xmlbeam/tutorial/xml3d"));
    }

    public static void main(String[] args) {
        try {
            new RunExample();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
