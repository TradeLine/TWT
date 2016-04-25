package org.tlsys.twt.members;

import org.tlsys.twt.expressions.AnntationItem;

import java.util.List;
import java.util.Optional;

public interface Annotated {
    public List<AnntationItem> getList();

    public default Optional<AnntationItem> getByClass(VClass clazz) {
        for (AnntationItem ai : getList()) {
            if (ai.getType() == clazz)
                return Optional.of(ai);
        }
        return Optional.empty();
    }
}
