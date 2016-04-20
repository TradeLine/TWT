package org.tlsys;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.tlsys.lex.members.TClassLoader;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VPackage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;

public class JavaSourceSet {


    private final HashMap<CompilationUnit, JavaFile> compiled = new HashMap<>();
    private final TClassLoader classLoader;
    private final FileProvider fileProvider;
    private final HashMap<String, CompilationUnit> files = new HashMap<>();
    private final HashMap<String, VClass> classes = new HashMap<>();
    private final VPackage rootPackage = new VPackage(null, null);
    private final HashMap<String, String> alias = new HashMap<>();

    public JavaSourceSet(TClassLoader classLoader, FileProvider fileProvider) {
        this.fileProvider = fileProvider;
        this.classLoader = classLoader;
    }

    public void addAlias(String alias, String realName) {
        this.alias.put(alias, realName);
    }

    private Optional<CompilationUnit> getFile(String name) {
        CompilationUnit c = files.get(name);
        if (c != null)
            return Optional.of(c);

        try (InputStream is = fileProvider.getFile(name).orElseThrow(() -> new RuntimeException("File " + name + " not found"))) {
            CompilationUnit cu = JavaParser.parse(is);
            files.put(name, cu);
            return Optional.of(cu);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<VClass> getClass(String name) {
        String ss = alias.get(name);
        if (ss != null)
            return getClass(ss);

        VClass cl = classes.get(name);
        if (cl != null)
            return Optional.of(cl);

        if (name.contains("$")) {
            cl = getClass(name.substring(0, name.indexOf("$"))).get();
            throw new RuntimeException("Not supported yet");
        }

        CompilationUnit cu = getFile(name.replace('.', '/') + ".java").get();

        for (TypeDeclaration td : cu.getTypes()) {
            if (td instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)td;
                String fullName = cu.getPackage().getName().toString();
                if (!fullName.isEmpty())
                    fullName += ".";
                fullName += cd.getName();

                if (fullName.equals(name)) {

                    if (cu.getPackage().getName().getName().isEmpty()) {
                        cl = new JavaClass(cd, rootPackage, classLoader);
                        classLoader.addClass(cl);
                        classes.put(name, cl);
                        return Optional.of(cl);
                    } else {
                        String[] list = cu.getPackage().getName().toString().split("\\.");
                        VPackage p = rootPackage;
                        for (String s : list) {
                            Optional<VPackage> op = p.getChild(e->{
                                if (e instanceof VPackage) {
                                    VPackage pp = (VPackage)e;
                                    if (pp.getName().equals(s)) {
                                        return true;
                                    }
                                }
                                return false;
                            });
                            if (op.isPresent())
                                p = op.get();
                            else {
                                VPackage ppp = new VPackage(s, p);
                                p.add(ppp);
                                p = ppp;
                            }
                        }
                        cl = new JavaClass(cd, p, classLoader);
                        classLoader.addClass(cl);
                        classes.put(name, cl);
                        return Optional.of(cl);
                    }
                }
            }
        }
        return null;
    }
}
