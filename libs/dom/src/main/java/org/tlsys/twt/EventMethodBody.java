package org.tlsys.twt;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.VExecute;

import java.io.PrintStream;
import java.lang.reflect.Executable;

public class EventMethodBody implements ICodeGenerator {
    /*
    @Override
    public void gen(MainGenerationContext ctx, JCTree.JCMethodDecl decl, Executable method, PrintStream ps) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {

        if (method.getName().equals("addEventListener")) {
            ps.append("{");
            String dom = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(0));
            String eventName = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(1));
            String obj = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(2));
            String useCapture = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(3));

            ps
                    .append("var ev=function(e){")
                    .append("var o=" + obj + ".onEvent.call(" + obj + ",this,e);")
                    .append("if(o!=null&&o!=undefined)return o;")
                    .append("};");

            ps
                    .append(dom).append(".EVENTS=").append(dom).append(".EVENTS || [];")
                    .append(dom).append(".EVENTS.push({name:"+eventName+",obj:"+obj+",u:"+useCapture+",o:ev});")
                    .append(dom).append(".addEventListener("+eventName+", ev, "+useCapture+");");
            ps.append("}");
            return;
        }

        if (method.getName().equals("removeEventListener")) {
            ps.append("{");
            String dom = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(0));
            String eventName = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(1));
            String obj = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(2));
            String useCapture = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(3));

            ps.append("if(!").append(dom).append(".EVENTS)")
                    .append("return;");

            ps.append("for(var i=0;i<").append(dom).append(".EVENTS.length; i++) {")
                    .append("var item = ").append(dom).append(".EVENTS[i];")
                    .append("if(item.name==").append(eventName).append("&&item.obj==").append(obj).append("&&item.u==").append(useCapture).append("){")
                    .append(dom).append(".removeEventListener(").append(eventName).append(",item.o,item.u);")
                    .append(dom).append(".EVENTS.splice(i, 1);")
                    .append("break;")
                    .append("}")
                    .append("}")

                    .append("if(").append(dom).append(".EVENTS.length == 0)")
                    .append("delete ").append(dom).append(".EVENTS;");
            ps.append("}");
            return;
        }

        if (method.getName().equals("setEvent")) {
            ps.append("{");
            String dom = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(0));
            String eventName = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(1));
            String obj = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(2));

            ps.append("if(").append(dom).append("['on'+").append(eventName).append("]&&obj==null) {")
                .append(dom).append("['on'+").append(eventName).append("]=undefined;")
                .append("return;")
            .append("}")

            .append("if(").append(obj).append("!=null) {")
                .append("var ev=function(e){")
                    .append("var o=").append(obj).append(".onEvent.call(").append(obj).append(",this,e);")
                    .append("console.error('event return '+o);")
                    .append("if(o!=null&&o!=undefined)")
                        .append("return o;")
                .append("};")
                .append("ev.OBJECT=").append(obj).append(";")
                .append(dom).append("['on'+").append(eventName).append("]=ev;");
            ps.append("}");
            ps.append("}");
            return;
        }

        if (method.getName().equals("getEvent")) {
            ps.append("{");
            String dom = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(0));
            String eventName = ctx.getCompileContext().getNamer().getVarName(decl.getParameters().get(1));

            ps.append("if(!").append(dom).append("['on'+").append(eventName).append("])")
                    .append("return null;")
                    .append("return ").append(dom).append("['on'+").append(eventName).append("].OBJECT;");
            ps.append("}");
            return;
        }


        throw new RuntimeException("Unknown method " + method);
    }
*/
    @Override
    public void generateClass(GenerationContext context, CompileModuls.ClassRecord record, PrintStream ps) throws CompileException {
        throw new RuntimeException("Nut supported yet!");
    }

    @Override
    public boolean operation(GenerationContext context, Operation operation, PrintStream out) throws CompileException {
        throw new RuntimeException("Nut supported yet!");
    }

    @Override
    public void generateExecute(GenerationContext context, VExecute execute, PrintStream ps) throws CompileException {
        throw new RuntimeException("Nut supported yet!");
    }
}
