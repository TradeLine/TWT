package org.tlsys.twt.generate;

import java.util.ArrayList;
import java.util.Arrays;

public class GenerationTarget {
    private ArrayList<String> classes;
    private String mainClass;
    private String fileName;
    private String generator;

    public String generator() {
        return generator;
    }

    public void generator(String generator) {
        this.generator = generator;
    }

    public void addClass(String ... classList) {
        classes.addAll(Arrays.asList(classList));
    }

    public String mainClass() {
        return mainClass;
    }

    public void mainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String fileName() {
        return fileName;
    }

    public void fileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<String> getClasses() {
        return classes;
    }
}
