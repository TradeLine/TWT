package org.tlsys.invokedynamic;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.tlsys.BytecodeTransformer;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * Created by Субочев Антон on 16.06.2016.
 */
public class InvokeDynamicBackporter implements BytecodeTransformer {
    public static byte[] transform(byte[] bytecode) {
        ClassNode classNode = new ClassNode(Opcodes.ASM5);
        InvokeDynamicConverter invokeDynamicConverter = new InvokeDynamicConverter(classNode);
        new ClassReader(bytecode).accept(invokeDynamicConverter, 0);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }

    private static class InvokeDynamicConverter extends ClassVisitor {
        private int classAccess;
        private String className;

        public InvokeDynamicConverter(ClassVisitor next) {
            super(Opcodes.ASM5, next);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.classAccess = access;
            this.className = name;
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (isBridgeMethodOnInterface(access)) {
                return null;
            }
            if (isNonAbstractMethodOnInterface(access) && !isClassInitializerMethod(name, desc, access)) {
                //				System.out.println("WARNING: Method '" + name + "' of interface '" + className + "' is non-abstract! " + "This will probably fail to run on Java 7 and below. " + "If you get this warning _without_ using Java 8's default methods, " + "please report a bug at https://github.com/orfjackal/retrolambda/issues " + "together with an SSCCE (http://www.sscce.org/)");
            }
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return new InvokeDynamicInsnConvertingMethodVisitor(api, mv, className);
        }

        private boolean isBridgeMethodOnInterface(int methodAccess) {
            return Flags.hasFlag(classAccess, Opcodes.ACC_INTERFACE) && Flags.hasFlag(methodAccess, Opcodes.ACC_BRIDGE);
        }

        private boolean isNonAbstractMethodOnInterface(int methodAccess) {
            return Flags.hasFlag(classAccess, Opcodes.ACC_INTERFACE) && !Flags.hasFlag(methodAccess, Opcodes.ACC_ABSTRACT);
        }

        private static boolean isClassInitializerMethod(String name, String desc, int methodAccess) {
            return name.equals("<clinit>") && desc.equals("()V") && Flags.hasFlag(methodAccess, Opcodes.ACC_STATIC);
        }
    }

    private static class InvokeDynamicInsnConvertingMethodVisitor extends MethodVisitor {
        private final String myClassName;

        public InvokeDynamicInsnConvertingMethodVisitor(int api, MethodVisitor mv, String myClassName) {
            super(api, mv);
            this.myClassName = myClassName;
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
            backportLambda(name, Type.getType(desc), bsm, bsmArgs);
        }

        private void backportLambda(String invokedName, Type invokedType, Handle bsm, Object[] bsmArgs) {
            /*
            Type[] argumentTypes= Type.getArgumentTypes(invokedType.toString());
            Type returnType= Type.getReturnType(invokedType.toString());
            String returnTypeName= returnType.getClassName();

            createArrayWithParameters(argumentTypes);

            this.visitLdcInsn(myClassName);
            this.visitLdcInsn(invokedName);
            this.visitLdcInsn(returnTypeName);
            this.visitLdcInsn(invokedType.toString());
            this.visitLdcInsn(bsmArgs[1].toString());

            this.visitVarInsn(Opcodes.ALOAD, 20);
            this.visitLdcInsn(bsm.getTag() == 5 ? "virtual" : "static");

            String runnableSignature= "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;";
            this.visitMethodInsn(INVOKESTATIC, "com/dragome/utils/DragomeCallsiteFactory", "create", runnableSignature, false);
            */


            Type[] argumentTypes = Type.getArgumentTypes(invokedType.toString());
            Handle handles = (Handle) bsmArgs[1];
            //this.visitTypeInsn(Opcodes.NEW, "MyClass");
            //this.visitInsn(Opcodes.DUP);
            StringBuilder sb = new StringBuilder("(");
            for (Type t : argumentTypes) {
                sb.append(t.toString());
            }
            if (bsm.getTag() == Opcodes.H_INVOKEVIRTUAL) {
                sb.append("L" + handles.getOwner() + ";");
                this.visitVarInsn(Opcodes.ASTORE, 0);
            }
            sb.append(")");
            String ss = sb.toString();


            //this.visitTypeInsn(Opcodes.NEW, "MyClass");
            //this.visitInsn(Opcodes.DUP);
            //this.visitInsn(Opcodes.DUP);
            //this.visitMethodInsn(Opcodes.INVOKESPECIAL,"MyClass","<init>", sb.toString()+"V",false);


            //this.visitTypeInsn(Opcodes.NEW, "MyClass");
            //this.visitInsn(Opcodes.DUP);


            String lambdaClass = handles.getOwner() + "/" + handles.getName();
            this.visitMethodInsn(Opcodes.INVOKESTATIC, lambdaClass, "getLambda", sb.toString() + "L" + lambdaClass + ";", false);

        }

        private void createArrayWithParameters(Type[] argumentTypes) {
            this.visitIntInsn(Opcodes.BIPUSH, argumentTypes.length);
            this.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
            this.visitVarInsn(Opcodes.ASTORE, 20);

            for (int i = argumentTypes.length - 1; i >= 0; i--) {
                convertPrimitive(argumentTypes[i], i);
                this.visitVarInsn(Opcodes.ASTORE, 21);
                this.visitVarInsn(Opcodes.ALOAD, 20);
                this.visitIntInsn(Opcodes.BIPUSH, i);
                this.visitVarInsn(Opcodes.ALOAD, 21);
                this.visitInsn(Opcodes.AASTORE);
            }
        }

        private void convertPrimitive(Object tp, int i) {
            if (tp.equals(Type.BOOLEAN_TYPE)) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            } else if (tp.equals(Type.BYTE_TYPE)) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            } else if (tp.equals(Type.CHAR_TYPE)) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
            } else if (tp.equals(Type.SHORT_TYPE)) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            } else if (tp.equals(Type.INT_TYPE)) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            } else if (tp.equals(Type.LONG_TYPE)) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                i++;
            } else if (tp.equals(Type.FLOAT_TYPE)) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            } else if (tp.equals(Type.DOUBLE_TYPE)) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                i++;
            }
            //			else
            //				mv.visitVarInsn(Opcodes.ALOAD, i);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            //if (opcode != Opcodes.NEW)
                super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }



    public byte[] transform(String className, byte[] bytecode) {
        return transform(bytecode);
    }

    public boolean requiresTransformation(String className) {
        return true;
    }
}
