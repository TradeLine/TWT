package org.tlsys;

import org.tlsys.lex.Invoke;
import org.tlsys.lex.This;
import org.tlsys.lex.declare.*;
import org.tlsys.twt.CompileException;

import java.util.List;
import java.util.Objects;

public class EnumModificator implements ClassModificator {

    private static final long serialVersionUID = 460728349210538523L;
    private final VClass clazz;

    public EnumModificator(VClass clazz) throws CompileException {
        this.clazz = clazz;

        System.out.println("Using enum to " + clazz.getRealName());

        for (VConstructor c : clazz.constructors) {
            c.getMods().add(new ConsMod(c));
            System.out.println("Use to " + c.getDescription());
        }
    }

    @Override
    public List<VField> getFields(List<VField> fields) {
        return fields;
    }

    @Override
    public List<VMethod> getMethods(List<VMethod> methods) {
        return methods;
    }

    private static class ConsMod implements ArgumentModificator {
        private static final long serialVersionUID = 4890319167882065240L;
        private final VArgument nameArg;
        private final VArgument ordinalArg;

        private final Invoke parentInvoke;

        private ConsMod(VConstructor parent) throws CompileException {

            VClass stringClass = parent.getParent().getClassLoader().loadClass(String.class.getName(), null);
            VClass intClass = parent.getParent().getClassLoader().loadClass(int.class.getName(), null);
            nameArg = new VArgument("name", null, stringClass, false, false, parent, this, null);
            ordinalArg = new VArgument("ordinal", null, intClass, false, false, parent, this, null);
            Objects.requireNonNull(parent);
            Objects.requireNonNull(parent.getParent());
            Objects.requireNonNull(parent.getParent().extendsClass);
            VConstructor parentInit = parent.getParent().extendsClass.getConstructor(null, stringClass, intClass);
            parentInvoke = new Invoke(parentInit, new This(parent.getParent())).addArg(nameArg).addArg(ordinalArg);
            parent.parentConstructorInvoke = parentInvoke;
        }

        @Override
        public List<VArgument> getArguments(List<VArgument> arguments) {
            arguments.add(nameArg);
            arguments.add(ordinalArg);
            return arguments;
        }

        @Override
        public void setBody(VBlock oldBody, VBlock newBody) {
        }
    }
}
