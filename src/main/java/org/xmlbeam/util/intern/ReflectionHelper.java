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
package org.xmlbeam.util.intern;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A set of tiny helper methods internally used in the projection framework. This methods are
 * <b>not</b> part of the public framework API and might change in minor version updates.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public final class ReflectionHelper {

    private final static Method ISDEFAULT = findIsDefaultMethod();
    private static final Object NO_ARGS = new Object[0];

    public static Set<Class<?>> findAllCommonSuperInterfaces(Class<?> a, Class<?> b) {
        Set<Class<?>> seta = new HashSet<Class<?>>(findAllSuperInterfaces(a));
        Set<Class<?>> setb = new HashSet<Class<?>>(findAllSuperInterfaces(b));
        seta.retainAll(setb);
        return seta;
    }

    /**
     * @return
     */
    private static Method findIsDefaultMethod() {
        for (Method m : Method.class.getMethods()) {
            if ("isDefault".equals(m.getName())) {
                return m;
            }
        }
        return null;
    }

    public static Collection<? extends Class<?>> findAllSuperInterfaces(Class<?> a) {
        Set<Class<?>> set = new LinkedHashSet <Class<?>>();        
        if (a.isInterface()) {
            set.add(a);
        }
        for (Class<?> i : a.getInterfaces()) {
            set.addAll(findAllSuperInterfaces(i));
        }
        return set;
    };

    public static boolean hasReturnType(final Method method) {
        if (method==null) {
            return false;
        }
        if (method.getReturnType() == null) {
            return false;
        }
        if (Void.class.equals(method.getReturnType())) {
            return false;
        }
        return !Void.TYPE.equals(method.getReturnType());
    }

    public static boolean hasParameters(final Method method) {
        return (method!=null) && (method.getParameterTypes().length > 0);
    }

    /**
     * @param method
     * @param projectionInterface
     * @return
     */
    public static Class<?> findDeclaringInterface(Method method, Class<?> projectionInterface) {                
        for (Class<?> interf:findAllSuperInterfaces(projectionInterface)) {
            if (declaresMethod(interf,method)) {
                return interf;
            }
        }
        return method.getDeclaringClass();
    }

    /**
     * @param interf
     * @param method
     * @return
     */
    private static boolean declaresMethod(Class<?> interf, Method method) {
        for (Method m:interf.getDeclaredMethods()) {
            if (!m.getName().equals(method.getName())) {
                continue;
            }
            if (!Arrays.equals(m.getParameterTypes(), method.getParameterTypes())) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static List<Object> array2ObjectList(Object array) {
        int length = Array.getLength(array);
        List<Object> list = new ArrayList<Object>(length);
        for (int i = 0; i < length; ++i) {
            list.add(Array.get(array, i));
        }
        return list;
    }

    /**
     * @param projectionInterface
     * @return
     */
    public static List<Method> getNonDefaultMethods(Class<?> projectionInterface) {
        List<Method> list = new LinkedList<Method>();
        for (Method m : projectionInterface.getMethods()) {
            if (isDefaultMethod(m)) {
                continue;
            }
            list.add(m);
        }
        return list;
    }

    /**
     * @param m
     * @return
     */
    public static boolean isDefaultMethod(Method m) {
        try {
            return (ISDEFAULT != null) && ((Boolean)ISDEFAULT.invoke(m, NO_ARGS));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
