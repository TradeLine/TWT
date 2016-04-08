package org.tlsys.twt.rt.boxcastadapter;

public class LongAdapter extends BoxCastAdapter {
    @Override
    protected Class getPrimitiveType() {
        return long.class;
    }

    @Override
    protected Class getObjectType() {
        return Long.class;
    }
}
