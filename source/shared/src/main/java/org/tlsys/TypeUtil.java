package org.tlsys;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassLoader;
import org.tlsys.lex.declare.VClassNotFoundException;

import javax.lang.model.type.NullType;
import java.util.Objects;

public final class TypeUtil {
    private TypeUtil() {
    }

    public static VClass loadClass(VClassLoader loader, Type type) throws VClassNotFoundException {
        Objects.requireNonNull(type, "Type is NULL");
        if (type instanceof Type.TypeVar)
            return loadClass(loader, ((Type.TypeVar) type).bound);


        if (type instanceof Type.ArrayType) {
            Type.ArrayType tt = (Type.ArrayType)type;
            return loadClass(loader,tt.elemtype).getArrayClass();
        }

        if (type instanceof Type.CapturedType) {
            return loadClass(loader, ((Type.CapturedType) type).bound);
        }

        if (type instanceof NullType)
            return loader.loadClass(Object.class.getName());

        try {
            if (type.tsym.owner != null && type.tsym.owner instanceof Symbol.ClassSymbol) {
                return loader.loadClass(loadClass(loader, type.tsym.owner.type).fullName + "." + type.tsym.name.toString());
            }
            return loader.loadClass(type.tsym.toString());
        } catch (VClassNotFoundException e) {
            throw e;
        }
    }
}
