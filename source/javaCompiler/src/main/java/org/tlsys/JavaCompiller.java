package org.tlsys;

import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import org.tlsys.lex.members.TClassLoader;
import org.tlsys.lex.members.VClass;
import org.tlsys.lex.members.VMember;

import java.util.Optional;

public final class JavaCompiller {
    private JavaCompiller() {
    }

    public static VClass findClass(Type type, VMember from) {
        TClassLoader cl = getClassFor(from).get().getClassLoader();

        if (type instanceof VoidType)
            return cl.findClassByName("void").get();

        throw new RuntimeException("Not supported yet");
    }

    public static Optional<VClass> getClassFor(VMember member) {
        while (member != null) {
            if (member instanceof VClass)
                return Optional.of((VClass) member);
            member = member.getParent();
        }
        return Optional.empty();
    }
}
