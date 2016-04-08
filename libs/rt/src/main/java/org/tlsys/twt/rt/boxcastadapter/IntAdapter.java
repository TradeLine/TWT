package org.tlsys.twt.rt.boxcastadapter;

public class IntAdapter extends BoxCastAdapter {
    @Override
    protected Class getPrimitiveType() {
        return int.class;
    }

    @Override
    protected Class getObjectType() {
        return Integer.class;
    }

    /*

    private Value fromPrimitive(Value from, VClass to, SourcePoint p) throws CompileException {

        Optional<Value> prim = BoxCastUtil.objectToPrimitive("fromInt", from, to, p);
        if(prim.isPresent())
            return prim.get();

        VClass integerClass = to.getClassLoader().loadClass(Integer.class.getName(), p);

        if (to == integerClass || integerClass.isParent(to))
            return new NewClass(integerClass.getConstructor(p, from.getType()), null).addArg(from);

        prim = BoxCastUtil.primitiveToString(from, to, p);
        if(prim.isPresent())
            return prim.get();

        throw new RuntimeException("Can't cast " + from.getType().getRealName() + " to " + to.getRealName());
    }

    private Value fromObject(Value from, VClass to, SourcePoint p) throws CompileException {
        Optional<Value> o = BoxCastUtil.objectToString(from, to, p);
        if (o.isPresent())
            return o.get();

        if (int.class.getName().equals(to.fullName)) {
            return CodeBuilder.constructor(from.getType()).arg(to).invoke(p).arg(from).build();
        }

        throw new RuntimeException("Can't cast " + from.getType().getRealName() + " to " + to.getRealName());
    }
    */


}
