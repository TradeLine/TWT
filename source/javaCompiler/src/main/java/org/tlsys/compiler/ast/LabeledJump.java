package org.tlsys.compiler.ast;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class LabeledJump extends Jump
{

    String label;

    public LabeledJump(String newLabel)
    {
        super();
        label= newLabel;
    }

    public LabeledJump(Block block)
    {
        super();
        label= block.setLabeled();
    }

    public String getLabel()
    {
        return label;
    }
}
