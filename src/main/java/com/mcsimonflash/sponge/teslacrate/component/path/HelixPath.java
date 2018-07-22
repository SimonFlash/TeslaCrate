package com.mcsimonflash.sponge.teslacrate.component.path;

import com.flowpowered.math.vector.Vector3f;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;

public final class HelixPath extends AnimationPath {

    @Override
    public final Vector3f[] getPositions(float radians) {
        return AnimUtils.parametric(radians, getSegments());
    }

}