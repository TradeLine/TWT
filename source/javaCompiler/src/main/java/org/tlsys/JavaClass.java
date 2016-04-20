package org.tlsys;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.tlsys.lex.members.TClassLoader;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VMember;

import java.util.Optional;

public class JavaClass extends JClass {

    private static final long serialVersionUID = 6278642675602796968L;
    private final String name;

    public JavaClass(ClassOrInterfaceDeclaration declaration, VMember parent, TClassLoader classLoader) {
        super(declaration, parent, classLoader);
        this.name = declaration.getName();
    }

    @Override
    public String getSimpleName() {
        return name;
    }

    @Override
    public Optional<VClass> getClass(String name) {
        return null;
    }

}
