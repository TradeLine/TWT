package org.tlsys.twt;

import org.tlsys.twt.annotations.JSClass;

import java.util.Optional;

@JSClass
public final class Storage {

    public static Storage LOCAL = new Storage(Script.code("localStorage"));
    public static Storage SESSION = new Storage(Script.code("sessionStorage"));
    private final Object js;

    private Storage(Object js) {
        this.js = js;
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(Script.code(js, ".getItem(", key, ")"));
    }

    public Optional<String> get(int index) {
        Optional<String> key = getIndexByKey(index);
        if (key.isPresent())
            return get(key.get());
        return Optional.empty();
    }

    public Optional<String> remove(String key) {
        Optional<String> r = get(key);
        if (r.isPresent())
            Script.code(js, ".removeItem(", key, ")");
        return r;
    }

    public Optional<String> remove(int index) {
        Optional<String> key = getIndexByKey(index);
        if (key.isPresent())
            return remove(key.get());
        return Optional.empty();
    }

    public Optional<String> getIndexByKey(int index) {
        return Optional.ofNullable(Script.code(js, ".key(", CastUtil.toObject(index), ")"));
    }

    public int size() {
        return CastUtil.toInt(Script.code(js, ".length"));
    }

    public Storage set(String key, String value) {
        Script.code(js, ".setItem(", key, ",", value, ")");
        return this;
    }

    public Storage set(int index, String value) {
        Optional<String> key = getIndexByKey(index);
        if (key.isPresent())
            return set(key.get(), value);
        return this;
    }

    public Storage clear() {
        Script.code(js, ".clear()");
        return this;
    }

}
