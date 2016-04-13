package org.tlsys;

import org.tlsys.lex.declare.VArgument;
import org.tlsys.lex.declare.VBlock;
import org.tlsys.lex.declare.VConstructor;

import java.util.List;

public class ParentArgumentModif implements ArgumentModificator {

    private static final long serialVersionUID = -1669335979923501896L;
    private final VConstructor constructor;
    private final VArgument arg;

    public ParentArgumentModif(VConstructor constructor) {
        this.constructor = constructor;
        arg = new VArgument("this$0", null, constructor.getParent().getDependencyParent().get(), false, false, constructor, this, null);

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
