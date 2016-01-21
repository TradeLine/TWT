package org.tlsys.twt.desc;

public class TypeDesc {
    private String type;
    private int array;

    public TypeDesc(Class clazz) {
        Class cur = clazz;
        array = 0;

        while(cur != null && cur.isArray()) {
            cur = cur.getComponentType();
            array++;
        }

        type = cur.getName();
    }

    public TypeDesc(String type, int array) {
        this.type = type;
        this.array = array;
    }

    public TypeDesc() {
    }

    public String getType() {
        return type;
    }

    public int getArray() {
        return array;
    }
}
