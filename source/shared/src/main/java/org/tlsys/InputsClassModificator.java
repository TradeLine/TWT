package org.tlsys;

import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Модификатор, пробрасывающий локальные переменные внутрь класса используя локальные переменные и аргументы конструктора
 */
public class InputsClassModificator implements ClassModificator {

    private static final long serialVersionUID = -7395221822114651467L;
    private final VClass forClass;
    private final ArrayList<InputMody> blocks = new ArrayList<>();
    private final ArrayList<VField> fields = new ArrayList<>();

    public InputsClassModificator(VClass forClass) {

        if (forClass.getModificator(e->e.getClass() == InputsClassModificator.class).isPresent()) {
            throw new IllegalArgumentException("Class " + forClass + " already have imput modificator");
        }
        this.forClass = forClass;

        for (VConstructor c : forClass.constructors) {
            if (c.getBlock() != null) {
                InputMody im = new InputMody(c);
                c.getBlock().addMod(im);
                blocks.add(im);
            }
        }
    }

    public static InputsClassModificator getOrCreateInputModificator(VClass forClass) {
        Optional<ClassModificator> op = forClass.getModificator(e -> e.getClass() == InputsClassModificator.class);
        if (op.isPresent())
            return (InputsClassModificator) op.get();
        InputsClassModificator icm = new InputsClassModificator(forClass);
        forClass.addMod(icm);
        return icm;
    }

    public ArrayList<VField> getFields() {
        return fields;
    }

    public VField addInput(SVar var) {
        VField field = new VField(var.getRuntimeName(), var.getRuntimeName(), var.getType(), Modifier.PRIVATE | Modifier.FINAL, forClass);
        for (VConstructor c : forClass.constructors) {
            c.addModificator(new InputArgs(c, var, field));


        }
        fields.add(field);
        return field;
    }

    @Override
    public List<VField> getFields(List<VField> fields) {
        fields.addAll(this.fields);
        return fields;
    }

    @Override
    public List<VMethod> getMethods(List<VMethod> methods) {
        return methods;
    }

    public static class InputMody implements BlockModificator {

        private static final long serialVersionUID = 2072268763544151078L;
        private final VConstructor constructor;

        public InputMody(VConstructor constructor) {
            this.constructor = constructor;
        }

        @Override
        public List<Operation> getOperations(List<Operation> operations) {
            for (VArgument ar : constructor.getArguments()) {
                if (ar.getCreator() != null && ar.getCreator().getClass()==InputArgs.class) {
                    InputArgs ia = (InputArgs)ar.getCreator();
                    operations.add(0, new SetField(new This(constructor.getParent()), ia.getLocal(), ia.getArg(), Assign.AsType.ASSIGN, null, null));
                }
            }
            return operations;
        }
    }


    public static class InputArgs implements ArgumentModificator {
        private static final long serialVersionUID = -8738220121695726222L;
        private final VArgument arg;
        private final SVar input;
        private final VField local;

        public InputArgs(VExecute execute, SVar input, VField local) {
            this.input = input;
            this.local = local;
            arg = new VArgument(input.getRuntimeName(), input.getRuntimeName(), input.getType(), false, false, execute, this);
        }

        public VArgument getArg() {
            return arg;
        }

        public SVar getInput() {
            return input;
        }

        public VField getLocal() {
            return local;
        }

        @Override
        public List<VArgument> getArguments(List<VArgument> arguments) {
            arguments.add(arg);
            return arguments;
        }

        @Override
        public void setBody(VBlock oldBody, VBlock newBody) {

        }
    }
}
