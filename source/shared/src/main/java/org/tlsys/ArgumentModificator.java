package org.tlsys;

import org.tlsys.lex.declare.VArgument;

import java.io.Serializable;
import java.util.List;

/**
 * Класс модификатор для аргументов методов
 */
public interface ArgumentModificator extends Serializable {
    public List<VArgument> getArguments(List<VArgument> arguments);
}
