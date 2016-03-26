package org.tlsys;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.tlsys.lex.*;
import org.tlsys.lex.declare.*;

import javax.lang.model.type.NullType;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class TypeUtil {
    private TypeUtil() {
    }

    /*
    public static VField getParentThis(VClass clazz) throws VFieldNotFoundException {
        return ((ParentClassModificator)clazz.getModificator(e->e instanceof ParentClassModificator).orElseThrow(()->new VFieldNotFoundException(clazz, "this$0"))).getParentField();
        / *
        if (!clazz.getDependencyParent().isPresent())
            throw new IllegalStateException("Class " + clazz.getRealName() + " not need parent");
        for (VField f : clazz.fields) {
            if (f.getRealName().equals("this$0"))
                return f;
        }
        throw new VFieldNotFoundException(clazz, "this$0");
        * /
    }

    public static VField createParentThis(VClass clazz) {
        if (clazz.getModificator(e->e instanceof ParentClassModificator).isPresent())
            throw new IllegalStateException("Parent this already added");
        ParentClassModificator pcm = new ParentClassModificator(clazz);
        clazz.addMod(pcm);

        return pcm.getParentField();
        / *

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
        * /
    }
    */

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
            return new NullClass();//loader.loadClass(Object.class.getName());

        try {
            if (type.tsym.owner != null && type.tsym.owner instanceof Symbol.ClassSymbol) {
                String n = loadClass(loader, type.tsym.owner.type).getRealName() + "$" + type.tsym.name.toString();
                System.out.println("Search subclass " + n);
                return loader.loadClass(n);
            }

            if (AnnonimusClass.isAnnonimusClass(type.tsym)) {
                VClass parentClazz = loader.loadClass(AnnonimusClass.extractParentClassName(type.tsym));
                String simpleName = AnnonimusClass.extractSimpleName(type.tsym);
                return (VClass) parentClazz.find(simpleName, e->e instanceof AnnonimusClass).get();
                //System.out.println("IS ANNONUMUS!");
            }

            /*
            if (type.tsym.toString().contains("<anonymous"))
                System.out.println("123");
                */
            return loader.loadClass(type.tsym.toString());
        } catch (VClassNotFoundException e) {
            throw e;
        }
    }

    public static Optional<Context> findParentContext(Context from, Predicate<Context> check) {
        while (from != null) {
            if (check.test(from))
                return Optional.of(from);
            Optional<Context> p = getParent(from);
            if (!p.isPresent())
                return Optional.empty();
            from = p.get();
        }

        return Optional.empty();
    }

    public static Optional<Context> getParent(Context context) {
        Objects.requireNonNull(context);

        if (context instanceof SVar) {
            return Optional.ofNullable(((SVar)context).getParentContext());
        }

        if (context instanceof Switch) {
            return Optional.ofNullable(((Switch)context).getParentContext());
        }

        if (context instanceof Switch.Case) {
            return Optional.ofNullable(((Switch.Case)context).getParent());
        }

        if (context instanceof VIf) {
            return Optional.ofNullable(((VIf)context).getParentContext());
        }

        if (context instanceof ForLoop) {
            return Optional.ofNullable(((ForLoop)context).getParentContext());
        }

        if (context instanceof WhileLoop) {
            return Optional.ofNullable(((WhileLoop)context).getParentContext());
        }

        if (context instanceof VBlock) {
            return Optional.ofNullable(((VBlock)context).getParentContext());
        }

        if (context instanceof VExecute) {
            return Optional.ofNullable(((VExecute)context).getParent());
        }

        if (context instanceof VClass) {
            return Optional.ofNullable(((VClass)context).getParentContext());
        }

        if (context instanceof Try) {
            return Optional.ofNullable(((Try)context).getParentContext());
        }

        if (context instanceof Try.Catch) {
            return Optional.ofNullable(((Try.Catch)context).getParentContext());
        }

        if (context instanceof VPackage) {
            return Optional.ofNullable(((VPackage)context).getParent());
        }

        if (context instanceof Lambda) {
            return Optional.ofNullable(((Lambda)context).getParentContext());
        }

        throw new RuntimeException("Unknown context " + context.getClass().getName());
    }
}
