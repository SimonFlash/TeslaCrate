package com.mcsimonflash.sponge.teslacrate.component.path;

import com.flowpowered.math.vector.Vector3f;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;

import java.util.ArrayList;
import java.util.List;

public class VortexPath extends AnimationPath {

    private int precision;

    public VortexPath(int segments, int precision) {
        super(segments);
        this.precision = precision;
    }

    @Override
    public Vector3f[] getPositions(float radians) {
        List<Vector3f> vecs = new ArrayList<>(getSegments() * precision);
        for (float shift : AnimUtils.shift(radians, AnimUtils.shift(getSegments()))) {
            for (radians = 0; radians < AnimUtils.TAU / 2; radians += AnimUtils.shift(precision)) {
                vecs.add(AnimUtils.circle(radians + shift).mul(radians / AnimUtils.TAU).add(0, 1 - AnimUtils.sin(radians / 2), 0));
            }
        }
        return vecs.toArray(new Vector3f[0]);
    }

}