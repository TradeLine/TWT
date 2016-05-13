package org.tlsys.twt.classes;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.JArray;
import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.java.lang.GenArrayClassCreateMethod;
import org.tlsys.twt.rt.java.lang.TClass;
import org.tlsys.twt.rt.java.lang.TObject;

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
    private ClassRecord component = null;
    private Object prototype;
    private ClassRecord arrayClassRecord;
    private TClass clazz;
    public ClassRecord(String jsName, String name) {
        this.jsName = jsName;
        this.name = name;
    }

    public ClassRecord getComponentType() {
        if (Script.isUndefined(component))
            return null;
        return component;
    }

    public ClassRecord setComponentType(ClassRecord component) {
        this.component = component;
        return this;
    }

    public JArray<Object> getStatics() {
        return statics;
    }

    public String getName() {
        return name;
    }

    public String getDomNode() {
        if (domNode == null || Script.isUndefined(domNode)) {
            if (getSuper() != null) {
                return getSuper().getType().getDomNode();
            }
            return null;
        }
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
        if (Script.isUndefined(superClass))
            return null;
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

    public Object createUnsafe() {
        if (getAsClass().isPrimitive())
            throw new RuntimeException("Can't create primitive");
        Object tempProto = prototype;

        if (getDomNode() == null) {
            return Script.code("new ", tempProto, "()");
        } else {
            TObject temp = Script.code("document.createElement(", getDomNode(), ")");
            Script.code("for(k in ", tempProto, ".prototype) o[k]=", tempProto, ".prototype[k]");
            temp.setHashCode(TObject.genHashCode());
            return CastUtil.cast(temp);
        }
    }

    private void createConstructor(MethodRecord mr) {
        //MethodRecord mr = methods.get(i);
        if (mr.getName() == null) {//This is Constructor?
            String nam = mr.getJsName();
            Object tempProto = prototype;
            String dom = getDomNode();
            if (dom == null)
                Script.code(prototype, "['n'+", nam, "]=function(){var o=new ", tempProto, "();o[", nam, "].apply(o, arguments);return o;}");
            else
                Script.code(prototype, "['n'+", nam, "]=function(){var o=document.createElement(", dom, "); for(k in ", tempProto, ".prototype) o[k]=", tempProto, ".prototype[k];o[", nam, "].apply(o, arguments);return o;}");
        }
    }

    public Object getPrototype() {
        if (prototype != null) {
            return prototype;
        }

        prototype = Script.code("function(){}");
        Script.code(prototype, ".prototype[", TObject.CLASS_RECORD, "]=", this);

        for (int i = 0; i < methods.length(); i++) {
            createConstructor(methods.get(i));
        }

        applyClassBody(prototype);

        //init static fields
        //Script.code("console.info('Creating static fields for '+", getName(), "+'...')");
        for (int i = 0; i < fields.length(); i++) {
            FieldRecord fr = fields.get(i);
            if ((fr.getModificators() & Modifier.STATIC) == 0) {
                //Script.code("console.info('Field '+", getName(), "+'=>'+", fr.getName(), "+' is NON static')");
                continue;
            }
            //Script.code("console.info('Field '+", getName(), "+'=>'+", fr.getName(), "+' is static! init it!')");
            Object tempProto = prototype;
            Script.code(tempProto, "[", fr.getJsName(), "]=eval(",fr.getInitValue(),")");
        }

        for (int i = 0; i < statics.length(); i++) {
            Script.code(statics.get(i), "()");
        }

        return prototype;
    }

    protected void applyClassBody(Object obj) {
        if (obj == null && Script.isUndefined(obj))
            Script.code("throw new Error('Object is NULL')");
        for (int i = 0; i < fields.length(); i++) {
            FieldRecord fr = fields.get(i);

            if ((fr.getModificators() & Modifier.STATIC) == 0)
                continue;
            Script.code(obj, ".prototype[", fr.getJsName(), "]=null");
        }

        for (int i = 0; i < methods.length(); i++) {
            MethodRecord mr = methods.get(i);

            if (mr.isStaticFlag()) {
                if (!Script.hasOwnProperty(obj, mr.getJsName()))
                    Script.code(obj, "[", mr.getJsName(), "]=", mr.getBody());
            } else {
                if (!Script.hasOwnProperty(Script.code(obj, ".prototype"), mr.getJsName()))
                    Script.code(obj, ".prototype[", mr.getJsName(), "]=", mr.getBody());

                if (mr.getName().equals("toString") && mr.getArguments().length() == 0) {
                    Script.code(obj, ".prototype.toString=", mr.getBody());
                }
            }
        }

        if (superClass != null) {
            superClass.getType().applyClassBody(obj);
        }

        for (int i = 0; i < imps.length(); i++) {
            imps.get(i).getType().applyClassBody(obj);
        }
    }

    public Class getAsClass() {
        if (clazz == null || Script.isUndefined(clazz)) {
            //Script.code("console.info('init class ' + ",name,")");
            clazz = new TClass(this);
        }
        return CastUtil.cast(clazz);
    }

    @CodeGenerator(GenArrayClassCreateMethod.class)
    private ClassRecord createArrayClassRecord() {
        throw new RuntimeException("Not supported");
    }

    public ClassRecord getArrayClassRecord() {
        if (arrayClassRecord != null)
            return arrayClassRecord;
        arrayClassRecord = createArrayClassRecord();
        if (arrayClassRecord == null || Script.isUndefined(arrayClassRecord))
            Script.code("throw new Error('Array ClassRecord not created!')");
        return arrayClassRecord;
    }
}
