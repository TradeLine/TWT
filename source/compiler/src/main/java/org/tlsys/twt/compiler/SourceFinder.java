package org.tlsys.twt.compiler;

import org.tlsys.twt.annotations.JSClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;


public class SourceFinder {

    public interface SourceProvider {
        public Optional<File> getSourceForClass(String className);
    }

    public static Set<File> getCompileClasses(File file, SourceProvider sourceProvider) throws IOException {
        System.out.println("Scan " + file + "...");
        if (!file.isDirectory())
            return null;
        HashSet<File> out = new HashSet<>();
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                Set<File> aa = getCompileClasses(f, sourceProvider);
                if (aa != null)
                    out.addAll(aa);
                continue;
            }
            if (!f.isFile())
                continue;
            if (!f.getName().endsWith(".class"))
                continue;
            System.out.print("CHECK " + f + "...");
            try (FileInputStream fis = new FileInputStream(f)) {
                ClassReader cr = new ClassReader(fis);
                ClassNode classNode = new ClassNode();
                cr.accept(classNode, 0);

                if (classNode.visibleAnnotations != null)
                    for (Object o : classNode.visibleAnnotations) {
                        AnnotationNode an = (AnnotationNode) o;
                        System.out.println("@" + an.desc);
                        if (an.desc.equals("L" + JSClass.class.getName().replace('.', '/') + ";")) {
                            if (classNode.name.contains("$")) {
                                String cn = classNode.name.substring(0, classNode.name.indexOf("$")).replace('/', '.');
                                sourceProvider.getSourceForClass(cn).ifPresent(e->out.add(e));
                            } else {
                                if (sourceProvider.getSourceForClass(classNode.name.replace('/', '.')).isPresent()) {
                                    out.add(sourceProvider.getSourceForClass(classNode.name.replace('/', '.')).get());
                                    System.out.println("DONE");
                                } else {
                                    System.out.println("NOT FOUND");
                                }
                            }
                        }
                    }
            }
        }

        return out;
    }
}
