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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A set of tiny helper methods internally used in the projection framework. This methods are
 * <b>not</b> part of the public framework API and might change in minor version updates.
 *
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public final class ReflectionHelper {

    private final static Method ISDEFAULT = findIsDefaultMethod();
    private final static int PUBLIC_STATIC_MODIFIER = Modifier.STATIC | Modifier.PUBLIC;
    private final static Pattern VALID_FACTORY_METHOD_NAMES = Pattern.compile("valueOf|of|parse|getInstance");

    public static Set<Class<?>> findAllCommonSuperInterfaces(final Class<?> a, final Class<?> b) {
        final Set<Class<?>> seta = new HashSet<Class<?>>(findAllSuperInterfaces(a));
        final Set<Class<?>> setb = new HashSet<Class<?>>(findAllSuperInterfaces(b));
        seta.retainAll(setb);
        return seta;
    }

    /**
     * @return
     */
    private static Method findIsDefaultMethod() {
        for (final Method m : Method.class.getMethods()) {
            if ("isDefault".equals(m.getName())) {
                return m;
            }
        }

        return null;
    }

    public static Collection<? extends Class<?>> findAllSuperInterfaces(final Class<?> a) {
        final Set<Class<?>> set = new LinkedHashSet<Class<?>>();
        if (a.isInterface()) {
            set.add(a);
        }
        for (final Class<?> i : a.getInterfaces()) {
            set.addAll(findAllSuperInterfaces(i));
        }
        return set;
    };

    public static boolean hasReturnType(final Method method) {
        if (method == null) {
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
        return (method != null) && (method.getParameterTypes().length > 0);
    }

    /**
     * @param method
     * @param projectionInterface
     * @return
     */
    public static Class<?> findDeclaringInterface(final Method method, final Class<?> projectionInterface) {
        for (final Class<?> interf : findAllSuperInterfaces(projectionInterface)) {
            if (declaresMethod(interf, method)) {
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
    private static boolean declaresMethod(final Class<?> interf, final Method method) {
        for (final Method m : interf.getDeclaredMethods()) {
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

    public static List<Object> array2ObjectList(final Object array) {
        final int length = Array.getLength(array);
        final List<Object> list = new ArrayList<Object>(length);
        for (int i = 0; i < length; ++i) {
            list.add(Array.get(array, i));
        }
        return list;
    }

    /**
     * @param projectionInterface
     * @return
     */
    public static List<Method> getNonDefaultMethods(final Class<?> projectionInterface) {
        final List<Method> list = new LinkedList<Method>();
        for (final Method m : projectionInterface.getMethods()) {
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
    public static boolean isDefaultMethod(final Method m) {
        try {
            return (ISDEFAULT != null) && ((Boolean) ISDEFAULT.invoke(m, (Object[]) null));
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (final InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Search for a suitable constructor which is invokable by the given parameter types. Similar to
     * {@link Class.getConstructor()}, but does not require parameter equality and does not throw
     * exceptions.
     * 
     * @param type
     * @param params
     * @return constructor or null if no matching constructor was found.
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getCallableConstructorForParams(final Class<T> type, Class<?>... params) {
        for (Constructor<T> c : (Constructor<T>[]) type.getConstructors()) {
            final Class<?>[] parameterTypes = c.getParameterTypes();
            if (!Arrays.equals(parameterTypes, params)) {
                continue;
            }
            return c;
        }
        return null;
    }

    /**
     * Search for a static factory method returning the target type.
     * 
     * @param type
     * @param params
     * @return factory method or null if it is not found.
     */
    public static Method getCallableFactoryForParams(final Class<?> type, Class<?>... params) {
        for (Method m : type.getMethods()) {
            if ((m.getModifiers() & PUBLIC_STATIC_MODIFIER) != PUBLIC_STATIC_MODIFIER) {
                continue;
            }
            if (!type.isAssignableFrom(m.getReturnType())) {
                continue;
            }
            if (!Arrays.equals(m.getParameterTypes(), params)) {
                continue;
            }
            if (!VALID_FACTORY_METHOD_NAMES.matcher(m.getName()).matches()) {
                continue;
            }
            return m;
        }
        return null;
    }
}
