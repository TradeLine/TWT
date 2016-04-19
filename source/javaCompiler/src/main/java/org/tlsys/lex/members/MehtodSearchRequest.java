package org.tlsys.lex.members;

import org.tlsys.lex.TArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class MehtodSearchRequest {
    private final VMember member;
    private final List<Predicate<TArgument>> list = new ArrayList<>();

    private MehtodSearchRequest(VMember member) {
        this.member = member;
    }

    public static MehtodSearchRequest of(VMember member) {
        return new MehtodSearchRequest(member);
    }

    public VMember getScopeMember(VMember member) {
        return member;
    }

    public List<Predicate<TArgument>> getPredicatlist() {
        return list;
    }

    public MehtodSearchRequest add(Predicate<TArgument> predicate) {
        list.add(predicate);
        return this;
    }
}
