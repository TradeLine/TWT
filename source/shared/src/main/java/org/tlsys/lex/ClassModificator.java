package org.tlsys.lex;

import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VField;
import org.tlsys.lex.declare.VMethod;

import java.io.Serializable;
import java.util.List;

/**
 * Интерфейс модификатор класса.
 * Предобработчик результата для класса, на который "повесили" модификатор
 */
public interface ClassModificator extends Serializable{
    public List<VField> getFields(List<VField> fields);
    public List<VMethod> getMethods(List<VMethod> methods);

    public default void onAdd(VClass clazz){}
    public default void onRemove(VClass clazz){}
}
