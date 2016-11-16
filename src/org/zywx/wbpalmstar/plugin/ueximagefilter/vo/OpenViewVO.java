package org.zywx.wbpalmstar.plugin.ueximagefilter.vo;

import java.io.Serializable;

/**
 * Created by ylt on 2016/10/31.
 */

public class OpenViewVO implements Serializable {

    private int x=0;
    private int y=0;
    private int w;
    private int h;

    private String path;

    private String type;

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

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
