package com.example.mdp.map;

public class Obstacle {
    private int x, y, n, f, i;

    public static final int TARGET_FACE_NORTH = 1;
    public static final int TARGET_FACE_EAST = 0;
    public static final int TARGET_FACE_SOUTH = 3;
    public static final int TARGET_FACE_WEST = 2;
    public static final int TARGET_IMG_NULL = -1;

    public static final String BLUETOOTH_TARGET_IDENTIFIER = "TARGET";

    public Obstacle(int x, int y, int n) {
        this.x = x;
        this.y = y;
        this.n = n;
        this.f = TARGET_FACE_NORTH;
        this.i = TARGET_IMG_NULL;
    }

    public Obstacle(int x, int y, int n, int f) {
        this.x = x;
        this.y = y;
        this.n = n;
        this.f = f;
        this.i = TARGET_IMG_NULL;
    }

    public void setImg(int img) {
        this.i = img;
    }

    public int getImg() {
        return i;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public void cycleFaceClockwise(boolean clockwise) {
        if (clockwise)
            this.f = this.f == TARGET_FACE_SOUTH ? TARGET_FACE_EAST : this.f + 1;
        else
            this.f = this.f == TARGET_FACE_NORTH ? TARGET_FACE_WEST : this.f - 1;
    }

    @Override
    public String toString() {
        return "Target{" +
                "x=" + x +
                ", y=" + y +
                ", n=" + n +
                ", f=" + f +
                ", i=" + i +
                '}';
    }
}
