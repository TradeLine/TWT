package org.tlsys.lex.declare;

import org.tlsys.twt.CompileException;

public class VFieldNotFoundException extends CompileException {
    private VClass clazz;
    private String fieldName;
    public VFieldNotFoundException(VClass clazz, String fieldName) {
        this.clazz = clazz;
        this.fieldName = fieldName;
    }

    public VClass getClazz() {
        return clazz;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getMessage() {
        return getClazz() + "::" + getFieldName();
    }
}
