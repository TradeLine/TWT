package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.*;
import org.tlsys.twt.annotations.*;
import org.tlsys.twt.classes.*;
import org.tlsys.twt.rt.java.lang.reflect.TConstructor;
import org.tlsys.twt.rt.java.lang.reflect.TField;
import org.tlsys.twt.rt.java.lang.reflect.TMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@JSClass
@ClassName("java.lang.Class")
@ReplaceClass(Class.class)
@CodeGenerator(NativeCodeGenerator.class)
public class TClass {

    static final String CLASS_IMP = "$";

    private boolean inited = false;
    private String name;
    private Object cons = null;
    private String jsName;
    private Class superClass;
    private JArray<Class> implementList = new JArray<>();
    private JArray<TConstructor> constructors;
    private JArray<TMethod> methods;
    private ClassRecord classRecord;
    private String domNode;
    private JDictionary<Object> lambdaList = new JDictionary<>();
    private JDictionary<TClass> annonimusList = new JDictionary<>();
    private Class arrayClass = null;

    public TClass(String name) {
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }

    public String getSimpleName() {
        return Script.code("this.simpleName");
    }

    public boolean isInited() {
        return inited;
    }

    public void init() {
        //
    }

    public Object getJsClass() {
        return cons;
    }

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

        TClass self = this;
        ClassRecord c = new ClassRecord(this.jsName + name, this.name + "$lambda" + name);
        c.setSuper(() -> CastUtil.cast(self));
        //c.addMethod(method.getMethod());//

        for (int i = 0; i < classRecord.getMethods().length(); i++) {
            MethodRecord mr = classRecord.getMethods().get(i);
            if (mr.getName() == null)
                c.addMethod(mr);
        }

