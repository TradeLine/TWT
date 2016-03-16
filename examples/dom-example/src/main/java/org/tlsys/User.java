package org.tlsys;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public class User {
    private String name;
    public String[] list;

    public User(String name) {
        this.name = name;
    }

    public User() {
        this("");
    }

    public String getName() {
        return name;
    }
}
