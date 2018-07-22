package com.mcsimonflash.sponge.teslacrate.component.path;

import com.flowpowered.math.vector.Vector3f;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;

public final class CirclePath extends AnimationPath {

    private Vector3f axis = Vector3f.UNIT_Y;

    public final Vector3f getAxis() {
        return axis;
    }

    public final void setAxis(Vector3f axis) {
        this.axis = axis;
    }

    @Override
    public final Vector3f[] getPositions(float radians) {
        Vector3f[] vecs = new Vector3f[getSegments()];
        float[] shifts = AnimUtils.shift(radians, AnimUtils.shift(getSegments()));
        for (int i = 0; i < vecs.length; i++) {
            vecs[i] = AnimUtils.circle(-shifts[i], axis);
        }
        return vecs;
    }

}