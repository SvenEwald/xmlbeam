/**
 *  Copyright 2017 Sven Ewald
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
package org.xmlbeam.refcards;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.util.intern.DocScope;
import org.xmlbeam.util.intern.Scope;

/**
 *
 */
@SuppressWarnings("javadoc")
public class GenerateAPIDoc {

    Set<Method> visitedMethods = new HashSet<Method>();

    @Test
    public void generateIOAPIDoc() {
        dump("projector", XBProjector.class, DocScope.IO);
    }

    /**
     * @param class1
     * @param io
     */
    private void dump(final String prefix, final Class<?> class1, final DocScope scope) {
        if (class1 == null) {
            return;
        }
        for (Method m : allPublicMethods(class1)) {
            Scope annotation = m.getAnnotation(Scope.class);
            if (annotation == null) {
                continue;
            }
            if (!scope.equals(annotation.value())) {
                continue;
            }
            if (visitedMethods.contains(m)) {
                continue;
            }
            if (!isAPIClass(m.getReturnType())) {

                System.out.println(prefix + "." + methodAsString(m) + (Void.TYPE.equals(m.getReturnType()) ? "" : " -> " + getTypeName(m.getGenericReturnType())));
                continue;
            }
            boolean methodIsOptional = m.getReturnType().equals(class1);
            if (methodIsOptional) {
                //prefix += "[." + methodAsString(m) + "]";
                continue;
            }
            visitedMethods.add(m);
            dump(prefix + "." + methodAsString(m), m.getReturnType(), scope);
            visitedMethods.remove(m);
        }

    }

    /**
     * @param class1
     * @return
     */
    private Collection<Method> allPublicMethods(final Class<?> class1) {
        if (class1 == null) {
            return Collections.emptyList();
        }
        Set<Method> methods = new HashSet<Method>();
        methods.addAll(Arrays.asList(class1.getDeclaredMethods()));
        methods.addAll(allPublicMethods(class1.getSuperclass()));
        for (Class<?> c : class1.getInterfaces()) {
            methods.addAll(allPublicMethods(c));
        }
        return methods;
    }

    /**
     * @param returnType
     * @return
     */
    private boolean isAPIClass(final Class<?> returnType) {
        for (Method m : returnType.getDeclaredMethods()) {
            Scope annotation = m.getAnnotation(Scope.class);
            if (annotation != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param m
     */
    private String methodAsString(final Method m) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(m.getName() + "(");
            Class[] params = m.getParameterTypes();
            int length = m.isVarArgs() ? params.length - 1 : params.length;
            for (int j = 0; j < length; j++) {
                sb.append(getTypeName(params[j]));
                if (j < (params.length - 1)) {
                    sb.append(",");
                }
            }
            sb.append(")");
            return sb.toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    static String getTypeName(final Class type) {
        if (type.isArray()) {
            try {
                Class cl = type;
                int dimensions = 0;
                while (cl.isArray()) {
                    dimensions++;
                    cl = cl.getComponentType();
                }
                StringBuffer sb = new StringBuffer();
                sb.append(cl.getName());
                for (int i = 0; i < dimensions; i++) {
                    sb.append("[]");
                }
                return sb.toString();
            } catch (Throwable e) {
                /* FALLTHRU */ }
        }
        StringBuffer tps = new StringBuffer();
        for (TypeVariable<GenericDeclaration> t : type.getTypeParameters()) {
            if (tps.length() > 0) {
                tps.append(", ");
            }
            tps.append(t.getName());
        }
        return type.getSimpleName() + (tps.length() > 0 ? "<" + tps.toString() + ">" : "");
    }

    static String getTypeName(final Type type) {
        //  if (type instanceof ParameterizedType) {
        return type.toString().replaceAll(".*\\.", "");
        //   }
        //   return type.toString();
    }
}
