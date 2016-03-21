package org.tlsys;

import org.tlsys.gl.CanvasRenderingContext2D;
import org.tlsys.gl.WebGLRenderingContext;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.DomNode;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@DomNode("canvas")
public class Canvas {

    private CanvasRenderingContext2D d2;

    public CanvasRenderingContext2D get2D() {
        if (d2 != null)
            return d2;
        d2 = new CanvasRenderingContext2D(Script.code(this, "getContext('2d')"));
        return d2;
    }

    private WebGLRenderingContext d3;

    public WebGLRenderingContext getWebGL() {
        if (d3!= null)
            return d3;
        d3 = new WebGLRenderingContext(Script.code(this, ".getContext('webgl') || ",this,".getContext('experimental-webgl')"));
        return d3;
    }
}
