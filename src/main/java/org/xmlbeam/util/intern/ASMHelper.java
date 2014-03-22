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
package org.xmlbeam.util.intern;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.UUID;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class ASMHelper implements Opcodes {


    public static <T> T create(final Class<T> projectionInterface, final Object projectionInvocationHandler) {
        final String proxyClassName = "P" + UUID.randomUUID().toString();

        Class<?> clazz = new ClassLoader() {
            public Class<?> defineClass(final String name, final byte[] b) {
                return defineClass(name, b, 0, b.length);
            }
        }.defineClass(proxyClassName, getClassData(proxyClassName,projectionInterface));
        T o;

        try {
            Constructor<?> constructor = clazz.getConstructors()[0];
            o = (T) constructor.newInstance(projectionInvocationHandler);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return o;
    }

    private static byte[] getClassData(final String proxyClassName, final Class<?> projectionInterface) {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;

        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, proxyClassName, null, "java/lang/Object", new String[] { Type.getInternalName(projectionInterface) });

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "handler", Type.getDescriptor(Object.class), null, null);
            fv.visitEnd();
        }
        {
            addConstructorWithParam(proxyClassName, cw, Object.class);
        }

        for (Method method : ReflectionHelper.getNonDefaultMethods(projectionInterface)) {
            addMethod(proxyClassName, cw, method);
        }
        cw.visitEnd();

        return cw.toByteArray();

    }

    /**
     * @param cw
     * @param ParamClass
     */
    private static void addConstructorWithParam(final String proxyClassName, final ClassWriter cw, final Class<?> ParamClass) {
        final String paramDescriptor = Type.getDescriptor(ParamClass);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "("+paramDescriptor+")V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V");
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, proxyClassName, "handler", paramDescriptor);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitInsn(RETURN);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    /**
     * @param cw
     * @param method
     */
    private static void addMethod(final String proxyClassName, final ClassWriter cw, final Method method) {
        MethodVisitor mv;
        int maxStackSize = ReflectionHelper.hasReturnType(method) ? 1 : 0;
        String name = method.getName();
        String methodDescriptor = Type.getMethodDescriptor(method);
        mv = cw.visitMethod(ACC_PUBLIC, method.getName(), methodDescriptor, null, null);

        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, proxyClassName, "handler", Type.getDescriptor(Object.class));
//        mv.visitInsn(ACONST_NULL);
//        mv.visitInsn(ACONST_NULL);
 //       mv.visitMethodInsn(INVOKEINTERFACE, proxyClassName, "asmInvoke", "(Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;");
        //   mv.visitInsn(ICONST_0);
        //   mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

//        mv.visitVarInsn(ALOAD, 0);
//        mv.visitFieldInsn(GETFIELD, "org/xmlbeam/tests/util/intern/TestASMProxy$1", "handler", "Lorg/xmlbeam/tests/util/intern/TestASMProxy$ProxyMe;");
        //  mv.visitMethodInsn(INVOKEINTERFACE, "org/xmlbeam/tests/util/intern/TestASMProxy$ProxyMe", "invokeMePlz", "()I", true);
        int c = 1;
        for (Class<?> param : method.getParameterTypes()) {
            mv.visitVarInsn(ALOAD, c++);
        }
        mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method));

        if (ReflectionHelper.hasReturnType(method)) {
            Class<?> returnType = method.getReturnType();
            //  mv.visitTypeInsn(CHECKCAST, Type.getInternalName(returnType));
            //    if (returnType.isPrimitive()) {
            //        autoUnBoxing(mv, Type.getType(returnType));
            //    }
            mv.visitInsn(getReturnOpcodeForType(returnType));
        } else {
            mv.visitInsn(POP);
            mv.visitInsn(RETURN);
        }
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitMaxs(c + 1, c);
        mv.visitEnd();


    }

//    protected static void autoUnBoxing(final MethodVisitor mv, final Type fieldType) {
//        switch (fieldType.getSort()) {
//        case Type.BOOLEAN:
//            mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
//            break;
//        case Type.BYTE:
//            mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
//            break;
//        case Type.CHAR:
//            mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
//            break;
//        case Type.SHORT:
//            mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
//            break;
//        case Type.INT:
//            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
//            break;
//        case Type.FLOAT:
//            mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
//            break;
//        case Type.LONG:
//            mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
//            break;
//        case Type.DOUBLE:
//            mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
//            break;
//        case Type.ARRAY:
//            mv.visitTypeInsn(CHECKCAST, fieldType.getInternalName());
//            break;
//        default:
//            mv.visitTypeInsn(CHECKCAST, fieldType.getInternalName());
//        }
//    }

    /**
     * @param returnType
     * @return
     */
    private static int getReturnOpcodeForType(final Class<?> returnType) {
        switch (Type.getType(returnType).getSort()) {
        case Type.BOOLEAN:
        case Type.BYTE:
        case Type.CHAR:
        case Type.SHORT:
        case Type.INT:
            return IRETURN;
        case Type.FLOAT:
            return FRETURN;
        case Type.LONG:
            return LRETURN;
        case Type.DOUBLE:
            return DRETURN;
        default:
            return ARETURN;
        }
    }

//    protected static void autoBoxing(final MethodVisitor mv, final Type fieldType) {
//        switch (fieldType.getSort()) {
//        case Type.BOOLEAN:
//            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
//            break;
//        case Type.BYTE:
//            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
//            break;
//        case Type.CHAR:
//            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
//            break;
//        case Type.SHORT:
//            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
//            break;
//        case Type.INT:
//            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
//            break;
//        case Type.FLOAT:
//            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
//            break;
//        case Type.LONG:
//            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
//            break;
//        case Type.DOUBLE:
//            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
//            break;
//        }
//    }
}
