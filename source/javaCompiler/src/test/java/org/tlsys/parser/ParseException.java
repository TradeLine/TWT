package org.tlsys.parser;

import org.tlsys.ast.ASTNode;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ParseException extends RuntimeException
{

    private ASTNode astNode;

    public ParseException(String msg, ASTNode node)
    {
        super(msg);
        astNode= node;
    }

    public ParseException(Throwable cause, ASTNode node)
    {
        super(cause);
        astNode= node;
    }

    public ASTNode getAstNode()
    {
        return astNode;
    }
}
