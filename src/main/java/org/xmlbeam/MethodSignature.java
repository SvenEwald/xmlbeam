/**
 *  Copyright 2014 Sven Ewald
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
package org.xmlbeam;

import java.util.Arrays;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Temporary class to map between method calls and invocation handlers. TODO: Replace with better
 * performing string representation
 *
 * @author sven
 */
class MethodSignature implements Serializable {

    @Override
    public String toString() {
        return "MethodSignature [name=" + name + ", paramTypes=" + Arrays.toString(paramTypes) + "]";
    }

    private final Class<?>[] paramTypes;
    private final String name;

    public static MethodSignature forVoidMethod(final String name) {
        return new MethodSignature(name, new Class<?>[] {});
    }

    public static MethodSignature forSingleParam(final String name, final Class<?> singleParam) {
        return new MethodSignature(name, new Class<?>[] { singleParam });
    }

//    public static MethodSignature forMultipleParams(final String name, final Class<?>[] params) {
//        return new MethodSignature(name, params);
//    }

    /**
     * @param method
     */
    private MethodSignature(final Method method) {
        this.name = method.getName();
        this.paramTypes = method.getParameterTypes();
    }

    /**
     */
    private MethodSignature(final String name, final Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        result = (prime * result) + Arrays.hashCode(paramTypes);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MethodSignature other = (MethodSignature) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (!Arrays.equals(paramTypes, other.paramTypes)) {
            return false;
        }
        return true;
    }

    public static MethodSignature forMethod(final Method m) {
        return new MethodSignature(m);
    }

    /**
     * Creates a copy of this signature with a different name. Use this to override another method
     * with same parameters.
     *
     * @param name
     *            Name of method that is overriden.
     * @return Method signature with different name.
     */
    public MethodSignature overridenBy(final String name) {
        return new MethodSignature(name, this.paramTypes);
    }

}