        TClass cc = new TClass("");
        cc.initFor(c);
        Script.code(cc.cons, ".prototype[", methodName, "]=function(){return ", method, ".apply(", scope, ",arguments);}");
        t = Script.code("new ", cc.cons, "()");
        lambdaList.set(name, t);
        return t;
    }

    private void initMethods() {
        if (methods != null)
            return;
        methods = new JArray<>();

        for (int i = 0; i < classRecord.getMethods().length(); i++) {
            MethodRecord mr = classRecord.getMethods().get(i);
            if (mr.getName() == null) {
                continue;
            }
            TMethod mm = new TMethod();
            methods.add(mm);
            mm.name = mr.getName();
            mm.jsName = mr.getJsName();
            mm.staticFlag = mr.isStaticFlag();

            if (mr.isStaticFlag())
                mm.jsFunction = Script.code(this, "[", mr.getJsName(), "]");
            else
                mm.jsFunction = Script.code(this.cons, ".prototype[", mr.getJsName(), "]");

            for (int j = 0; j < mr.getArguments().length(); j++) {
                ArgumentRecord ar = mr.getArguments().get(j);
                mm.arguments.add(ar.getType().getType());
            }
        }
    }

    public TConstructor[] getConstructors() {
        initConstructors();
        TConstructor[] out = new TConstructor[constructors.length()];
        for (int i = 0; i < out.length; i++) {
            out[i] = constructors.get(i);
        }
        return out;
    }


    public TMethod[] getMethods() {
        TMethod[] out = new TMethod[methods.length()];
        for (int i = 0; i < out.length; i++) {
            out[i] = methods.get(i);
        }
        return out;
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
            mm.jsFunction = Script.code(this.cons, ".prototype[", mr.getJsName(), "]");

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
        String fieldInitFlag = cr.getJsName() + "_";
        String functionBody = "";
        /*
        String fieldInit = "if (!this."+fieldInitFlag+"){";

        for (int i = 0; i < cr.getFields().length(); i++) {
            FieldRecord fr = cr.getFields().get(i);
            if (!fr.isStaticFlag()) {
                fieldInit+="this."+fr.getJsName()+"="+fr.getInitValue()+";";
            }
        }
        fieldInit+="this.fieldInitFlag=true;}";
        */
        //Object functionFieldInit = Script.code("new Function(",fieldInit,")");

        if (cr.getSuper() != null)
            superClass = cr.getSuper().getType();
        for (int i = 0; i < cr.getImplementations().length(); i++) {
            implementList.add(cr.getImplementations().get(i).getType());
        }
        
        Console.info("=========Creating class " + cr.getName()+"...=========");
        Console.dir(cr);
        //Script.code(cons,".c=",this);


        cons = Script.code("new Function(", functionBody, ")");
        Script.code(cons, ".prototype[", CLASS_IMP, "]=", this);
        Script.code(cons, "['NEW']=", cons);


        for (int i = 0; i < cr.getMethods().length(); i++) {
            MethodRecord mr = cr.getMethods().get(i);
            if (mr.getName() == null) {
                String arguments = "";
                JArray<String> a = new JArray<>();
                for (int j = 0; j < mr.getArguments().length(); j++) {
                    if (j > 0)
                        arguments += ",";
                    arguments += mr.getArguments().get(j).getName();
                    a.add(mr.getArguments().get(j).getName());
                }
                
                Console.info(cr.getName()+"->"+mr.getName()+"(" + arguments + ")");
                Console.dir(mr.getArguments());
                Console.info("len="+mr.getArguments().length());

                if (domNode == null) {

                    a.add(Script.code("'var o = new ", cons, "();" + "o.'+", mr.getJsName(), "+'.apply(o,arguments); return o;'"));

                } else {
                    a.add(Script.code("'var o = document.createElement(", this.domNode, ");" +
                            "var o = document.createElement(", this.domNode, ");" +
                            "for(var k in ", cons, ".prototype) o[k]=", cons, ".prototype[k];" +
                            "o.'+", mr.getJsName(), "+'.apply(o, arguments);return o;'"));
                    Console.info("CONSTRUCTOR OF " + cr.getName()+"=" + a.get(a.length()-1));
                }
                Script.code(this, "['n'+", mr.getJsName(), "]=Function.apply(null,", a.getJSArray(), ")");
            }

            if (mr.isStaticFlag() && mr.getName() != null) {
                Script.code(this, "[", mr.getJsName(), "]=", mr.getBody());
            } else {
                Script.code(cons, ".prototype[", mr.getJsName(), "]=", mr.getBody());
                if (mr.getName().equals("toString") && mr.getArguments().length() == 0) {
                    Script.code(cons, ".prototype.toString=", mr.getBody());
                }
            }
        }

        for (int i = 0; i < cr.getFields().length(); i++) {
            FieldRecord fr = cr.getFields().get(i);
            if (Modifier.isStatic(fr.getModificators())) {
                Script.code(this, "[", fr.getJsName(), "]=eval(", fr.getInitValue(), ")");
            }
        }

        Console.info("======="+cr.getName() + " copy parent==========");
        Class t = superClass;
        while (t != null) {
            TClass tt = CastUtil.cast(t);
            Console.info("----Read class " + tt.getName()+"----");
            for (int i = 0; i < tt.classRecord.getMethods().length(); i++) {
                MethodRecord mr = tt.classRecord.getMethods().get(i);
                if (mr.getName() == null)
                    continue;
                if (mr.isStaticFlag()) {
                    if (Script.hasOwnProperty(this, mr.getJsName()))
                        continue;
                    Script.code(this, "[", mr.getJsName(), "]=", mr.getBody());
                } else {
                    if (Script.hasOwnProperty(Script.code(cons, ".prototype"), mr.getJsName())) {
                        Console.info("Method " + mr.getName() + " exist!");
                        continue;
                    }
                    Console.info("Method " + mr.getName() + " copped!");
                    Script.code(cons, ".prototype[", mr.getJsName(), "]=", mr.getBody());
                    if (mr.getName().equals("toString") && mr.getArguments().length() == 0) {
                        Script.code(cons, ".prototype.toString=", mr.getBody());
                    }
                }
            }
            t = t.getSuperclass();
        }


        /*
        if (superClass != null) {




            TClass ss = CastUtil.cast(superClass);
            ss.initMethods();
            for (int i = 0; i < ss.methods.length(); i++) {
                initMethods();
                TMethod mm = getMethodByJSName(ss.methods.get(i).jsName);
                if (mm == null) {
                    mm = ss.methods.get(i);

                }
            }
        }
        */

        for (int i = 0; i < cr.getStatics().length(); i++) {
            Script.code(cr.getStatics().get(i), "()");
        }
    }

    private Object newInstance() throws InstantiationException {
        for (TConstructor c : getConstructors()) {
            if (c.getParameterCount() == 0) {
                return Script.code(this, "['n'+", c.jsName, "]()");
            }
        }
        throw new InstantiationException("Can't find constructor whout arguments");
    }

    private TMethod getMethodByJSName(String name) {
        for (int i = 0; i < methods.length(); i++) {
            if (methods.get(i).jsName.equals(name))
                return methods.get(i);
        }
        return null;
    }

    @JSName("isPrimitive")
    public boolean isPrimitive() {
        return getName().equals("char") || getName().equals("byte") || getName().equals("short") || getName().equals("int") || getName().equals("long") || getName().equals("float") || getName().equals("double") || getName().equals("boolean");
    }

    @JSName("getSuperClass")
    public Class getSuperclass() {
        return superClass;
    }

    @JSName("isArray")
    public boolean isArray() {
        return component != null;
    }

    @CodeGenerator(GenArrayClassCreateMethod.class)
    private native Class initArrayClass();

    public Class getArrayClass() {
        if (arrayClass == null) {
            arrayClass = initArrayClass();
            TClass cl = CastUtil.cast(arrayClass);
            cl.component = this;
        }
        return arrayClass;
    }

    public Object cast(Object obj) {
        if (obj == null)
            return null;
        if (isInstance(obj))
            return obj;
        throw new ClassCastException("Can not cast from " + getName() + " to " + obj.getClass().getName());
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

    private Field[] fields = null;

    public Field[] getFields() {
        if (fields == null) {
            fields = new Field[classRecord.getFields().length()];
            for (int i = 0; i < fields.length; i++) {
                FieldRecord fr = classRecord.getFields().get(i);
                Console.info("FIELD " + fr.getName() + " " + Modifier.isStatic(fr.getModificators()));
                TField f = new TField(fr.getName(), fr.getJsName(), CastUtil.cast(this), fr.getType().getType(), fr.getModificators());
                fields[i] = CastUtil.cast(f);
            }
        }
        return fields;
    }

    public TClass component;

    public Class getComponentType() {
        return CastUtil.cast(component);
    }
}
