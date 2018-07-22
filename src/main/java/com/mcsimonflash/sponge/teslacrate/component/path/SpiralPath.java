package com.mcsimonflash.sponge.teslacrate.component.path;

import com.flowpowered.math.vector.Vector3f;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;

import java.util.ArrayList;
import java.util.List;

public final class SpiralPath extends AnimationPath {

    @Override
    public final Vector3f[] getPositions(float radians) {
        List<Vector3f> vecs = new ArrayList<>(getSegments() * getPrecision());
        for (float shift : AnimUtils.shift(radians, AnimUtils.shift(getSegments()))) {
            for (radians = 0; radians < AnimUtils.TAU; radians += AnimUtils.shift(getPrecision())) {
                vecs.add(AnimUtils.circle(radians + shift).mul(radians / AnimUtils.TAU));
            }
        }
        return vecs.toArray(new Vector3f[0]);
    }

}