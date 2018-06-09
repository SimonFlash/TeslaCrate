package com.mcsimonflash.sponge.teslacrate.internal;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public enum Utils {;

    public static Text toText(String message) {
        return TextSerializers.FORMATTING_CODE.deserialize(message);
    }

}