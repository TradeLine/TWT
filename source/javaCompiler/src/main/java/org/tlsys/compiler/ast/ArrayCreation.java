package org.tlsys.compiler.ast;

import org.apache.bcel.generic.Type;
import org.tlsys.compiler.Compile;
import org.tlsys.compiler.generators.NodeVisiter;

import java.util.List;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ArrayCreation extends Expression {

    private List<ASTNode> dimensions;

    private ArrayInitializer initializer;

    public ArrayCreation(MethodDeclaration methodDecl, Type theType, List<ASTNode> theDimensions) {
        type = theType;
        dimensions = theDimensions;
        for (ASTNode dimension : dimensions) {
            this.widen(dimension);
        }
        //Compile.getInstance().addReference(methodDecl, this);
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public ArrayInitializer getInitializer() {
        return initializer;
    }

    public void setInitializer(ArrayInitializer theInitializer) {
        initializer = theInitializer;
    }

    public List<ASTNode> getDimensions() {
        return dimensions;
    }

}
