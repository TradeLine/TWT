package org.tlsys;

import org.tlsys.lex.Assign;
import org.tlsys.lex.Operation;
import org.tlsys.lex.SetField;
import org.tlsys.lex.This;
import org.tlsys.lex.declare.*;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

public class OtherClassLink implements ClassModificator {

    private final VClass toClass;
    private final VField field;

    public OtherClassLink(VClass forClass, VClass toClass) {
        this.toClass = toClass;
        field = new VField("c" + Integer.toString(toClass.hashCode()).replace('-', '_'), null, toClass, Modifier.PUBLIC, forClass);

        for (VConstructor con : forClass.constructors) {
            con.getMods().add(new ArgumentLink(toClass, con, this));
        }
    }

    public static OtherClassLink getOrCreate(VClass forClass, VClass toClass) {
        Optional<ClassModificator> cm = forClass.getModificator(e -> {
            if (e instanceof OtherClassLink) {
                OtherClassLink m = (OtherClassLink) e;
                if (m.toClass == toClass)
                    return true;
            }
            return false;
        });

        if (cm.isPresent())
            return (OtherClassLink) cm.get();

        OtherClassLink ocl = new OtherClassLink(forClass, toClass);
        forClass.addMod(ocl);
        return ocl;
    }

    public VField getField() {
        return field;
    }

    public VClass getToClass() {
        return toClass;
    }

    @Override
    public List<VField> getFields(List<VField> fields) {
        fields.add(field);
        return fields;
    }

    @Override
    public List<VMethod> getMethods(List<VMethod> fields) {
        return fields;
    }

    public static class ArgumentLink implements ArgumentModificator {

        private static final long serialVersionUID = -3340377371594356849L;
        private final VClass toClass;
        private final VArgument arg;
        //private final VConstructor constructor;
        private final OtherClassLink otherClassLink;

        private final ConstructorBlockArgs cba;

        public ArgumentLink(VClass toClass, VConstructor con, OtherClassLink otherClassLink) {
            this.toClass = toClass;
            this.otherClassLink = otherClassLink;
            //this.constructor = constructor;

            String str = "to" + toClass.getRealName().replace('.', '_');
            arg = new VArgument(str, str, toClass, false, false, con, this);

            cba = new ConstructorBlockArgs(con, this);

            if (con.getBlock() != null)
                con.getBlock().addMod(cba);
        }

        public VArgument getArg() {
            return arg;
        }

        public VClass getToClass() {
            return toClass;
        }

        @Override
        public List<VArgument> getArguments(List<VArgument> arguments) {
            arguments.add(getArg());
            return arguments;
        }

        @Override
        public void setBody(VBlock oldBody, VBlock newBody) {
            if (oldBody != null)
                oldBody.removeMod(cba);
            if (newBody != null)
                newBody.addMod(cba);
        }
    }

    public static class ConstructorBlockArgs implements BlockModificator {

        private static final long serialVersionUID = 3790360979353215588L;
        private final VConstructor constructor;
        private final ArgumentLink otherClassLink;

        private SetField sf;

        public ConstructorBlockArgs(VConstructor constructor, ArgumentLink otherClassLink) {
            this.constructor = constructor;
            this.otherClassLink = otherClassLink;

/*
            VArgument[] args = constructor.getArguments().stream().filter(e->{
                if (e.getCreator() != null && e.getCreator().getClass() == ArgumentLink.class) {
                    ArgumentLink al = (ArgumentLink)e.getCreator();
                    if (al.getToClass() == otherClassLink.getToClass())
                        return true;
                }
                return false;
            }).toArray(VArgument[]::new);
            if (args.length != 1)
                throw new RuntimeException("Bad argument count");
                */
            sf = new SetField(new This(constructor.getParent()), otherClassLink.otherClassLink.getField(), otherClassLink.getArg(), Assign.AsType.ASSIGN, null, null);
        }

        @Override
        public List<Operation> getOperations(List<Operation> operations) {
            operations.add(0, sf);
            return operations;
        }
    }
}
