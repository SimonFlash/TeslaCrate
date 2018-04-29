package com.mcsimonflash.sponge.teslacrate.component.path;

import com.flowpowered.math.vector.Vector3f;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;

public class HelixPath extends AnimationPath {

    public HelixPath(int segments) {
        super(segments);
    }

    @Override
    public Vector3f[] getPositions(float radians) {
        return AnimUtils.parametric(radians, getSegments());
    }

}