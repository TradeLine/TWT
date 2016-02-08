package org.tlsys.twt.rt.java.lang;

import org.tlsys.lex.Cast;
import org.tlsys.twt.*;
import org.tlsys.twt.annotations.*;
import org.tlsys.twt.classes.*;
import org.tlsys.twt.rt.java.lang.reflect.TConstructor;
import org.tlsys.twt.rt.java.lang.reflect.TExecutable;
import org.tlsys.twt.rt.java.lang.reflect.TField;
import org.tlsys.twt.rt.java.lang.reflect.TMethod;

import java.lang.reflect.Field;

@JSClass
@ClassName("java.lang.Class")
@ReplaceClass(Class.class)
@CodeGenerator(NativeCodeGenerator.class)
public class TClass {
    //@InvokeGen("org.tlsys.twt.rt.java.lang.ClassInvoke")
    public String getName() {
        return Script.code("this.fullName");
    }

    //@InvokeGen("org.tlsys.twt.rt.java.lang.ClassInvoke")
    public String toString() {
        return getName();
    }

    //@InvokeGen("org.tlsys.twt.rt.java.lang.ClassInvoke")
    public String getSimpleName() {
        return Script.code("this.simpleName");
    }

    private boolean inited = false;

    public boolean isInited() {
        return inited;
    }

    public void init() {
        //
    }

    private String name;

    private Object cons = null;
    private String jsName;

    public Object getJsClass() {
        return cons;
    }

    private JArray<TField> fields = new JArray<>();

    private Class superClass;
    private JArray<Class> implementList = new JArray<>();
    private JArray<TConstructor> constructors;
    private JArray<TMethod> methods;

    private ClassRecord classRecord;

    private String domNode;

    private JDictionary<Object> lambdaList = new JDictionary<>();
    private JDictionary<TClass> annonimusList = new JDictionary<>();

    public TClass getAnnonimus(String name, AnnonimusProvider annonimusProvider) {

        TClass cc = annonimusList.get(name);
        if (cc != null)
            return cc;
        cc = new TClass("");
        cc.initFor(annonimusProvider.getRecord());
        annonimusList.set(name, cc);
        return cc;
    }

    public Object getLambda(String name, String methodName, Object method, Object scope) {
        Object t = lambdaList.get(name);
        if (t != null)
            return t;


        ClassRecord c = new ClassRecord(this.jsName+name, this.name+"$lambda"+name);
        c.setSuper(()->CastUtil.cast(this));
        //c.addMethod(method.getMethod());//

        for (int i = 0; i < classRecord.getMethods().length(); i++) {
            MethodRecord mr = classRecord.getMethods().get(i);
            if (mr.getName() == null)
                c.addMethod(mr);
        }

        TClass cc = new TClass("");
        cc.initFor(c);
        Script.code(cc.cons,".prototype[",methodName,"]=function(){return ",method,".apply(",scope,",arguments);}");
        t = Script.code("new ",cc.cons,"()");
        lambdaList.set(name, t);
        return t;
    }

    private void initMethods() {
        if (methods != null)
            return;
        methods = new JArray<>();

        for (int i = 0; i < classRecord.getMethods().length(); i++) {
            MethodRecord mr = classRecord.getMethods().get(i);
            if (mr.getName() == null)
                continue;

            TMethod mm = new TMethod();
            methods.add(mm);
            mm.name = mr.getName();
            mm.staticFlag = mr.isStaticFlag();

            if (mr.isStaticFlag())
                mm.jsFunction = Script.code(this,"[",mr.getJsName(),"]");
            else
                mm.jsFunction = Script.code(this.cons,".prototype[",mr.getJsName(),"]");

            for (int j = 0; j < mr.getArguments().length(); j++) {
                ArgumentRecord ar = mr.getArguments().get(j);
                mm.arguments.add(ar.getType().getType());
            }
        }
    }

    private void initConstructors() {
        if (constructors != null)
            return;
        constructors = new JArray<>();

        for (int i = 0; i < classRecord.getMethods().length(); i++) {
            MethodRecord mr = classRecord.getMethods().get(i);
            if (mr.getName() != null)
                continue;

            TConstructor mm = new TConstructor();
            constructors.add(mm);

            mm.jsName = mr.getJsName();
            mm.parentClass = CastUtil.cast(this);
            mm.jsFunction = Script.code(this.cons,".prototype[",mr.getJsName(),"]");

            for (int j = 0; j < mr.getArguments().length(); j++) {
                ArgumentRecord ar = mr.getArguments().get(j);
                mm.arguments.add(ar.getType().getType());
            }
        }
    }

