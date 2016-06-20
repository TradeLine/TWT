package org.tlsys.compiler.invokedynamic;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.tlsys.compiler.BytecodeTransformer;

import java.lang.reflect.Modifier;
import java.util.Stack;

public class InvokeDynamicBackporter implements BytecodeTransformer {
    public static byte[] transform(byte[] bytecode) {
        ClassNode classNode = new ClassNode(Opcodes.ASM5);
        ClassReader classReader = new ClassReader(bytecode);
        classReader.accept(new InvokeDynamicConverter(classNode), 0);
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


            if (name.equals("<init>")) {
                Type[] argumentTypes = Type.getArgumentTypes(desc);
                StringBuilder sb = new StringBuilder("(");
                for (Type t : argumentTypes) {
                    sb.append(t.toString());
                }
                sb.append(")");
                sb.append("L" + className + ";");
                String s = sb.toString();
                MethodVisitor mm = super.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "<sinit>", s, null, new String[0]);
                mm.visitCode();
                mm.visitTypeInsn(Opcodes.NEW, className);
                mm.visitInsn(Opcodes.DUP);

                int i = 0;
                for (Type t : argumentTypes) {
                    switch (t.getDescriptor()) {
                        case "V":
                            break;
                        case "Z":
                        case "C":
                        case "B":
                        case "S":
                        case "I":
                            mm.visitVarInsn(Opcodes.ILOAD, i++);
                            break;
                        case "F":
                            mm.visitVarInsn(Opcodes.FLOAD, i++);
                            break;
                        case "J":
                            mm.visitVarInsn(Opcodes.LLOAD, i++);
                            break;
                        case "D":
                            mm.visitVarInsn(Opcodes.DLOAD, i++);
                            break;
                        default:
                            mm.visitVarInsn(Opcodes.ALOAD, i++);
                    }
                }

                mm.visitMethodInsn(Opcodes.INVOKESPECIAL, className, "<init>", desc, false);
                mm.visitInsn(Opcodes.ARETURN);
                mm.visitEnd();
            }


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
            Type[] argumentTypes = Type.getArgumentTypes(invokedType.toString());
            Handle handles = (Handle) bsmArgs[1];
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

        private final Stack<String> init = new Stack<>();


        @Override
        public void visitTypeInsn(int opcode, String type) {
            if (opcode == Opcodes.NEW) {
                init.push(type);
                return;
            }
            super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == Opcodes.DUP && !init.isEmpty())
                return;
            super.visitInsn(opcode);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (name.equals("<init>")) {
                if (!init.isEmpty() && init.peek().equals(owner)) {
                    init.pop();
                    Type[] arguments = Type.getArgumentTypes(desc);
                    StringBuilder sb = new StringBuilder("(");
                    for (Type t : arguments)
                        sb.append(t.toString());
                    sb.append(")L").append(owner).append(";");
                    String d = sb.toString();
                    final String STATIC_INIT = "<sinit>";
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, STATIC_INIT, d, false);
                    return;
                }
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }

        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            super.visitLocalVariable(name, desc, signature, start, end, index);
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
            return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
        }
    }


    public byte[] transform(String className, byte[] bytecode) {
        return transform(bytecode);
    }

    public boolean requiresTransformation(String className) {
        return true;
    }
}
