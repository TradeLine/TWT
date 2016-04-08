package org.tlsys.twt.rt.boxcastadapter;

public class DoubleAdapter extends BoxCastAdapter {
    @Override
    protected Class getPrimitiveType() {
        return double.class;
    }

    @Override
    protected Class getObjectType() {
        return Double.class;
    }
}
