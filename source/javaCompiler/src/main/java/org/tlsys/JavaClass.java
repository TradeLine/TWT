package org.tlsys;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.members.TClassLoader;
import org.tlsys.twt.members.VMember;

public class JavaClass extends JClass {

    private static final long serialVersionUID = 6278642675602796968L;
    private final String name;
    private ClassVal superClass;

    public JavaClass(ClassOrInterfaceDeclaration declaration, VMember parent, TClassLoader classLoader) {
        super(declaration, parent, classLoader);
        this.name = declaration.getName();
    }

    @Override
    public ClassOrInterfaceDeclaration getTypeDeclaration() {
        return (ClassOrInterfaceDeclaration) super.getTypeDeclaration();
    }

    @Override
    public String getSimpleName() {
        return name;
    }

    @Override
    public ClassVal getSuperClass() {
        if (superClass != null)
            return superClass;

        if (getTypeDeclaration().isInterface()) {
            superClass = getClassLoader().findClassByName(Object.class.getName()).get().asRef();
            return superClass;
        }

        if (getTypeDeclaration().getExtends().isEmpty()) {
            superClass = getClassLoader().findClassByName(Object.class.getName()).get().asRef();
            return superClass;
        }

        superClass = JavaCompiller.findClass(getTypeDeclaration().getExtends().get(0), this);
        return superClass;
    }

}
