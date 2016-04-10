package org.tlsys;

public class JavaFile {
    private final String name;
    private final String data;

    public JavaFile(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }
}
