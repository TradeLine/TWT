package org.tlsys;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VMember;

import java.util.Optional;

public class JavaClass extends JClass {
    private final String name;
    private transient JavaSourceSet compiler;

    public JavaClass(ClassOrInterfaceDeclaration declaration, VMember parent, JavaSourceSet compiler) {
        super(declaration, parent);
        this.name = declaration.getName();
        this.compiler = compiler;
    }

    @Override
    public Optional<VClass> getClass(String name) {
        return null;
    }
}
