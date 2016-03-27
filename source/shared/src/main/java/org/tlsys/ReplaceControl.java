package org.tlsys;

import org.tlsys.lex.Operation;

/**
 * Класс, управляющий заменой узлов AST дерева.
 */
public class ReplaceControl {
    private final Operation operation;

    private Operation newOperation;

    public ReplaceControl(Operation operation) {
        this.operation = operation;
    }

    public void set(Operation newOperation) {
        this.newOperation = newOperation;
    }

    public boolean isNew() {
        return newOperation != null;
    }

    public Operation get() {
        if (isNew())
            return newOperation;
        return operation;
    }
}
