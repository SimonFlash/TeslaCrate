package com.mcsimonflash.sponge.teslacrate.component.crate;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;

public final class StandardCrate extends Crate {

    public static final Type<StandardCrate> TYPE = new Type<>("Standard", StandardCrate::new, n -> false, TeslaCrate.get().getContainer());

    private StandardCrate(String id) {
        super(id);
    }

}