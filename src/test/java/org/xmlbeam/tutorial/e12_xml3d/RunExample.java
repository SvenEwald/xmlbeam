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
import java.util.List;

import java.io.IOException;

import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.DefaultXMLFactoriesConfig.NamespacePhilosophy;

/* START SNIPPET: TutorialDescription
~~
END SNIPPET: TutorialDescription */

public class RunExample {

    public static class Triplet<T> {
        public T a, b, c;

        public Triplet(T a, T b, T c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public String toString() {
            return a + " " + b + " " + c;
        }
    }

    public static class Vector extends Triplet<Float> {
        public Vector(Float a, Float b, Float c) {
            super(a, b, c);
        }
    }

    public static class Vertex extends Triplet<Vector> {
        public Vertex(Vector a, Vector b, Vector c) {
            super(a, b, c);
        }
    }

    public static class Triangle extends Triplet<Vertex> {
        public Triangle(Vertex a, Vertex b, Vertex c) {
            super(a, b, c);
        }
    }
    
    private static Vertex middle(Vertex a, Vertex b) {
        Vector position = new Vector((a.a.a + b.a.a) / 2, (a.a.b + b.a.b) / 2, (a.a.c + b.a.c) / 2);
        return new Vertex(position, new Vector(0f, 0f, 1f), null);
    }

    private static void addVertex(Xml3d mesh, Vertex v) {
        int newIndex = 0;
        String oldIndexes = mesh.getIndexes();
        if (!oldIndexes.isEmpty()) {
            List<String> indexes = new ArrayList<String>(Arrays.asList(oldIndexes.split(" ")));
            newIndex = Integer.parseInt(indexes.get(indexes.size() - 1)) + 1;
        }
        String newIndexes = oldIndexes + (oldIndexes.endsWith(" ") ? "" : " ") + newIndex;
        mesh.setIndexes(newIndexes);

        String oldPositions = mesh.getPositions();
        String newPositions = oldPositions + " " + v.a;
        mesh.setPositions(newPositions);

        String oldNormals = mesh.getNormals();
        String newNormals = oldNormals + " " + v.b;
        mesh.setNormals(newNormals);
    }

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8088);
        while (true) {
            Socket s = ss.accept();
            XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
            projector.config().as(DefaultXMLFactoriesConfig.class).setNamespacePhilosophy(NamespacePhilosophy.NIHILISTIC);
            Xml3d xml3d = projector.io().fromURLAnnotation(Xml3d.class);
            Vector normal = new Vector(0f, 0f, 1f);
            for (float f = -2f; f <= 2f; f += 1f) {
                Triangle t1 = new Triangle(new Vertex(new Vector(-1f, -1f, f), normal, null), new Vertex(new Vector(1f, -1f, f), normal, null), new Vertex(new Vector(0f, 1f, f), normal, null));
                spanTriangles(xml3d, t1, 2+(int)f);
            }
            String page = xml3d.toString();
            String header = "HTTP/1.0 200 OK\r\nContent-Type: application/xhtml+xml\r\nContent-Length: " + page.getBytes("UTF-8").length + "\r\n\r\n";
            s.getOutputStream().write((header + page).getBytes("UTF-8"));
            s.getOutputStream().flush();
            s.close();
        }
    }

    private static void spanTriangles(Xml3d xml3d, Triangle t, int i) {
        if (i == 0) {
            addTriangle(xml3d, t);
            return;
        }
        Vertex ac = middle(t.a, t.c);
        Vertex ab = middle(t.a, t.b);
        Vertex bc = middle(t.b, t.c);
        spanTriangles(xml3d, new Triangle(t.a, ac, ab), i - 1);
        spanTriangles(xml3d, new Triangle(t.b, ab, bc), i - 1);
        spanTriangles(xml3d, new Triangle(t.c, bc, ac), i - 1);
    }

    private static void addTriangle(Xml3d xml3d, Triangle t) {
        addVertex(xml3d, t.a);
        addVertex(xml3d, t.b);
        addVertex(xml3d, t.c);
    }
}
