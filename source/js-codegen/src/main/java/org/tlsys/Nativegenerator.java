package org.tlsys;


import org.tlsys.twt.generate.NameContext;
import org.tlsys.twt.members.*;

import java.io.IOException;
import java.lang.reflect.Modifier;

public class Nativegenerator {
    public void generate(VClass clazz, Output buffer, NameContext nameContext) throws IOException {
        final String CL = "a";
        buffer.append("var ").append(nameContext.getName(clazz)).append("=(function(){");
        buffer.append("function "+CL+"(){");

        clazz.getMembers().stream().filter(e->e instanceof TField).forEach(e->{
            TField f = (TField)e;
            buffer.append("this.").append(nameContext.getName(f)).append("=");
            buffer.append(";");
        });

        buffer.append("}");
        clazz.getMembers().stream().filter(e->e instanceof VExecute).forEach(e->{
            buffer.append(CL+".");
            if (!Modifier.isStatic(e.getModifiers())) {
                buffer.append("prototype.");
            }

            if (e instanceof VMethod)
                buffer.append(nameContext.getName((VMethod)e));

            if (e instanceof TConstructor)
                buffer.append(nameContext.getName((TConstructor)e));
            buffer.append("(");
            VExecute ee = (VExecute)e;
            for (int i = 0; i < ee.getArguments().size(); i++) {
                TArgument arg = ee.getArguments().get(i);
                if (i != 0)
                    buffer.append(",");
                buffer.append(arg.getName());
            }
            buffer.append("){");
            buffer.append("};");
        });
        buffer.append("return " + CL + "}());");
    }
}
