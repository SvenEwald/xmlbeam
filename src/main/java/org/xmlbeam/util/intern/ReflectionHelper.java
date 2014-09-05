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
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

/**
 * A set of tiny helper methods internally used in the projection framework. This methods are
 * <b>not</b> part of the public framework API and might change in minor version updates.
 *
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public final class ReflectionHelper {

    private final static Method ISDEFAULT = findMethodByName(Method.class, "isDefault");
    private final static Class<?> OPTIONAL_CLASS = findOptionalClass();
    private final static Method GETPARAMETERS = findMethodByName(Method.class, "getParameters");
    private final static int PUBLIC_STATIC_MODIFIER = Modifier.STATIC | Modifier.PUBLIC;
    private final static Pattern VALID_FACTORY_METHOD_NAMES = Pattern.compile("valueOf|of|parse|getInstance");

    private static Class<?> findOptionalClass() {
        try {
            return Class.forName("java.util.Optional", false, ReflectionHelper.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * @param a
     * @param b
     * @return a set of all interfaces that a extends and b extends
     */
    public static Set<Class<?>> findAllCommonSuperInterfaces(final Class<?> a, final Class<?> b) {
        final Set<Class<?>> seta = new HashSet<Class<?>>(findAllSuperInterfaces(a));
        final Set<Class<?>> setb = new HashSet<Class<?>>(findAllSuperInterfaces(b));
        seta.retainAll(setb);
        return seta;
    }

    /**
     * Non exception throwing shortcut to find the first method with a given name.
     *
     * @param clazz
     * @param name
     * @return method with name "name" or null if it does not exist.
     */
    public static Method findMethodByName(final Class<?> clazz, final String name) {
        for (final Method m : clazz.getMethods()) {
            if (name.equals(m.getName())) {
                return m;
            }
        }
        return null;
    }

    /**
     * @param a
     * @return a set of all super interfaces of a
     */
    public static Set<Class<?>> findAllSuperInterfaces(final Class<?> a) {
        final Set<Class<?>> set = new LinkedHashSet<Class<?>>();
        if (a.isInterface()) {
            set.add(a);
        }
        for (final Class<?> i : a.getInterfaces()) {
            set.addAll(findAllSuperInterfaces(i));
        }
        return set;
    };

    /**
     * Defensive implemented method to determine if method has a return type.
     *
     * @param method
     * @return true if and only if it is not a void method.
     */
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

    /**
     * @param method
     * @return true if and only if the method has at least one parameter.
     */
    public static boolean hasParameters(final Method method) {
        return (method != null) && (method.getParameterTypes().length > 0);
    }

    /**
     * @param method
     * @param projectionInterface
     * @return lowest type in hierarchy that defines the given method
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

    /**
     * Same as Arrays.asList(...), but does automatically conversion of primitive arrays.
     *
     * @param array
     * @return List of objects representing the given array contents
     */
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
     * @return a LinkedList containing all non default methods for the given projection interface.
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
     * @return true if the given method is a Java 8 default method
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
     * Try to determine the names of the method parameters. Does not work on pre Java 8 jdk and
     * given method owner has to be compiled with "-parameters" option. NOTICE: The correct function
     * depends on the JVM implementation.
     *
     * @param m
     * @return Empty list if no parameters present or names could not be determined. List of
     *         parameter names else.
     */
    public static List<String> getMethodParameterNames(final Method m) {
        if ((GETPARAMETERS == null) || (m == null)) {
            return Collections.emptyList();
        }
        try {
            Object[] params = (Object[]) GETPARAMETERS.invoke(m);
            if (params.length == 0) {
                return Collections.emptyList();
            }
            Method getName = findMethodByName(params[0].getClass(), "getName");
            if (getName == null) {
                return Collections.emptyList();
            }
            List<String> paramNames = new LinkedList<String>();
            for (Object o : params) {
                paramNames.add((String) getName.invoke(o));
            }
            return paramNames;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Search for a suitable constructor which is invokable by the given parameter types. Similar to
     * {@code Class.getConstructor(...)}, but does not require parameter equality and does not throw
     * exceptions.
     *
     * @param type
     * @param params
     * @return constructor or null if no matching constructor was found.
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getCallableConstructorForParams(final Class<T> type, final Class<?>... params) {
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
    public static Method getCallableFactoryForParams(final Class<?> type, final Class<?>... params) {
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

    /**
     * @return true if and only if the current runtime may provide parameter names
     */
    public static boolean mayProvideParameterNames() {
        return GETPARAMETERS != null;
    }

    /**
     * Unwrap a given object until we assume it is a value. Supports Callable and Supplier so far.
     *
     * @param type
     * @param object
     * @return object if it's a value. Unwrapped object if its a Callable or a Supplier
     * @throws Exception
     *             may be thrown by given objects method
     */
    public static Object unwrap(final Class<?> type, final Object object) throws Exception {
        if (object == null) {
            return null;
        }
        if (type == null) {
            return object;
        }
        if (Callable.class.equals(type)) {
            assert (object instanceof Callable);
            return ((Callable<?>) object).call();
        }

        if ("java.util.function.Supplier".equals(type.getName())) {
            return findMethodByName(type, "get").invoke(object, (Object[]) null);
        }

        if ("java.util.Optional".equals(type.getName())) {
            return findMethodByName(type, "get").invoke(object, (Object[]) null);
        }
        return object;
    }

    /**
     * @param type
     * @return type as class, if possible.
     */
    public static Class<?> upperBoundAsClass(final Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return upperBoundAsClass(((ParameterizedType) type).getRawType());
        }
        if (type instanceof GenericArrayType) {
            return Array.newInstance(upperBoundAsClass(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        }
        if (type instanceof WildcardType) {
            Type[] bounds = ((WildcardType) type).getUpperBounds();
            if (bounds.length == 0) {
                return Object.class;
            };
            return upperBoundAsClass(bounds[0]);
        }

        throw new IllegalArgumentException("Unimplemented conversion for type " + type);
    }

    /**
     * @param type
     * @return generic parameter type if is optional, else type
     */
    public static Class<?> unwrapOptional(final Type type) {
        if (!isOptional(type)) {
            return (Class<?>) type;
        }
        assert type instanceof ParameterizedType;
        assert ((ParameterizedType) type).getActualTypeArguments().length == 1;
        return null;//(Class<?>) ((ParameterizedType) type)..getActualTypeArguments()[0];
    }

    public static boolean isOptional(final Type type) {
        if (OPTIONAL_CLASS == null) {
            return false;

        }
        if (type instanceof ParameterizedType) {
            return false;
        }

        return false;// return OPTIONAL_CLASS..equals(((ParameterizedType) type).getRawType());
    }
    
    public static boolean isRawType(final Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType)type).getActualTypeArguments().length==0;
        }
//         if (type instanceof Class) {
//             return true;
//         }
         
        return true;
        
    }
}
