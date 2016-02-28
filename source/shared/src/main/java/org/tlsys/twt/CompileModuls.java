package org.tlsys.twt;

import org.tlsys.lex.CanUse;
import org.tlsys.lex.Collect;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.lex.declare.VMethod;

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

        public boolean isExist(VExecute e) {
            return exe.contains(e);
        }
    }

    public Collection<ClassRecord> getRecords() {
        return classes.values();
    }

    public boolean isExist(VClass clazz) {
        return classes.containsKey(clazz);
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
            System.out.println("adding class " + clazz.realName);
            classes.put(clazz, cr);
            Collect c = Collect.create();
            clazz.getUsing(c);
            System.out.println("added " + clazz.realName);
            add(c);
        }

        return cr;
    }

    /**
     * Ищет не используемые методы, которые подменяют используемые
     */
    public void detectReplace() {
        System.out.println("search replace...");
        CLASSES:
        while (true) {
            for (ClassRecord cr : classes.values()) {
                for (VMethod m : cr.getClazz().methods) {
                    if (m.getReplace() == null) {
                        continue;
                    }
                    if (m.getReplace().getParent() == cr.getClazz()) {
                        continue;
                    }
                    ClassRecord cc = classes.get(m.getReplace().getParent());
                    if (cc == null) {
                        continue;
                    }
                    if (cc.isExist(m.getReplace())) {
                        int size = classes.size();
                        add(m);
                        if (classes.size() != size)
                            continue CLASSES;
                    }
                }
                if (m.getReplace().getParent() == cr.getClazz()) {
                    System.out.println("self replace");
                    continue;
                }
                ClassRecord cc = classes.get(m.getReplace().getParent());
                if (cc == null) {
                    System.out.println("parent class not using");
                    continue;
                }
                if (cc.isExist(m.getReplace())) {
                    System.out.println("added");
                    add(m);
                    continue;
                }

                System.out.println("not using");
            }
            return;
        }
    }

    /**
     * Добовляет в компиляцию метод
     *
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
                add((VExecute) cu);
            if (cu instanceof VClass)
                add((VClass) cu);
        }
    }
}
