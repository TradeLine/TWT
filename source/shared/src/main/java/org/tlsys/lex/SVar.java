package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class SVar extends Value {

    private static final long serialVersionUID = 5407405178456004940L;
    protected String runtimeName;
    protected String realName;
    protected String aliasName;
    private VClass clazz;

    public String getRuntimeName() {
        if (runtimeName == null)
            return getAliasName();
        return runtimeName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRuntimeName(String runtimeName) {
        this.runtimeName = runtimeName;
    }

    public String getAliasName() {
        if (aliasName == null)
            return getRealName();
        return aliasName;
    }

    public SVar(String realName, VClass clazz) {
        this(realName, realName, clazz);
    }

    public SVar(String realName, String alias, VClass clazz) {
        this.clazz = Objects.requireNonNull(clazz);
    }

    @Override
    public void getUsing(Collect c) {
        c.add(clazz);
    }

    @Override
    public VClass getType() {
        return clazz;
    }

    @Override
    public Optional<SVar> find(String name, Predicate<Context> searchIn) {
        if (name.equals(realName) || name.equals(aliasName))
            return Optional.of(this);
        return Optional.empty();
    }
}
