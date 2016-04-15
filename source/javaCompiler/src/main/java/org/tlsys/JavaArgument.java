package org.tlsys;

import com.github.javaparser.ast.body.Parameter;
import org.tlsys.lex.members.TArgument;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VExecute;

public class JavaArgument implements TArgument {
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
}
