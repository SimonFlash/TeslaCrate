package com.mcsimonflash.sponge.teslacrate.component.path;

import com.flowpowered.math.vector.Vector3f;

public abstract class AnimationPath {

    private boolean animated = true;
    private int interval = 20;
    private int precision = 120;
    private int segments = 1;
    private float shift = 0;
    private float speed = 1;
    private Vector3f scale = Vector3f.ONE;

    protected AnimationPath() {}

    public final boolean isAnimated() {
        return animated;
    }

    public final void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public final int getInterval() {
        return interval;
    }

    public final void setInterval(int interval) {
        this.interval = interval;
    }

    public final int getPrecision() {
        return precision;
    }

    public final void setPrecision(int precision) {
        this.precision = precision;
    }

    public final int getSegments() {
        return segments;
    }

    public final void setSegments(int segments) {
        this.segments = segments;
    }

    public final float getShift() {
        return shift;
    }

    public final void setShift(float shift) {
        this.shift = shift;
    }

    public final float getSpeed() {
        return speed;
    }

    public final void setSpeed(float speed) {
        this.speed = speed;
    }

    public final Vector3f getScale() {
        return scale;
    }

    public final void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public abstract Vector3f[] getPositions(float radians);

}