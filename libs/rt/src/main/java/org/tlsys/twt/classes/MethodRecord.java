package org.tlsys.twt.classes;

import org.tlsys.twt.JArray;
import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class MethodRecord {
    private final JArray<ArgumentRecord> arguments = new JArray<>();
    private final JArray<AnnotationProvider> annotations = new JArray<>();
    private String jsName;
    private String name;
    private Object body;
    private boolean staticFlag;


    public MethodRecord(String jsName, String name, Object body, boolean staticFlag) {
        this.body = body;
        this.jsName = jsName;
        this.name = name;
        this.staticFlag = staticFlag;
    }

    public MethodRecord addAnnotation(AnnotationProvider annotationProvider) {
        annotations.add(annotationProvider);
        return this;
    }

    public JArray<AnnotationProvider> getAnnotations() {
        return annotations;
    }

    public JArray<ArgumentRecord> getArguments() {
        return arguments;
    }

    public String getJsName() {
        return jsName;
    }

    public String getName() {
        if (Script.isUndefined(name))
            return null;
        return name;
    }

    public Object getBody() {
        if (Script.isUndefined(body))
            return null;
        return body;
    }

    public boolean isStaticFlag() {
        return staticFlag;
    }

    public MethodRecord addArg(ArgumentRecord ar) {
        arguments.add(ar);
        return this;
    }
}
