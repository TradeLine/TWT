package org.tlsys;

import org.tlsys.lex.Operation;
import org.tlsys.lex.declare.VBlock;

import java.io.Serializable;
import java.util.List;

/**
 * Класс модификатор в блоке кода
 */
public interface BlockModificator extends Serializable {
    default void onAdd(VBlock block){}
    default void onRemove(VBlock block){}
    List<Operation> getOperations(List<Operation> operations);
}
