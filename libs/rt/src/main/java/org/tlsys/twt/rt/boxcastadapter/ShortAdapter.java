package org.tlsys.twt.rt.boxcastadapter;

public class ShortAdapter extends BoxCastAdapter {
    @Override
    protected Class getPrimitiveType() {
        return short.class;
    }

    @Override
    protected Class getObjectType() {
        return Short.class;
    }
}
