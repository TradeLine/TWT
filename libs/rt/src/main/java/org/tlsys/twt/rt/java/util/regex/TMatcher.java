package org.tlsys.twt.rt.java.util.regex;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;

@JSClass
@ReplaceClass(Matcher.class)
public class TMatcher implements MatchResult {
    private final TPattern pattern;
    private String text;

    public TMatcher(TPattern pattern, String text) {
        this.pattern = pattern;
        this.text = text;
    }


    @Override
    public int start() {
        return 0;
    }

    @Override
    public int start(int group) {
        return 0;
    }

    @Override
    public int end() {
        return 0;
    }

    @Override
    public int end(int group) {
        return 0;
    }

    @Override
    public String group() {
        return null;
    }

    @Override
    public String group(int group) {
        return null;
    }

    @Override
    public int groupCount() {
        return 0;
    }
}
