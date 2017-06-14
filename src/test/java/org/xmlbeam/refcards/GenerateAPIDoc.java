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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.refcards.xgml.Section;
import org.xmlbeam.types.XBAutoList;
import org.xmlbeam.types.XBAutoMap;
import org.xmlbeam.util.intern.DocScope;
import org.xmlbeam.util.intern.Scope;

/**
 *
 */
@SuppressWarnings("javadoc")
public class GenerateAPIDoc {

    static {
        System.setProperty("java.awt.headless", "true");
    }
    final XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
    final XBAutoMap<String> map = projector.autoMapEmptyDocument(String.class);
    final XBAutoList<Section> nodes = map.getList("/section[@name='xgml']/section[@name='graph']/section[@name='node']", Section.class);
    final XBAutoList<Section> edges = map.getList("/section[@name='xgml']/section[@name='graph']/section[@name='edge']", Section.class);

    final Map<MethodSignature, Section> nodeMap = new HashMap<MethodSignature, Section>();
    final Set<String> createdEdges = new HashSet<String>();
    final Set<String> createdLeaves = new HashSet<String>();

    final Set<Method> visitedMethods = new HashSet<Method>();
    private int maxID = 0;

    AffineTransform affinetransform = new AffineTransform();
    FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    Font font = new Font("Dialog", Font.PLAIN, 12);

    private static class MethodSignature {
        @Override
        public String toString() {
            return getLabel();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
//            result = (prime * result) + ((declaringClass == null) ? 0 : declaringClass.hashCode());
            result = (prime * result) + ((name == null) ? 0 : name.hashCode());
            result = (prime * result) + Arrays.hashCode(parameterTypes);
            result = (prime * result) + ((returnType == null) ? 0 : returnType.hashCode());
            result = (prime * result) + (varArgs ? 1231 : 1237);
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
//            if (declaringClass == null) {
//                if (other.declaringClass != null) {
//                    return false;
//                }
//            } else if (!declaringClass.equals(other.declaringClass)) {
//                return false;
//            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            if (!Arrays.equals(parameterTypes, other.parameterTypes)) {
                return false;
            }
            if (returnType == null) {
                if (other.returnType != null) {
                    return false;
                }
            } else if (!returnType.equals(other.returnType)) {
                return false;
            }
            if (varArgs != other.varArgs) {
                return false;
            }
            return true;
        }

        String getLabel() {
            StringBuffer sb = new StringBuffer(name);
            if (parameterTypes != null) {
                sb.append("(");

                Class[] params = parameterTypes;
                int length = varArgs ? params.length - 1 : params.length;
                for (int j = 0; j < length; j++) {
                    sb.append(getTypeName(params[j]));
                    if (j < (params.length - 1)) {
                        sb.append(",");
                    }
                }

                sb.append(")");
            }
            return sb.toString();
        }

        private final String name;
        private final Type returnType;
        private Class<?> declaringClass;
        private Class[] parameterTypes;
        private boolean varArgs;

        /**
         * @param string
         * @param class1
         */
        public MethodSignature(final String string, final Type class1) {
            this.name = string;
            this.returnType = class1;
        }

