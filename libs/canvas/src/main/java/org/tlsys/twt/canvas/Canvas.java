package org.tlsys.twt.canvas;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.DomNode;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@DomNode("canvas")
public class Canvas {
    private CanvasRenderingContext2D ctx2d = null;

    public CanvasRenderingContext2D get2D() {
        if (ctx2d != null)
            return ctx2d;

        ctx2d = new CanvasRenderingContext2D(this, Script.code(this,".getContext('2d')"));
        return ctx2d;
    }
}
