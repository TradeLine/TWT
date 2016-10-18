package org.tlsys;

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
    List<VField> getFields(List<VField> fields);
    List<VMethod> getMethods(List<VMethod> methods);

    default void onAdd(VClass clazz){}
    default void onRemove(VClass clazz){}
}
