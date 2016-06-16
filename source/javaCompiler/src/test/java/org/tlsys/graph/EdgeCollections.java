package org.tlsys.graph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class EdgeCollections {

    public static Set<Node> getSources(Collection<Edge> edges)
    {
        Set<Node> sources= new LinkedHashSet<Node>();
        for (Edge edge : edges)
        {
            sources.add(edge.source);
        }
        return sources;
    }

}
