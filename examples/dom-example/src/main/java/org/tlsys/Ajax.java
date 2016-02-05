package org.tlsys;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Ajax {
    private final String url;
    private Method method;

    private Ajax(String url) {
        this.url = url;
    }

    public static Ajax create(String url) {
        return new Ajax(url);
    }

    public Ajax method(Method method) {
        this.method = method;
        return this;
    }

    public Ajax post() {
        return method(Method.POST);
    }

    public Ajax get() {
        return method(Method.GET);
    }

    public enum Method {
        GET,
        POST;

        public String toMethod() {
            switch (this) {
                case GET:
                    return "GET";
                case POST:
                    return "POST";
                default:
                    throw new RuntimeException("Unknown method");
            }
        }
    }
}
