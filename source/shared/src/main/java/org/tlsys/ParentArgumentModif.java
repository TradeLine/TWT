package org.tlsys;

import org.tlsys.lex.declare.VArgument;
import org.tlsys.lex.declare.VConstructor;

import java.util.List;

public class ParentArgumentModif implements ArgumentModificator {

    private final VConstructor constructor;
    private final VArgument arg;

    public ParentArgumentModif(VConstructor constructor) {
        this.constructor = constructor;
        arg = new VArgument("this$0", constructor.getParent().getDependencyParent().get(), false, false, constructor, this);

    }

    @Override
    public List<VArgument> getArguments(List<VArgument> arguments) {
        arguments.add(arg);
        return arguments;
    }
}
