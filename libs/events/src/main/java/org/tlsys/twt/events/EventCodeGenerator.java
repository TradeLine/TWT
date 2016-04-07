package org.tlsys.twt.events;

import org.tlsys.CodeBuilder;
import org.tlsys.Outbuffer;
import org.tlsys.lex.Const;
import org.tlsys.lex.Invoke;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.lex.declare.VMethod;
import org.tlsys.twt.CompileException;
import org.tlsys.twt.CompileModuls;
import org.tlsys.twt.DefaultGenerator;
import org.tlsys.twt.GenerationContext;

import java.util.Objects;

public class EventCodeGenerator extends DefaultGenerator {
    @Override
    public void generateExecute(GenerationContext context, VExecute execute, Outbuffer ps, CompileModuls moduls) throws CompileException {
        VClass eventListenerClass = context.getCurrentClass().getClassLoader().loadClass(Events.EventListener.class.getName(), execute.getPoint());
        VClass objectClass = context.getCurrentClass().getClassLoader().loadClass(Object.class.getName(), execute.getPoint());
        VClass objectsClass = context.getCurrentClass().getClassLoader().loadClass(Objects.class.getName(), execute.getPoint());
        VClass stringClass = context.getCurrentClass().getClassLoader().loadClass(String.class.getName(), execute.getPoint());
        VMethod onEventMethod = eventListenerClass.getMethod("onEvent", execute.getPoint(), objectClass, objectClass);
        if (execute.alias.equals("addEventListener")) {
            ps.append("{");
            Invoke inv = CodeBuilder.scopeStatic(objectsClass).method("requireNonNull").arg(objectClass).arg(stringClass).invoke(execute.getPoint()).arg(execute.getArguments().get(0)).arg(new Const("Argument listener is NULL", stringClass)).build();
            //Invoke inv = new Invoke(objectsClass.getMethod("requireNonNull", execute.getPoint(), objectClass,stringClass), new StaticRef(objectsClass)).addArg(execute.getArguments().get(0)).addArg(new Const("Argument listener is NULL",stringClass));
            moduls.add(inv.getMethod());
            context.getGenerator(objectsClass).operation(context,
            inv
                    , ps);
            ps.append(";");

            //Функция события
            ps.append("var f=function(e){")
                    .append(execute.getArguments().get(2).getRuntimeName()).append(".")
                    .append(onEventMethod.getRunTimeName()).append(".call(").append(execute.getArguments().get(2).getRuntimeName()).append(",this,e);")
                    .append("};");

            String contener = execute.getArguments().get(0).getRuntimeName()+".E";
            //создаем контейнер событий
            ps.append(contener).append("=").append(contener).append("||[];");

            ps.append(contener)
                    .append(".push({o:").append(execute.getArguments().get(2).getRuntimeName())
                    .append(",n:").append(execute.getArguments().get(1).getRuntimeName())
                    .append(",u:").append(execute.getArguments().get(3).getRuntimeName())
                    .append(",f:f")
                    .append("});");

            ps.append(execute.getArguments().get(0).getRuntimeName()).append(".addEventListener(")
                    .append(execute.getArguments().get(1).getRuntimeName())
                    .append(",f,").append(execute.getArguments().get(3).getRuntimeName()).append(");");

            ps.append("}");
            return;
        }

        if (execute.alias.equals("removeEventListener")) {
            ps.append("{");
            String contener = execute.getArguments().get(0).getRuntimeName()+".E";
            ps.append("if (!").append(contener).append(")return;");

            ps.append("for(var i=0;i<").append(contener).append(".length;i++){")
                    .append("var g=").append(contener).append("[i];")
                    .append("if (g.o==").append(execute.getArguments().get(2).getRuntimeName())
                    .append("&&g.n==").append(execute.getArguments().get(1).getRuntimeName())
                    .append("&&g.u==").append(execute.getArguments().get(3).getRuntimeName())
                    .append("){").append(contener).append(".splice(i,1);")
                        .append(execute.getArguments().get(0).getRuntimeName()).append(".removeEventListener(g.n,g.f,g.u);")
                    .append("break;")
                    .append("}")
                    .append("}");

            ps.append("if (").append(contener).append(".length==0)")
                    .append("delete ").append(contener).append(";");
            ps.append("}");
            return;
        }

        throw new RuntimeException("Unknown method " + execute.alias);
    }
}
