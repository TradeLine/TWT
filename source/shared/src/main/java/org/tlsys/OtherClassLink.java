package org.tlsys;

import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;

import java.util.List;
import java.util.Optional;

public class OtherClassLink implements ClassModificator {

    private final VClass toClass;
    private final VField field;

    public VField getField() {
        return field;
    }

    public OtherClassLink(VClass forClass, VClass toClass) {
        this.toClass = toClass;
        field = new VField("c"+Integer.toString(toClass.hashCode()).replace('-','_'), null, toClass, 0, forClass);

        for (VConstructor con : forClass.constructors) {
            con.getMods().add(new ArgumentLink(toClass, con));
            if (con.block != null)
                con.block.addMod(new ConstructorBlockArgs(con, this));
        }
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

    public static OtherClassLink getOrCreate(VClass forClass, VClass toClass) {
        Optional<ClassModificator> cm = forClass.getModificator(e->{
            if (e instanceof OtherClassLink) {
                OtherClassLink m = (OtherClassLink)e;
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

    public static class ArgumentLink implements ArgumentModificator {

        private final VClass toClass;
        private final VArgument arg;
        //private final VConstructor constructor;

        public ArgumentLink(VClass toClass, VConstructor constructor) {
            this.toClass = toClass;
            //this.constructor = constructor;

            String str = "to" + Integer.toString(toClass.hashCode()).replace('-','_');
            arg = new VArgument(str, str, toClass, false, false, constructor, this);
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
    }

    public static class ConstructorBlockArgs implements BlockModificator {

        private final VConstructor constructor;
        private final OtherClassLink otherClassLink;

        private SetField sf;

        public ConstructorBlockArgs(VConstructor constructor, OtherClassLink otherClassLink) {
            this.constructor = constructor;
            this.otherClassLink = otherClassLink;


            VArgument[] args = constructor.getArguments().stream().filter(e->{
                if (e.getCreator().getClass() == ArgumentLink.class) {
                    ArgumentLink al = (ArgumentLink)e.getCreator();
                    if (al.getToClass() == otherClassLink.getToClass())
                        return true;
                }
                return false;
            }).toArray(VArgument[]::new);
            if (args.length != 1)
                throw new RuntimeException("Bad argument count");
            sf = new SetField(new This(constructor.getParent()), otherClassLink.getField(), args[0], Assign.AsType.ASSIGN);
        }

        @Override
        public List<Operation> getOperations(List<Operation> operations) {
            operations.add(0, sf);
            return operations;
        }
    }
}
