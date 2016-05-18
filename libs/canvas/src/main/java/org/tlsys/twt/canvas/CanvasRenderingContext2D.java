package org.tlsys.twt.canvas;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class CanvasRenderingContext2D {
    private final Canvas parent;
    private final Object ctx;

    public CanvasRenderingContext2D(Canvas parent, Object ctx) {
        this.parent = parent;
        this.ctx = ctx;
    }

    /**
     * Save current state, and push it to stack
     */
    public void saveState() {
        Script.code(ctx, ".save()");
    }

    /**
     * restore saved state by popping the top entry in stack
     */
    public void restoreState() {
        Script.code(ctx, ".restore()");
    }

    public void clearRect(float x, float y, float width, float height) {
        Script.code(ctx, ".clearRect(",
                CastUtil.toObject(x), ",",
                CastUtil.toObject(y), ",",
                CastUtil.toObject(width), ",",
                CastUtil.toObject(height), ")");
    }

    /**
     * Рисует залитый прямоугольник в позиции (x, y), размеры которого определяются шириной width и высотой height.
     *
     * @param x      начальная позиция по оси X
     * @param y      начальная позиция по оси Y
     * @param width  ширена
     * @param height высота
     */
    public void fillRect(float x, float y, float width, float height) {
        Script.code(ctx, ".fillRect(",
                CastUtil.toObject(x), ",",
                CastUtil.toObject(y), ",",
                CastUtil.toObject(width), ",",
                CastUtil.toObject(height), ")");
    }

    /**
     * Рисует на холсте прямоугольник с начальной точкой в позиции (x, y), имеющий ширину w и высоту h, используя
     * текущий стиль обводки.
     *
     * @param x      начальная позиция по оси X
     * @param y      начальная позиция по оси Y
     * @param width  ширена
     * @param height высота
     */
    public void strokeRect(float x, float y, float width, float height) {
        Script.code(ctx, ".strokeRect(",
                CastUtil.toObject(x), ",",
                CastUtil.toObject(y), ",",
                CastUtil.toObject(width), ",",
                CastUtil.toObject(height), ")");
    }

    //TODO create function for gradient, and for pattern
    public void setFullStyle(String color) {
        Script.code(ctx, ".fillStyle=", color);
    }

    //TODO create function for gradient, and for pattern
    public void setStrokeStyle(String color) {
        Script.code(ctx, ".strokeStyle=", color);
    }

    /**
     * Move start point to new coodinat
     *
     * @param x
     * @param y
     */
    public void moveTo(float x, float y) {
        Script.code(ctx, ".moveTo(", CastUtil.toObject(x), ",", CastUtil.toObject(y), ")");
    }

    /**
     * The CanvasRenderingContext2D.arc() method of the Canvas 2D API adds an arc to the path which is centered at (x, y) position with radius r starting at startAngle and ending at endAngle going in the given direction by anticlockwise (defaulting to clockwise).
     *
     * @param x
     * @param y
     * @param radius
     * @param startAngle
     * @param endAngle
     * @param anticlockwise
     */
    public void arc(float x, float y, float radius, float startAngle, float endAngle, boolean anticlockwise) {
        Script.code(ctx, ".arc(",
                CastUtil.toObject(x), ",",
                CastUtil.toObject(y), ",",
                CastUtil.toObject(radius), ",",
                CastUtil.toObject(startAngle), ",",
                CastUtil.toObject(endAngle), ",",
                CastUtil.toObject(anticlockwise), ")");
    }

    public void arc(float x, float y, float radius, float startAngle, float endAngle) {
        arc(x, y, radius, startAngle, endAngle, true);
    }

    public void setTransform(float a, float b, float c, float d, float e, float f) {
        Script.code(ctx, ".setTransform(",
                CastUtil.toObject(a), ",",
                CastUtil.toObject(b), ",",
                CastUtil.toObject(c), ",",
                CastUtil.toObject(d), ",",
                CastUtil.toObject(e), ",",
                CastUtil.toObject(f), ")");
    }

    public void transform(float a, float b, float c, float d, float e, float f) {
        Script.code(ctx, ".transform(",
                CastUtil.toObject(a), ",",
                CastUtil.toObject(b), ",",
                CastUtil.toObject(c), ",",
                CastUtil.toObject(d), ",",
                CastUtil.toObject(e), ",",
                CastUtil.toObject(f), ")");
    }

    public void translate(float x, float y) {
        Script.code(ctx, ".translate(",
                CastUtil.toObject(x), ",",
                CastUtil.toObject(y), ")");
    }

    public void scale(float x, float y) {
        Script.code(ctx, ".scale(",
                CastUtil.toObject(x), ",",
                CastUtil.toObject(y), ")");
    }

    public void rotate(float angle) {
        Script.code(ctx, ".rotate(",
                CastUtil.toObject(angle), ")");
    }

    //TODO create function for gradient, and for pattern
    public void fill() {
        Script.code(ctx, ".fill()");
    }

    public void stroke() {
        Script.code(ctx, ".stroke()");
    }

    public void clip() {
        Script.code(ctx, ".clip()");
    }
}
