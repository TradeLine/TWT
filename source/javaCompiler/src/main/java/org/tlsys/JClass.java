package org.tlsys;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import org.tlsys.java.lex.JavaField;
import org.tlsys.twt.expressions.AnntationItem;
import org.tlsys.twt.members.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class JClass implements VClass {

    private static final long serialVersionUID = 2551751885402816213L;
    private final VMember parent;
    private final int modifiers;
    private final transient TClassLoader classLoader;
    private final ArrayList<org.tlsys.lex.members.ClassModificator> modificators = new ArrayList<>();
    private final ArrayList<VMember> members = new ArrayList<>();
    private transient TypeDeclaration typeDeclaration;
    private String runtimeName;

    private transient String runtimeSimpleName;
    private ArrayList<AnntationItem> annotationList;

    public JClass(TypeDeclaration typeDeclaration, VMember parent, TClassLoader classLoader) {
        this.typeDeclaration = typeDeclaration;
        this.parent = parent;
        this.classLoader = classLoader;
        parent.add(this);
        modifiers = typeDeclaration.getModifiers();
    }

    public void setRuntimeName(String runtimeName) {
        this.runtimeName = runtimeName;
        runtimeSimpleName = null;
    }

    @Override
    public String getSimpleRealTimeName() {
        if (runtimeSimpleName != null)
            return runtimeSimpleName;
        int p = getRealTimeName().lastIndexOf(".");
        int p1 = getRealTimeName().lastIndexOf("$");

        if (p != -1 && (p > p1 || p1 == -1))
            return getRealTimeName().substring(p + 1);

        if (p1 != -1 && (p1 > p || p == -1))
            return getRealTimeName().substring(p1 + 1);

        return getRealTimeName();
    }

    @Override
    public String getRealTimeName() {
        if (runtimeName == null)
            return getName();
        return runtimeName;
    }

    public TypeDeclaration getTypeDeclaration() {
        return typeDeclaration;
    }

    public void addModificator(org.tlsys.lex.members.ClassModificator mod) {
        modificators.add(mod);
    }

    @Override
    public Optional<VMethod> findMethod(String name, MehtodSearchRequest request) {

        for (BodyDeclaration bd : typeDeclaration.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                if (!md.getName().equals(name))
                    continue;
                if (md.getParameters().isEmpty() && !md.getParameters().isEmpty())
                    continue;

                VMethod jm = new JavaMethod(md, this);

                for (org.tlsys.lex.members.ClassModificator m : modificators) {
                    jm = m.onAddMethod(jm);
                }

                members.add(jm);
                typeDeclaration.getMembers().remove(md);
                return Optional.of(jm);
            }
        }

        for (VMember me : members) {
            if (me instanceof VMethod) {
                VMethod m = (VMethod) me;
                if (!m.getName().equals(name))
                    continue;
            }
        }
        return null;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public TClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public boolean add(VMember member) {
        return false;
    }

    @Override
    public boolean remove(VMember member) {
        return false;
    }

    @Override
    public <T extends VMember> Optional<T> getChild(Predicate<VMember> predicate) {
        return null;
    }

    @Override
    public VMember getParent() {
        return parent;
    }

    @Override
    public Optional<TField> getField(String name) {
        if (typeDeclaration != null)
            for (BodyDeclaration bd : typeDeclaration.getMembers()) {
                if (bd instanceof FieldDeclaration) {
                    FieldDeclaration fs = (FieldDeclaration) bd;
                    for (VariableDeclarator vd : fs.getVariables()) {
                        if (vd.getId().getName().equals(name)) {
                            VClass result = JavaCompiller.findClass(fs.getType(), this);
                            JavaField jf = new JavaField(vd, fs.getModifiers(), result, this);
                            members.add(jf);
                            fs.getVariables().remove(vd);
                            if (fs.getVariables().isEmpty()) {
                                typeDeclaration.getMembers().remove(fs);
                            }
                            return Optional.of(jf);
                        }
                    }
                }
            }
        for (VMember me : members) {
            if (me instanceof TField) {
                TField f = (TField) me;
                if (f.getName().equals(name))
                    return Optional.of(f);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public List<AnntationItem> getList() {
        if (annotationList != null)
            return annotationList;
        annotationList = new ArrayList<>();
        for (AnnotationExpr ae : getTypeDeclaration().getAnnotations()) {
        }
        return annotationList;
    }
}
