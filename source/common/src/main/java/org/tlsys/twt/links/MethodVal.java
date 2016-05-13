package org.tlsys.twt.links;

import org.tlsys.twt.ExecuteVal;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public final class MethodVal extends ExecuteVal implements Externalizable {
    private static final long serialVersionUID = 2971698612483610371L;

    private String name;

    public MethodVal(ClassVal classVal, ClassVal[] arguments, String name) {
        super(classVal, arguments);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = in.readUTF();
    }
}
