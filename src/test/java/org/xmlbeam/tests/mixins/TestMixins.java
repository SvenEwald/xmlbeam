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
package org.xmlbeam.tests.mixins;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.xmlbeam.XBProjector;
@SuppressWarnings("javadoc")
public class TestMixins {

    public interface Mixin {
        void doSomething();  
    }

    public interface MixinOverridingToString {
        @Override
        String toString();
    }
    
    public interface Projection extends Mixin {
    }
    
    public interface OverridingProjection extends MixinOverridingToString {
        
    }

    boolean verifyMixin = false;
    @Before
    public void resetVerifyFlag() {
        verifyMixin=false;
    }
    
    final Mixin mixin = new Mixin() {
        private Projection me;

        @Override
        public void doSomething() {
            assertNotNull(me);
            verifyMixin=true;
        }
    };

    final MixinOverridingToString overridingMixin = new MixinOverridingToString() {
        @SuppressWarnings("unused")
        private OverridingProjection me;
        @Override
        public String toString() { return "12345";};
    };
    
    @Test
    public void testMixinIsCalled() {
        Projection projection = new XBProjector().mixins().addProjectionMixin(Projection.class, mixin).projectEmptyDocument(Projection.class);
        assertFalse(verifyMixin);
        projection.doSomething();
        assertTrue(verifyMixin);
    }
    
    @Test
    public void testMixinOnDOMAccess() {
        OverridingProjection projection = new XBProjector().mixins().addProjectionMixin(OverridingProjection.class, overridingMixin).projectEmptyDocument(OverridingProjection.class);
        assertEquals("12345", projection.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCallWithoutMixin() {
        Mixin mixin = new XBProjector().projectEmptyDocument(Mixin.class);
        assertFalse(verifyMixin);
        mixin.doSomething();
        assertFalse(verifyMixin);
    }
}
