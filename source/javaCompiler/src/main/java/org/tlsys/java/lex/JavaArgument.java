package org.tlsys.java.lex;

import com.github.javaparser.ast.body.Parameter;
import org.tlsys.JavaCompiller;
import org.tlsys.lex.TArgument;
import org.tlsys.lex.TNode;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VExecute;

public class JavaArgument implements TArgument {
    private static final long serialVersionUID = 540512926595151896L;
    private final String name;
    private final transient Parameter desc;
    private final VExecute parent;
    private VClass type;

    public JavaArgument(Parameter desc, VExecute parent) {
        this.desc = desc;
        this.name = desc.getId().getName();

        this.parent = parent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public VClass getType() {
        if (type != null)
            return type;

        type = JavaCompiller.findClass(desc.getType(), parent);
        return type;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}