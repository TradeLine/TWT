package org.tlsys.twt;

import org.tlsys.lex.CanUse;
import org.tlsys.lex.Collect;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Класс, содержащий в себе информацию о нужных для компиляции классах методах
 *
 * @author Субочев Антон
 */
public class CompileModuls {
    private final HashMap<VClass, ClassRecord> classes = new HashMap<>();
    public class ClassRecord {
        private final VClass clazz;
        private final HashSet<VExecute> exe = new HashSet<>();

        public VClass getClazz() {
            return clazz;
        }

        public HashSet<VExecute> getExe() {
            return exe;
        }

        public ClassRecord(VClass clazz) {
            this.clazz = clazz;
        }
    }

    public Collection<ClassRecord> getRecords() {
        return classes.values();
    }

    /**
     * Добовляет в компиляцию класс
     *
     * @param clazz класс
     * @return запись о компилируемых методах класса
     */
    public ClassRecord add(VClass clazz) {
        ClassRecord cr = classes.get(clazz);
        if (cr == null) {
            cr = new ClassRecord(clazz);
            classes.put(clazz, cr);
            Collect c = Collect.create();
            clazz.getUsing(c);
            add(c);
        }

        return cr;
    }

    /**
     * Добовляет в компиляцию метод
     * @param exe метод, который обязательно надо скомпилировать
     * @return запись о компилируемых методах класса
     */
    public ClassRecord add(VExecute exe) {
        ClassRecord cr = add(exe.getParent());
        if (cr.getExe().contains(exe))
            return cr;
        cr.getExe().add(exe);
        Collect c = Collect.create();
        exe.getUsing(c);
        add(c);
        return cr;
    }

    public void add(Collect collect) {
        for (CanUse cu : collect.get()) {
            if (cu instanceof VExecute)
                add((VExecute)cu);
            if (cu instanceof VClass)
                add((VClass)cu);
        }
    }
}
