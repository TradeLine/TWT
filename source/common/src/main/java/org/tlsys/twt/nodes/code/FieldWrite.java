package org.tlsys.twt.nodes.code;

import org.tlsys.twt.FieldReferance;
import org.tlsys.twt.nodes.ClassReferance;

/**
 * Created by Субочев Антон on 20.06.2016.
 */
public class FieldWrite extends FieldAccessNode {

    private final FieldReferance fieldReferance;

    public FieldWrite(Value self, FieldReferance fieldReferance) {
        super(self);
        this.fieldReferance = fieldReferance;
    }

    @Override
    public ClassReferance getResultType() {
        throw new RuntimeException("Not supported yet");
    }

}
