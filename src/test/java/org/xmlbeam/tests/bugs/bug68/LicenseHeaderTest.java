/**
 *  Copyright 2025 Sven Ewald
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
package org.xmlbeam.tests.bugs.bug68;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class LicenseHeaderTest {
    private static final String LICENSE_HEADER = "Licensed under the Apache License, Version 2.0";
    private static final String GPL_HEADER = "Free Software Foundation" + ", either version 3";
    private static final int HEADER_LINES_TO_CHECK = 20;
    private static final Predicate<Path> IS_JAVA_FILE = new Predicate<Path>() {

        @Override
        public boolean test(Path p) {
            return p.toString().endsWith(".java");
        }
    };

    @Test
    public void testAllJavaFileHeaders() throws IOException {
        List<Path> javaFiles;

        Stream<Path> paths = Files.walk(Paths.get("src"));
        javaFiles = paths.filter(IS_JAVA_FILE).collect(Collectors.<Path> toList());
        paths.close();

        assertTrue(javaFiles.size() >= 372);
        List<String> filesMissingHeader = new ArrayList<String>();
        List<String> glp3Headers = new ArrayList<String>();
        for (Path file : javaFiles) {
            if (!startsWithLicenseHeader(file)) {
                filesMissingHeader.add(file.toString());
            }
            if (containsGPL(file)) {
                glp3Headers.add(file.toString());
            }
        }
        assertTrue("missing Apache License Header: " + filesMissingHeader, filesMissingHeader.isEmpty());
        assertTrue("GLP3 reference: " + glp3Headers, glp3Headers.isEmpty());
    }

    private boolean startsWithLicenseHeader(Path file) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file.toFile()));
        for (int i = 0; i < HEADER_LINES_TO_CHECK; i++) {
            String line = reader.readLine();
            if (line == null)
                break;
            if (line.contains(LICENSE_HEADER)) {
                reader.close();
                return true;
            }
        }
        reader.close();
        return false;
    }

    private boolean containsGPL(Path file) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file.toFile()));
        for (int i = 0; i < HEADER_LINES_TO_CHECK; i++) {
            String line = reader.readLine();
            if (line == null)
                break;
            if (line.contains(GPL_HEADER)) {
                reader.close();
                return true;
            }
        }
        reader.close();
        return false;
    }
}