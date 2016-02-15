package org.tlsys.twt.generate;

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

    public String mainClass() {
        return mainClass;
    }

    public GenerationTarget mainClass(String mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    public String fileName() {
        return fileName;
    }

    public GenerationTarget fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ArrayList<String> getClasses() {
        return classes;
    }
}
