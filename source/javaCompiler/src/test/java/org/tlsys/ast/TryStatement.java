package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;
import org.tlsys.graph.*;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class TryStatement extends Block {

    public TryHeaderNode header;

    public TryStatement() {
        super();
    }

    public void addCatchStatement(CatchClause catchStatement) {
        if (getChildCount() < 2)
            throw new RuntimeException("Illegal DOM state");
        ((Block) getChildAt(1)).appendChild(catchStatement);
    }

    public Block getCatchStatements() {
        return (Block) getChildAt(1);
    }

    public Block getFinallyBlock() {
        if (getChildCount() < 3)
            return null;
        return (Block) getChildAt(2);
    }

    public void setFinallyBlock(Block finallyBlock) {
        setChildAt(2, finallyBlock);
    }

    public Block getTryBlock() {
        return (Block) getChildAt(0);
    }

    public void setTryBlock(Block tryBlock) {
        setChildAt(0, tryBlock);
        setChildAt(1, new Block());
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public String toString() {
        return super.toString();
    }

}
