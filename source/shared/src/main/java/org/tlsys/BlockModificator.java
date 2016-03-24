package org.tlsys;

import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.VBlock;

import java.util.List;

/**
 * Класс модификатор в блоке кода
 */
public interface BlockModificator {
    public default void onAdd(VBlock block){}
    public default void onRemove(VBlock block){}
    public List<Operation> getOperations(List<Operation> operations);
}
