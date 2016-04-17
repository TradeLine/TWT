package org.tlsys.lex;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Predicate;

public interface TNode extends Serializable {
    public TNode getParent();

    public default Optional<TNode> searchUp(Predicate<TNode> predicate) {
        if (getParent() == null)
            return Optional.empty();
        return getParent().searchUp(predicate);
    }
}
