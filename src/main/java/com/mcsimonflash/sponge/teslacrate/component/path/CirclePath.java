package com.mcsimonflash.sponge.teslacrate.component.path;

import com.flowpowered.math.vector.Vector3f;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;

import javax.annotation.Nullable;

public class CirclePath extends AnimationPath {

    @Nullable private Vector3f axis;

    public CirclePath(int segments, Vector3f axis) {
        super(segments);
        this.axis = axis.equals(Vector3f.UNIT_Y) ? null : axis;
    }

    @Override
    public Vector3f[] getPositions(float radians) {
        Vector3f[] vecs = new Vector3f[getSegments()];
        float[] shifts = AnimUtils.shift(radians, AnimUtils.shift(getSegments()));
        for (int i = 0; i < vecs.length; i++) {
            vecs[i] = axis != null ? AnimUtils.circle(-shifts[i], axis) : AnimUtils.circle(shifts[i]);
        }
        return vecs;
    }

}