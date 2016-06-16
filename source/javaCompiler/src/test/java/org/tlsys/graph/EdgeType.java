package org.tlsys.graph;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class EdgeType {
    public static EdgeType FINALLY= new EdgeType("Finally");

    public static EdgeType CATCH= new EdgeType("Catch");

    public static EdgeType TRYBODY= new EdgeType("TryBody");

    private String name;

    private EdgeType(String theName)
    {
        name= theName;
    }

    public String toString()
    {
        return name;
    }
}
