package com.openxu.bmap.bean;

/**
 * Created by Admin on 2018/4/24.
 */

public class MakerPoint {

    private float x;
    private float y;
    private String title;

    public MakerPoint() {
    }

    public MakerPoint(float x, float y, String title) {
        this.x = x;
        this.y = y;
        this.title = title;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
