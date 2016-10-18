package org.tlsys;

import org.tlsys.lex.declare.VArgument;
import org.tlsys.lex.declare.VBlock;

import java.io.Serializable;
import java.util.List;

/**
 * Класс модификатор для аргументов методов
 */
public interface ArgumentModificator extends Serializable {
    List<VArgument> getArguments(List<VArgument> arguments);

    void setBody(VBlock oldBody, VBlock newBody);
}
