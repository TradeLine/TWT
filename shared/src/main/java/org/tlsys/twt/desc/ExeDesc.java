package org.tlsys.twt.desc;

public class ExeDesc extends MemberDesc {
    private ArgumentDesc[] arguments;
    private String body;


    public ExeDesc(String jsName, boolean staticFlag, ArgumentDesc[] arguments, String body) {
        super(jsName, staticFlag);
        this.arguments = arguments;
        this.body = body;
    }

    public ExeDesc() {
    }

    public String getBody() {
        return body;
    }

    public ArgumentDesc[] getArguments() {
        return arguments;
    }


}
