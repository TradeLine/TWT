package org.tlsys.compiler.graph.transformation;

import org.tlsys.compiler.ast.*;
import org.tlsys.compiler.graph.*;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class Loop extends Transformation
{

    private Set selfEdges;

    public Loop()
    {
        selfEdges= null;
    }

    public boolean applies_()
    {
        return header.hasSelfEdges();
    }

    public void apply_()
    {
        selfEdges= graph.removeSelfEdges(header);
    }

    void rollOut_(Block block)
    {
        WhileStatement loopStmt= new WhileStatement();
        Block loopBody= new Block();
        loopStmt.setBlock(loopBody);
        loopStmt.setExpression(new BooleanLiteral(true));

        block.appendChild(loopStmt);

        Iterator iter= selfEdges.iterator();
        while (iter.hasNext())
        {
            Edge edge= (Edge) iter.next();
            if (!edge.isGlobal())
                continue;
            loopStmt.isLabeled();
            produceJump(edge, loopStmt);
        }

        graph.rollOut(header, loopBody);
    }

    public String toString()
    {
        return super.toString() + "(" + header + ")";
    }
}
