package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;
import org.tlsys.twt.nodes.SimpleClassReferance;

/**
 * Created by Субочев Антон on 20.06.2016.
 */
public class StringNode extends ConstNode {

    private final String value;

    public StringNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public ClassReferance getResultType() {
        return new SimpleClassReferance(String.class.getName().replace('.', '/'));
    }
}