        public MethodSignature(final Method m) {
            this.name = m.getName();
            this.returnType = m.getGenericReturnType();
            this.declaringClass = m.getDeclaringClass();
            parameterTypes = m.getParameterTypes();
            varArgs = m.isVarArgs();
        }

    }

    @Test
    public void generateIOAPIDoc() throws IOException {
        //dump("new XBProjector()", XBProjector.class, DocScope.IO);
        dump(new MethodSignature("new XBProjector()", XBProjector.class), XBProjector.class, DocScope.IO);
        //   System.out.println(projector.asString(map));
        projector.io().file("test.xgml").write(map);
    }

    /**
     * @param class1
     * @param io
     */
    private void dump(final MethodSignature prefix, final Class<?> class1, final DocScope scope) {
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

                // System.out.println(prefix + "." + methodAsString(m) + (Void.TYPE.equals(m.getReturnType()) ? "" : " -> " + getTypeName(m.getGenericReturnType())));
                // System.out.println(prefix + "->" + methodAsString(m));
                Section from = createNode(prefix);
                Section to = createNode(/* methodAsString(m) */new MethodSignature(m));
                createEdge(from, to);
                if (!Void.TYPE.equals(m.getReturnType())) {
                    MethodSignature ms = new MethodSignature(getTypeName(m.getGenericReturnType()), new Type() {
                    });
                    //createEdge(to, createNode(getTypeName(m.getGenericReturnType())));
                    if (!createdLeaves.contains(to.id().get() + ms.getLabel())) {
                        createEdge(to, createNode(ms));
                        createdLeaves.add(to.id().get() + ms.getLabel());
                    }

                }
                continue;
            }
            boolean methodIsOptional = m.getReturnType().equals(m.getDeclaringClass());
            if (methodIsOptional) {
                //prefix += "[." + methodAsString(m) + "]";
                continue;
            }
            // System.out.println(prefix + "->" + methodAsString(m));
            createEdge(createNode(prefix), createNode(new MethodSignature(m)));
            visitedMethods.add(m);
            //dump(prefix + optionalMethodsAsString(class1, scope) + "." + methodAsString(m), m.getReturnType(), scope);
            //   dumpOptionalMethods(class1, prefix, scope);
            dump(new MethodSignature(m)/* methodAsString(m) */, m.getReturnType(), scope);
            // visitedMethods.remove(m);
        }
    }

//    private void dumpOptionalMethods(final Class<?> class1, final String prefix, final DocScope scope) {
//        for (Method m : getAllOptionalMethod(class1)) {
//            if (!scope.equals(m.getAnnotation(Scope.class).value())) {
//                continue;
//            }
//            //sb.append("[." + methodAsString(m) + "]");
//            createEdge(createNode(prefix), createNode(methodAsString(m)));
//            createEdge(createNode(methodAsString(m)), createNode(prefix));
//        }
//    }

    /**
     * @param class1
     * @param scope
     * @return
     */
    private String optionalMethodsAsString(final Class<?> class1, final DocScope scope) {
        StringBuilder sb = new StringBuilder();
        for (Method m : getAllOptionalMethod(class1)) {
            if (!scope.equals(m.getAnnotation(Scope.class).value())) {
                continue;
            }
            sb.append("[." + methodAsString(m) + "]");
        }
        return sb.toString();
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

    private Collection<Method> getAllOptionalMethod(final Class<?> class1) {
        Set<Method> methods = new HashSet<Method>();
        for (Method m : allPublicMethods(class1)) {
            if (m.getAnnotation(Scope.class) == null) {
                continue;
            }
            if (m.getDeclaringClass().equals(m.getReturnType())) {
                methods.add(m);
            }
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
        return type.toString().replaceAll("(class )?([a-z\\.]+\\.)*", "");
        //   }
        //   return type.toString();
    }

    @Ignore
    public void testGraphCreation() throws IOException {
//
//        Section from = createNode("from");
//        Section to = createNode("to");
//        createEdge(from, to);
//
//        System.out.println(projector.asString(map));
//        projector.io().file("test.xgml").write(map);
    }

    private void createEdge(final Section from, final Section to) {
        if (createdEdges.contains(from.id() + "#" + to.id())) {
            return;
        }
        Section edge = projector.projectEmptyElement("section", Section.class);
        edge.name().set("edge");
        edge.source().set(from.id().get());
        edge.target().set(to.id().get());
        edge.graphics().set("standard");
        edges.add(edge);
        createdEdges.add(from.id() + "#" + to.id());
    }

    private Section createNode(final MethodSignature ms) {
        if (nodeMap.containsKey(ms)) {
            return nodeMap.get(ms);
        }
        System.out.println("new node " + ms.getLabel() + " " + ms.declaringClass);
        Section node = projector.projectEmptyElement("section", Section.class);
        node.id().set(++maxID);
        node.name().set("node");
        String label = ms.getLabel();
        node.label().set(label);
        node.fill().set("#FEFEFE");
        double width = font.getStringBounds(label, frc).getWidth();
        node.widh().set(5 + width);
        nodeMap.put(ms, node);
        nodes.add(node);
        return node;
    }

}
