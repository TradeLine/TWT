package org.tlsys;

import org.tlsys.lex.Operation;

import java.util.Optional;

public class ReplaceHelper {
    public static <T extends Operation> Optional<T> replace(T op, ReplaceVisiter replaceVisiter) {
        if (op == null)
            return Optional.empty();
        ReplaceControl rc = new ReplaceControl(op);
        if (replaceVisiter.replace(rc))
            rc.get().visit(replaceVisiter);
        if (rc.isNew())
            return Optional.of((T) rc.get());
        return Optional.empty();
    }
}
