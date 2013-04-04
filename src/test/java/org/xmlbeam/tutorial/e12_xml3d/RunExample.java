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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.DefaultXMLFactoriesConfig.NamespacePhilosophy;

/**
 * @author sven
 */
public class RunExample {

    public static class Vector {
        public float x,y,z;

        @Override
        public String toString() {
            return x + " " + y + " " + z;
        }        
    }
    
    public static class Vertex {
        public Vector position;
        public Vector normal;
    }
    
    
    private void addVertex(Xml3d mesh,Vertex v) {
        List<String> indexes=new ArrayList(Arrays.asList(mesh.getIndexes().split(" ")));
    }
    
    public static void main(String[] args) throws IOException {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
        projector.config().as(DefaultXMLFactoriesConfig.class).setNamespacePhilosophy(NamespacePhilosophy.NIHILISTIC);
        Xml3d xml3d = projector.io().fromURLAnnotation(Xml3d.class);
        xml3d.getIndexes();
        xml3d.getNormals();
        xml3d.getPositions();

        
        
        ServerSocket ss = new ServerSocket(8088);
        while (true) {
            String page = new Scanner(RunExample.class.getResourceAsStream("test.html")).useDelimiter("\\A").next();
            String header = "HTTP/1.0 200 OK\r\nContent-Type: application/xhtml+xml\r\nContent-Length: " + page.getBytes("UTF-8").length + "\r\n\r\n";
            ss.accept().getOutputStream().write((header + page).getBytes("UTF-8"));
        }
    }
}
