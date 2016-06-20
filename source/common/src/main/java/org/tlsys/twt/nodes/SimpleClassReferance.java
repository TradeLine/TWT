package org.tlsys.twt.nodes;

public class SimpleClassReferance extends ClassReferance {

    private final String name;

    public SimpleClassReferance(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
