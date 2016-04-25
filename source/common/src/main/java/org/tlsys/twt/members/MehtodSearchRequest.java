package org.tlsys.twt.members;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class MehtodSearchRequest {
    public static final byte BAD = -1;
    public static final byte EQUAL = 0;
    public static final Predicate<TArgument> UNKNOWN = e -> true;
    public static final Predicate<TArgument> NULL = e -> !e.getType().isPrimitive();
    private final VMember member;
    private final List<ArgumentCheck> list = new ArrayList<>();

    private MehtodSearchRequest(VMember member) {
        this.member = member;
    }

    public static MehtodSearchRequest of(VMember member) {
        return new MehtodSearchRequest(member);
    }

    public static ArgumentCheck PRIMITIVE_LOW(VClass clazz) {
        if (!clazz.isPrimitive())
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not primitive");
        return null;
    }

    public static Predicate<TArgument> LOW(VClass clazz) {
        /*
        if (clazz.isPrimitive()) {

        }
        */
        return null;
    }

    public VMember getScopeMember(VMember member) {
        return member;
    }

    public List<ArgumentCheck> getPredicatlist() {
        return list;
    }

    public MehtodSearchRequest add(ArgumentCheck predicate) {
        list.add(predicate);
        return this;
    }

    public interface ArgumentCheck {
        public byte check(TArgument argument);
    }

}
