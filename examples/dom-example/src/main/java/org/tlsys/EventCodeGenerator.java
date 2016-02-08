package org.tlsys;

import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.lex.declare.VMethod;
import org.tlsys.twt.*;

import java.io.PrintStream;

public class EventCodeGenerator extends DefaultGenerator {
    @Override
    public void generateExecute(GenerationContext context, VExecute execute, PrintStream ps) throws CompileException {
        VClass eventListenerClass = context.getCurrentClass().getClassLoader().loadClass(Events.EventListener.class.getName());
        VClass objectClass = context.getCurrentClass().getClassLoader().loadClass(Object.class.getName());
        VMethod onEventMethod = eventListenerClass.getMethod("onEvent",objectClass,objectClass);
        if (execute.alias.equals("addEventListener")) {
            ps.append("{");

            //Функция события
            ps.append("var f=function(e){")
                    .append(execute.arguments.get(2).name).append(".")
                    .append(onEventMethod.getRunTimeName()).append("(this,e);")
                    .append("};");

            String contener = execute.arguments.get(0).name+".E";
            //создаем контейнер событий
            ps.append(contener).append("=").append(contener).append("||[];");

            ps.append(contener)
                    .append(".push({o:").append(execute.arguments.get(2).name)
                    .append(",n:").append(execute.arguments.get(1).name)
                    .append(",u:").append(execute.arguments.get(3).name)
                    .append(",f:f")
                    .append("});");

            ps.append(execute.arguments.get(0).name).append(".addEventListener(")
                    .append(execute.arguments.get(1).name)
                    .append(",f,").append(execute.arguments.get(3).name).append(");");

            ps.append("}");
            return;
        }

        if (execute.alias.equals("removeEventListener")) {
            ps.append("{");
            String contener = execute.arguments.get(0).name+".E";
            ps.append("if (!").append(contener).append(")return;");

            //цикл по всем событиям. Ищем нужное
            ps.append("for(var i=0;i<").append(contener).append(".length;i++){")
                    .append("var g=").append(contener).append("[i];")
                    .append("if (g.o==").append(execute.arguments.get(2).name)
                    .append("&&g.n==").append(execute.arguments.get(1).name)
                    .append("&&g.u==").append(execute.arguments.get(3).name)
                    .append("){").append(contener).append(".splice(i,1);")
                        .append(execute.arguments.get(0).name).append(".removeEventListener(g.n,g.f,g.u);")
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
