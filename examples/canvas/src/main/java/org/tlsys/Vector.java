package org.tlsys;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Vector {
    private float x;
    private float y;

    public Vector(Vector vector) {
        this(vector.getX(), vector.getY());
    }

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector() {
        this(0.f, 0.f);
    }

    public static Vector fromTo(Vector from, Vector to) {
        return to._sub(from);
    }

    public float getX() {
        return x;
    }

    public Vector setX(float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public Vector setY(float y) {
        this.y = y;
        return this;
    }

    public Vector set(float x, float y) {
        setX(x);
        setY(y);
        return this;
    }

    public Vector set(Vector vector) {
        set(vector.getX(), vector.getY());
        return this;
    }

    public Vector add(float x, float y) {
        return set(getX() + x, getY() + y);
    }

    public Vector mul(float value) {
        return set(getX() * value, getY() * value);
    }

    public Vector _mul(float value) {
        return new Vector(this).mul(value);
    }

    public Vector sub(float x, float y) {
        return set(getX() - x, getY() - y);
    }

    public Vector _sub(Vector v) {
        return _sub(v.getX(), v.getY());
    }

    public Vector _sub(float x, float y) {
        Vector n = new Vector(this);
        return n.sub(x, y);
    }

    public Vector _add(Vector vector) {
        return _add(vector.getX(), vector.getY());
    }

    public Vector _add(float x, float y) {
        Vector n = new Vector(this);
        return n.add(x, y);
    }

    public Vector cross() {
        float x = -y;
        setY(getX());
        setX(x);
        return this;
    }

    public Vector _cross() {
        Vector n = new Vector(this);
        return n.cross();
    }

    public float lengthSQR() {
        return getX() * getX() + getY() * getY();
    }

    public float length() {
        return (float) Math.sqrt(lengthSQR());
    }

    public Vector norm() {
        float n = 1 / length();
        setX(getX() * n);
        setY(getY() * n);
        return this;
    }

    public Vector _norm() {
        System.out.println("6");
        return new Vector(this).norm();
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Vector div(float value) {
        return set(getX() / value, getY() / value);
    }

    public Vector add(Vector vector) {
        return add(vector.getX(), vector.getY());
    }

    public Vector copy() {
        return new Vector(this);
    }
}