    public void initFor(ClassRecord cr) {
        domNode = cr.getDomNode();
        this.jsName = cr.getJsName();
        classRecord = cr;
        this.name = cr.getName();
        String fieldInitFlag = cr.getJsName()+"_";
        String functionBody = "";
        String fieldInit = "if (!this."+fieldInitFlag+"){";

        for (int i = 0; i < cr.getFields().length(); i++) {
            FieldRecord fr = cr.getFields().get(i);
            if (!fr.isStaticFlag()) {
                fieldInit+="this."+fr.getJsName()+"="+fr.getInitValue()+";";
            }
        }
        fieldInit+="this.fieldInitFlag=true;}";
        //Object functionFieldInit = Script.code("new Function(",fieldInit,")");

        if (cr.getSuper() != null)
            superClass = cr.getSuper().getType();
        for (int i = 0; i < cr.getImplementations().length(); i++) {
            implementList.add(cr.getImplementations().get(i).getType());
        }

        //Script.code(cons,".c=",this);



        cons = Script.code("new Function(",functionBody,")");
        Script.code(cons,"['NEW']=",cons);

        for (int i = 0; i < cr.getMethods().length(); i++) {
            MethodRecord mr = cr.getMethods().get(i);
            JArray<String> args = new JArray<>();
            /*

            */

            for (int j = 0; j < mr.getArguments().length(); j++) {
                ArgumentRecord ar = mr.getArguments().get(j);
                args.add(ar.getName());
            }

            if (mr.getName() == null) {
                //TConstructor con = (TConstructor)exe;

                if (Object.class != CastUtil.cast(this)) {
                    int p = mr.getBody().indexOf(";");
                    //TODO добавить проверку на p=-1
                    String callConstructor = mr.getBody().substring(0, p+1);
                    String body = mr.getBody().substring(callConstructor.length());
                    args.add(callConstructor+fieldInit+body);
                } else
                    args.add(fieldInit + mr.getBody());

                if (domNode == null) {
                    JArray<String> a = new JArray<>();
                    String arguments = "";
                    for (int j = 0; j < mr.getArguments().length(); j++) {
                        if (j>0)
                            arguments+=",";
                        arguments+=mr.getArguments().get(j).getName();
                        a.add(mr.getArguments().get(j).getName());
                    }
                    a.add(Script.code("'var o = new ", cons, "();" +
                            "o.'+", mr.getJsName(), "+'('+",arguments,"+'); return o;'"));
                    Script.code(this, "['n'+", mr.getJsName(), "]=Function.apply(null,",a.getJSArray(),")");
                }else {
                    Script.code(this,"['n'+",mr.getJsName(),"]=new Function('" +
                            "var o = document.createElement(",this.domNode,");" +
                            "for(var k in ",cons,".prototype) o[k]=",cons,".prototype[k];" +
                            "o.'+",mr.getJsName(),"+'();return o;')");
                }
            } else {
                args.add(mr.getBody());
            }

            Object func = Script.code("Function.apply(null, ",args.getJSArray(),")");

            if (mr.isStaticFlag()) {
                Script.code(this,"[",mr.getJsName(),"]=",func);
            } else {
                Script.code(cons, ".prototype[", mr.getJsName(), "]=", func);
            }
        }

        for (int i = 0; i < cr.getFields().length(); i++) {
            FieldRecord fr = cr.getFields().get(i);
            if (fr.isStaticFlag()) {
                Script.code(this,"[",fr.getJsName(),"]=eval(",fr.getInitValue(),")");
            }
        }

        if (superClass != null) {
            TClass ss = CastUtil.cast(superClass);
            ss.initMethods();
            for (int i = 0; i < ss.methods.length(); i++) {
                initMethods();
                TMethod mm = getMethodByJSName(ss.methods.get(i).jsName);
                if (mm == null) {
                    mm = ss.methods.get(i);
                    if (mm.staticFlag) {
                        Script.code(this,"[",mm.jsName,"]=",mm.jsFunction);
                    } else {
                        Script.code(cons,".prototype[",mm.jsName,"]=",mm.jsFunction);
                    }
                }
            }
        }
    }

    private TMethod getMethodByJSName(String name) {
        for (int i = 0; i < methods.length(); i++) {
            if (methods.get(i).jsName.equals(name))
                return methods.get(i);
        }
        return null;
    }

    @JSName("isPrimitive")
    public boolean isPrimitive(){
        return Script.code("this.primitive");
    }

    @JSName("getSuperClass")
    public Class getSuperclass() {
        return Script.code("this.ex");
    }

    @JSName("isArray")
    public boolean isArray() {
        return Script.code("Object.getPrototypeOf(this)==AT");
    }

    private Class arrayClass = null;

    @CodeGenerator(GenArrayClassCreateMethod.class)
    private native Class initArrayClass();

    public Class getArrayClass() {
        if (arrayClass == null) {
            arrayClass = initArrayClass();
        }
        return arrayClass;
    }

    public TClass(String name) {
    }

    public Object cast(Object obj) {
        if (obj == null)
            return null;
        if (isInstance(obj))
            return obj;
        throw new ClassCastException("Can not cast from " + getName() + " to " + obj.getClass().getName() );
    }

    public boolean isInstance(Object obj) {
        if (obj == null)
            return false;
        return isAssignableFrom(obj.getClass());
    }

    public boolean isAssignableFrom(Class cls) {
        Class t = CastUtil.cast(this);
        while (cls != null) {
            if (cls == t)
                return true;
            cls = cls.getSuperclass();
        }
        return false;
    }

    public Field getField(String name) {
        for (Field f : getFields()) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    public Field[] getFields() {
        return Script.code("this.meta.fields");
    }

    public Class getComponentType() {
        if (!isArray())
            return null;
        return Script.code("getClass(",this,".type,","this.len-1)");
    }
}
