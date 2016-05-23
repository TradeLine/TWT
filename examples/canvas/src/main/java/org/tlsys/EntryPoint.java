package org.tlsys;

import org.tlsys.twt.Console;
import org.tlsys.twt.JArray;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ForceInject;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.canvas.Canvas;
import org.tlsys.twt.canvas.CanvasRenderingContext2D;
import org.tlsys.twt.dom.DOM;
import org.tlsys.twt.dom.Document;

import java.util.Random;

@JSClass
public class EntryPoint {
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    static double time_old = 0;
    static JArray<Unit> units = new JArray<>();
    private static TimeOutClass t = new TimeOutClass();
    private static CanvasRenderingContext2D st;

    public static void main() {
        Object body = Script.code(Document.get(), ".body");
        Canvas canvas = new Canvas();
        DOM.appendChild(body, canvas);
        //DOM.getStyle(canvas).setPx("width", WIDTH);
        //DOM.getStyle(canvas).setPx("height", HEIGHT);

        DOM.setAttribute(canvas, "width", Integer.toString(WIDTH));
        DOM.setAttribute(canvas, "height", Integer.toString(HEIGHT));


        st = canvas.get2D();

        Console.info("Hello from TWT!");

        //t.onTimeout();
        //Script.setInterval(10, t);

        Unit u;
        Random r = new Random();
        for (int i = 0; i < 40; i++) {
            u = new Unit();
            u.position.set(r.nextFloat() * WIDTH - u.radius * 2 + u.radius,
                    r.nextFloat() * HEIGHT - u.radius * 2 + u.radius);

            double angl = r.nextFloat() * Math.PI * 2;
            u.direcrion.set((float) Math.sin(angl) * 20, (float) Math.cos(angl) * 20);

            units.add(u);
        }

        draw(0.0);
    }

    private static void draw(double time) {
        st.setFullStyle("red");
        st.fillRect(0, 0, WIDTH, HEIGHT);
        st.setFullStyle("green");

        for (int i = 0; i < units.length(); i++) {
            Unit u = units.get(i);
            u.update(time / 500);
        }

        for (int i = 0; i < units.length(); i++) {
            Unit u = units.get(i);
            u.draw();
        }

        Script.requestAnimationFrame(t);
    }

    private static class TimeOutClass implements Script.FrameRequest {

        @ForceInject
        @Override
        public void onFrame(double time) {
            EntryPoint.draw(time - time_old);
            time_old = time;
        }
    }

    private static class Unit {
        public final Vector direcrion = new Vector(10.f, 10.f);
        public final Vector realDir = new Vector(10.f, 10.f);
        private final Vector position = new Vector(0.f, 0.f);
        public float radius = 10;

        public void update(double speed) {

            realDir.set(direcrion);
            for (int i = 0; i < units.length(); i++) {
                Unit a = units.get(i);
                if (a == this)
                    continue;

                Vector toAg = Vector.fromTo(position, a.position);
                float minlen = radius * 2 + a.radius * 2;
                if (toAg.length() < minlen) {
                    float len = minlen - toAg.length();
                    len = (float) (len / minlen * Math.PI);
                    len = (float) Math.sin(len) * minlen * 1.5f;
                    toAg.norm().mul(-len);
                    realDir.add(toAg);
                }
            }

            //position.add(direcrion.copy().mul(speed));

            if (position.getX() > WIDTH - radius) {
                direcrion.setX(-Math.abs(direcrion.getX()));
            }

            if (position.getX() < radius)
                direcrion.setX(Math.abs(direcrion.getX()));

            if (position.getY() > HEIGHT - radius)
                direcrion.setY(-Math.abs(direcrion.getY()));

            if (position.getY() < radius)
                direcrion.setY(Math.abs(direcrion.getY()));


            position.add(realDir.copy().mul((float) speed));
        }

        public void draw() {
            st.fillRect(position.getX() - radius, position.getY() - radius, radius * 2, radius * 2);
        }
    }
}
