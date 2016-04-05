package org.tlsys.twt.classes;

import org.tlsys.twt.JArray;
import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

import java.lang.reflect.Modifier;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class ClassRecord {
    private String jsName;
    private String name;
    private String domNode;
    private JArray<FieldRecord> fields = new JArray<>();
    private JArray<MethodRecord> methods = new JArray<>();
    private JArray<TypeProvider> imps = new JArray<>();
    private TypeProvider superClass;
    private JArray<Object> statics = new JArray<>();

    private Object prototype;
    private ClassRecord arrayClassRecord;

    public ClassRecord(String jsName, String name) {
        this.jsName = jsName;
        this.name = name;
    }

    public JArray<Object> getStatics() {
        return statics;
    }

    public String getName() {
        return name;
    }

    public String getDomNode() {
        return domNode;
    }

    public ClassRecord setDomNode(String domNode) {
        this.domNode = domNode;
        return this;
    }

    public ClassRecord addStatic(Object block) {
        statics.add(block);
        return this;
    }

    public JArray<FieldRecord> getFields() {
        return fields;
    }

    public JArray<MethodRecord> getMethods() {
        return methods;
    }

    public ClassRecord addImplement(TypeProvider imp) {
        imps.add(imp);
        return this;
    }

    public JArray<TypeProvider> getImplementations() {
        return imps;
    }

    public TypeProvider getSuper() {
        return superClass;
    }

    public ClassRecord setSuper(TypeProvider superClass) {
        this.superClass = superClass;
        return this;
    }

    public ClassRecord addField(String jsName, String name, TypeProvider type, String initValue, int mods) {
        FieldRecord fr = new FieldRecord(jsName, name, this, type, initValue, mods);
        fields.add(fr);
        return this;
    }

    public ClassRecord addMethod(MethodRecord mr) {
        methods.add(mr);
        return this;
    }

    public String getJsName() {
        return jsName;
    }

    public Object getPrototype() {
        if (prototype != null) {
            return prototype;
        }

        Script.code("console.info('init '+", name, ")");

        prototype = Script.code("function(){}");

        for (int i = 0; i < methods.length(); i++) {
            MethodRecord mr = methods.get(i);
            if (mr.getName() == null) {//This is Constructor?
                String consName = "n" + mr.getJsName();
                Script.code(prototype, ".[", consName, "]=new Function('var o=new ", prototype, "();o.", mr.getJsName(), ".apply(o, arguments);return o;')", mr.getBody());
            }
        }

        applyClassBody(prototype);

        return prototype;
    }

    protected void applyClassBody(Object obj) {
        for (int i = 0; i < fields.length(); i++) {
            FieldRecord fr = fields.get(i);

            if ((fr.getModificators() & Modifier.STATIC) == 0)
                continue;
            Script.code(obj, ".obj[", fr.getJsName(), "]=null");
        }

        for (int i = 0; i < methods.length(); i++) {
            MethodRecord mr = methods.get(i);

            if (mr.isStaticFlag()) {
                Script.code(obj, "[", mr.getJsName(), "]=", mr.getBody());
            } else {
                if (!Script.hasOwnProperty(Script.code(obj, ".prototype"), mr.getJsName()))
                    Script.code(obj, ".prototype[", mr.getJsName(), "]=", mr.getBody());
            }
        }

        if (superClass != null) {
            superClass.getType().applyClassBody(prototype);
        }

        for (int i = 0; i < imps.length(); i++) {
            imps.get(i).getType().applyClassBody(prototype);
        }
    }

    public Class getAsClass() {
        return null;
    }

    public ClassRecord getArrayClassRecord() {
        if (arrayClassRecord != null)
            return arrayClassRecord;
        RuntimeException t = Script.code("new Error('Not implemented yet')");
        throw t;
    }
}
