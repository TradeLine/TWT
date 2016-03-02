package org.tlsys.twt;

import org.tlsys.lex.CanUse;
import org.tlsys.lex.Collect;
import org.tlsys.lex.declare.*;

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
            classes.put(clazz, cr);
            Collect c = Collect.create();
            clazz.getUsing(c);
            add(c);

            for (VMethod m : clazz.methods) {
                if (m.force) {
                    add(m);
                }
            }

            for (VConstructor m : clazz.constructors) {
                if (m.force) {
                    add(m);
                }
            }
        }

        return cr;
    }

    /**
     * Ищет не используемые методы, которые подменяют используемые
     */
    public void detectReplace() {
        CLASSES:
        while (true) {
            for (ClassRecord cr : classes.values()) {
                boolean log = false;
                if (cr.getClazz().realName.equals("org.tlsys.admin.TextTableRender")) {
                    log = true;
                }
                for (VMethod m : cr.getClazz().methods) {
                    if (m.isStatic())
                        continue;
                    if (log)
                        System.out.println("Check method " + m + "...");
                    if (m.getReplace() == null) {
                        if (log)
                            System.out.println("no replace....");
                        continue;
                    }
                    if (m.getReplace().getParent() == cr.getClazz()) {
                        if (log)
                            System.out.println("replace self");
                        continue;
                    }
                    ClassRecord cc = classes.get(m.getReplace().getParent());
                    if (cc == null) {
                        if (log)
                            System.out.println("replaced class not build");
                        continue;
                    }
                    if (cc.isExist(m.getReplace())) {
                        if (log)
                            System.out.println("add for compile!");
                        int size = classes.size();
                        add(m);
                        if (classes.size() != size) {
                            continue CLASSES;
                        }
                    }
                }
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
        if (cr.getExe().contains(exe)) {
            return cr;
        }
        cr.getExe().add(exe);
        Collect c = Collect.create();
        exe.getUsing(c);
        add(c);
        return cr;
    }

    public void add(Collect collect) {
        for (CanUse cu : collect.get()) {
            if (cu instanceof VExecute) {
                add((VExecute) cu);
            }
            if (cu instanceof VClass) {
                add((VClass) cu);
            }
        }
    }

    public void addForced(VClassLoader loader) {
        for (VClass cl : loader.classes) {
            if (!cl.force) {
                continue;
            }
            add(cl);
        }

        for (VClassLoader p : loader.parents) {
            addForced(p);
        }
    }
}
