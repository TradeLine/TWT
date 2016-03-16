package org.tlsys;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.tlsys.lex.declare.*;

import javax.lang.model.type.NullType;
import java.lang.reflect.Modifier;
import java.util.Objects;

public final class TypeUtil {
    private TypeUtil() {
    }

    public static VField getParentThis(VClass clazz) throws VFieldNotFoundException {
        if (!clazz.getDependencyParent().isPresent())
            throw new IllegalStateException("Class " + clazz.getRealName() + " not need parent");
        for (VField f : clazz.fields) {
            if (f.getRealName().equals("this$0"))
                return f;
        }
        throw new VFieldNotFoundException(clazz, "this$0");
    }

    public static VField createParentThis(VClass clazz) {
        if (!clazz.getDependencyParent().isPresent())
            throw new IllegalStateException("Class " + clazz.getRealName() + " not need parent");
        try {
            getParentThis(clazz);
            throw new IllegalStateException("Parent this already added");
        } catch (VFieldNotFoundException e) {
        }
        VField f = new VField("this$0", clazz.getParent(), Modifier.PRIVATE, clazz);
        clazz.fields.add(f);
        return f;
    }

    public static VClass loadClass(VClassLoader loader, Type type) throws VClassNotFoundException {
        Objects.requireNonNull(type, "Type is NULL");
        if (type instanceof Type.TypeVar)
            return loadClass(loader, ((Type.TypeVar) type).bound);


        if (type instanceof Type.ArrayType) {
            Type.ArrayType tt = (Type.ArrayType) type;
            return loadClass(loader, tt.elemtype).getArrayClass();
        }

        if (type instanceof Type.CapturedType) {
            return loadClass(loader, ((Type.CapturedType) type).bound);
        }

        if (type instanceof NullType)
            return loader.loadClass(Object.class.getName());

        try {
            if (type.tsym.owner != null && type.tsym.owner instanceof Symbol.ClassSymbol) {
                String n = loadClass(loader, type.tsym.owner.type).getRealName() + "$" + type.tsym.name.toString();
                System.out.println("Search subclass " + n);
                return loader.loadClass(n);
            }
            return loader.loadClass(type.tsym.toString());
        } catch (VClassNotFoundException e) {
            throw e;
        }
    }
}
