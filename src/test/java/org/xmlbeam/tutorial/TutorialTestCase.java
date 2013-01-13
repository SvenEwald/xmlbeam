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

import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;

/**
 * Abstract base class for tutorial test cases. The tutorial tests are printing out a lot of stuff
 * to demonstrate the example projections. To suppress this output and get a faster and more
 * readable log, set the "SwallowTutorialOutput" property.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public abstract class TutorialTestCase {

    private PrintStream origSyso;

    @Before
    public void swallowOutput() {
        if (System.getProperty("SwallowTutorialOutput") == null) {
            return;
        }
        this.origSyso = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
            }
        }));
    }

    @After
    public void restoreSyso() {
        if (System.getProperty("SwallowTutorialOutput") == null) {
            return;
        }
        System.setOut(origSyso);
    }

}
