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
package org.xmlbeam.tutorial.e14_kml;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
@SuppressWarnings("javadoc")
public class CoordinateList implements Iterable<Coordinate> {

    private final List<Coordinate> coordinates = new LinkedList<Coordinate>();

    public CoordinateList(String data) {
        for (String s : data.trim().split("\\s+")) {
            if (s.isEmpty()) {
                continue;
            }
            Coordinate coordinate = new Coordinate(Double.parseDouble(s.split(",")[0]), Double.parseDouble(s.split(",")[1]));
            coordinates.add(coordinate);
        }
    }

    @Override
    public Iterator<Coordinate> iterator() {
        return coordinates.iterator();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Coordinate xy : this) {            
            s.append(xy.getX()).append(",").append(xy.getY()).append(" ");
        }
        return s.toString().trim();
    }
}
