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

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;

import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.DefaultXMLFactoriesConfig.NamespacePhilosophy;

public class RunExample {

    private static final Map<Vector, Vector> HMAP = new HashMap<Vector, Vector>();

    public static class Vector {
        public float x, y, z;

        public Vector(float i, float j, float k) {
            x = i;
            y = j;
            z = k;
        }

        @Override
        public String toString() {
            return x + " " + y + " " + z;
        }
    }

    public static class Vertex {
        public Vertex(Vector p,Vector n) {
            this.position=p;
            this.normal=n;
        }
        public Vertex(float i, float j, float k, float nx, float ny, float nz) {
            position = new Vector(i, j, k);
            normal = new Vector(nx, ny, nz);
        }

        public Vector position;
        public Vector normal;
    }

    public static class Triangle {
        public Vertex a, b, c;

        public Triangle(Vertex a, Vertex b, Vertex c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    private static void addVertex(Xml3d mesh, Vertex v) {
        int newIndex = 0;
        String oldIndexes = mesh.getIndexes();
        if (!oldIndexes.isEmpty()) {
            List<String> indexes = new ArrayList(Arrays.asList(oldIndexes.split(" ")));
            newIndex = Integer.parseInt(indexes.get(indexes.size() - 1)) + 1;
        }
        String newIndexes = oldIndexes + (oldIndexes.endsWith(" ") ? "" : " ") + newIndex;
        mesh.setIndexes(newIndexes);

        String oldPositions = mesh.getPositions();
        String newPositions = oldPositions + " " + v.position;
        mesh.setPositions(newPositions);

        String oldNormals = mesh.getNormals();
        String newNormals = oldNormals + " " + v.normal;
        mesh.setNormals(newNormals);

    }

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8088);
        while (true) {
            Socket s=ss.accept();
            XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
            projector.config().as(DefaultXMLFactoriesConfig.class).setNamespacePhilosophy(NamespacePhilosophy.NIHILISTIC);
            Xml3d xml3d = projector.io().fromURLAnnotation(Xml3d.class);
            Triangle t=new Triangle(new Vertex(-1, -1, -4, 0, 0, 1), new Vertex(1, -1, -4, 0, 0, 1), new Vertex(0, 1, -4, 0, 0, 1));
            spanTriangles(xml3d,t,0);
            //addTriangle(xml3d,t);
            // String page = new Scanner(RunExample.class.getResourceAsStream("test.html")).useDelimiter("\\A").next();
            String page = xml3d.toString();
            String header = "HTTP/1.0 200 OK\r\nContent-Type: application/xhtml+xml\r\nContent-Length: " + page.getBytes("UTF-8").length + "\r\n\r\n";            
            s.getOutputStream().write((header + page).getBytes("UTF-8"));
            s.getOutputStream().flush();
            s.close();
            s=null;
        }
    }

    /**
     * @param xml3d
     * @param t
     * @param i
     */
    private static void spanTriangles(Xml3d xml3d, Triangle t, int i) {
        if (i==3) {
            addTriangle(xml3d, t);
            return;
        }
        Vertex ac=middle(t.a,t.c);
        Vertex ab=middle(t.a,t.b);
        Vertex bc=middle(t.b,t.c);
        spanTriangles(xml3d, new Triangle(t.a, ac, ab), i+1);
        spanTriangles(xml3d, new Triangle(t.b, ab, bc), i+1);        
        spanTriangles(xml3d, new Triangle(t.c, bc, ac), i+1);
        spanTriangles(xml3d, new Triangle(ac, bc, ab), i+1);
    }

    private static Vertex middle(Vertex a, Vertex b) {
        Vector position=new Vector((a.position.x+b.position.x)/2,(a.position.y+b.position.y)/2,0f);
        if (!HMAP.containsKey(position)) {
            position.z = (float) (0f * Math.random() + (a.position.y + b.position.y) / 2);
            HMAP.put(new Vector((a.position.x+b.position.x)/2,(a.position.y+b.position.y)/2,0f), position);
        } else {
            position = HMAP.get(position);
        }
        return new Vertex(position,new Vector(0,0,1));
    }

    private static void addTriangle(Xml3d xml3d, Triangle t) {
        addVertex(xml3d, t.a);
        addVertex(xml3d, t.b);
        addVertex(xml3d, t.c);
    }
}
