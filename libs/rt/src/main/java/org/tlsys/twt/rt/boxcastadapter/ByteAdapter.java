package org.tlsys.twt.rt.boxcastadapter;

/**
 * Created by subochev on 08.04.16.
 */
public class ByteAdapter extends BoxCastAdapter {
    @Override
    protected Class getPrimitiveType() {
        return byte.class;
    }

    @Override
    protected Class getObjectType() {
        return Byte.class;
    }
}
