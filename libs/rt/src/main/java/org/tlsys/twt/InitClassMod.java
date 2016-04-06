package org.tlsys.twt;

import org.tlsys.BlockModificator;
import org.tlsys.ClassModificator;
import org.tlsys.CodeBuilder;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class InitClassMod implements ClassModificator {
    private static final long serialVersionUID = 5169344545851994733L;

    private VField fieldInit;

    public InitClassMod() {

    }

    @Override
    public void onAdd(VClass clazz) {
        try {
            VClass booleanClass = clazz.getClassLoader().loadClass("boolean", null);
            fieldInit = new VField("f" + clazz.fullName, booleanClass, Modifier.PRIVATE, clazz);

            for (VConstructor v : clazz.constructors) {
                if (v.getBlock() == null)
                    continue;
                v.getBlock().addMod(new InitVars(v, booleanClass));
            }
        } catch (VClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (MethodNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onRemove(VClass clazz) {
        fieldInit = null;
    }

    @Override
    public List<VField> getFields(List<VField> list) {
        list.add(fieldInit);
        return list;
    }

    @Override
    public List<VMethod> getMethods(List<VMethod> list) {
        return list;
    }

    public class InitVars implements BlockModificator {

        private static final long serialVersionUID = 3079859845088127743L;

        private final VConstructor constructor;
        private final ArrayList<Operation> ops = new ArrayList<>();
        private final VClass clazz;
        private final VClass booleanClass;
        private final VMethod isUndefinedMethod;

        public InitVars(VConstructor constructor, VClass booleanClass) throws VClassNotFoundException, MethodNotFoundException {
            this.constructor = constructor;
            this.booleanClass = booleanClass;

            clazz = constructor.getParent();
            if (constructor.parentConstructorInvoke != null)
                ops.add(constructor.parentConstructorInvoke);


            VClass scriptClass = clazz.getClassLoader().loadClass(Script.class.getName(), null);
            isUndefinedMethod = scriptClass.getMethod("isUndefined", null, clazz.getClassLoader().loadClass(Object.class.getName(), null));
        }

        @Override
        public void onAdd(VBlock block) {

            VBinar checkFalse = new VBinar(new GetField(new This(clazz), fieldInit), new Const(false, booleanClass), booleanClass, VBinar.BitType.EQ);


            VBinar allCheck = new VBinar(checkFalse, CodeBuilder.invokeStatic(isUndefinedMethod).arg(new GetField(new This(clazz), fieldInit)).build(), booleanClass, VBinar.BitType.OR);


            VBlock ifBlock = null;

            VIf vif = new VIf(allCheck, block);
            ops.add(vif);
            ifBlock = vif.createThen(null, null);

            for (VField f : clazz.getLocalFields()) {
                if (f.isStatic())
                    continue;
                if (f == fieldInit)
                    continue;
                if (f.init == null)
                    continue;
                SetField sfield = new SetField(new This(clazz), f, f.init, Assign.AsType.ASSIGN, null, null);
                ifBlock.add(sfield);
            }

            ifBlock.add(new SetField(new This(clazz), fieldInit, new Const(true, booleanClass), Assign.AsType.ASSIGN, null, null));

        }

        @Override
        public List<Operation> getOperations(List<Operation> list) {
            list.addAll(0, ops);
            return list;
        }


    }
}
