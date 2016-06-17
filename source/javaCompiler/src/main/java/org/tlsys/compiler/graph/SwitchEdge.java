package org.tlsys.compiler.graph;

import org.tlsys.compiler.ast.NumberLiteral;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class SwitchEdge extends Edge
{
    public List<NumberLiteral> expressions= new ArrayList<>();

    SwitchEdge(Graph graph, Node theSource, Node theTarget)
    {
        super(graph, theSource, theTarget);
    }
}
