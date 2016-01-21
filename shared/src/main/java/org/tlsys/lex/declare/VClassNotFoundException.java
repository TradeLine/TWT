package org.tlsys.lex.declare;

public class VClassNotFoundException extends Exception {
    private String name;

    public VClassNotFoundException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return name;
    }
}
