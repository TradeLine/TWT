package org.tlsys.twt.events;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Ajax {
    private final String url;
    private Method method;
    private Auth auth;
    private Object request;
    private String data;
    private boolean sync;

    private Ajax(String url) {
        this.url = url;
        request = Script.code("new XMLHttpRequest()");
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

    public Ajax auth(String user, String password) {
        this.auth = new Auth(user, password);
        return this;
    }

    public Auth auth() {
        return auth;
    }

    public Ajax removeAuth() {
        auth = null;
        return this;
    }

    public Ajax header(String name, String value) {
        Script.code(request,".setRequestHeader(",name,",",value,")");
        return this;
    }

    public Ajax data(String data) {
        this.data = data;
        return this;
    }

    public String data() {
        return data;
    }

    public Ajax get() {
        return method(Method.GET);
    }


    public Result sync() {


        String m = method.name();

        if (auth == null)
            Script.code(request, ".open(",m,", ",url,", false)");
        else
            Script.code(request, ".open(",m,", ",url,", false, ",auth.getUser(),",",auth.getPassword(),")");

        if (data == null)
            Script.code(request,".send()");
        else
            Script.code(request,".send(",data,")");
        return new Result(Script.code(request,".status"), Script.code(request,".statusText"),Script.code(request,".responseText"), request);
    }

    public static class Auth {
        private final String user;
        private final String password;

        public Auth(String user, String password) {
            this.user = user;
            this.password = password;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class Result {
        private final int status;
        private final String statusText;
        private final String responseText;
        private final Object request;

        public Result(int status, String statusText, String responseText, Object request) {
            this.status = status;
            this.statusText = statusText;
            this.responseText = responseText;
            this.request = request;
        }

        public int getStatus() {
            return status;
        }

        public String getStatusText() {
            return statusText;
        }

        public String getResponseText() {
            return responseText;
        }

        public String header(String name) {
            return Script.code(request, ".getResponseHeader(",name,")");
        }
    }

    public interface DoneListener {
        public void done();
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
