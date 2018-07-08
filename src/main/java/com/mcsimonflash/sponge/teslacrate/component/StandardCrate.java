package com.mcsimonflash.sponge.teslacrate.component;

import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;

public final class StandardCrate extends Crate {

    public static final Type<StandardCrate, Object> TYPE = Type.create("Standard", StandardCrate::new);

    private StandardCrate(String name) {
        super(name);
    }

}