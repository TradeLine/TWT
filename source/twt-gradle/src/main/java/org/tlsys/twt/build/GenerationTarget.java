package org.tlsys.twt.build;

import java.util.ArrayList;
import java.util.Arrays;

public class GenerationTarget {
    private ArrayList<String> classes = new ArrayList<>();
    private String mainClass;
    private String fileName;
    private String generator;

    public String generator() {
        return generator;
    }

    public GenerationTarget generator(String generator) {
        this.generator = generator;
        return this;
    }

    public GenerationTarget addClass(String ... classList) {
        classes.addAll(Arrays.asList(classList));
        return this;
    }

    public String main() {
        return mainClass;
    }

    public GenerationTarget main(String mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    public String out() {
        return fileName;
    }

    public GenerationTarget out(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ArrayList<String> getClasses() {
        return classes;
    }
}
