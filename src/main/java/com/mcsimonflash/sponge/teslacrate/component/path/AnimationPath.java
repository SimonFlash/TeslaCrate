package com.mcsimonflash.sponge.teslacrate.component.path;

import com.flowpowered.math.vector.Vector3f;

public abstract class AnimationPath {

    private final int segments;

    public AnimationPath(int segments) {
        this.segments = segments;
    }

    public int getSegments() {
        return segments;
    }

    public abstract Vector3f[] getPositions(float radians);

}