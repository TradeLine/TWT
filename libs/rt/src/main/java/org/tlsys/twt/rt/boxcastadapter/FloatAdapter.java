package org.tlsys.twt.rt.boxcastadapter;

public class FloatAdapter extends BoxCastAdapter {
    @Override
    protected Class getPrimitiveType() {
        return float.class;
    }

    @Override
    protected Class getObjectType() {
        return Float.class;
    }
}
