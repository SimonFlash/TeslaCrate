package com.mcsimonflash.sponge.teslacrate.internal;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;

public enum Utils {;

    public static Text toText(String msg) {
        return TextSerializers.FORMATTING_CODE.deserialize(msg);
    }

    public static ItemStack.Builder createItem(ItemType type, Text name, List<Text> lore) {
        return ItemStack.builder()
                .itemType(type)
                .add(Keys.DISPLAY_NAME, name)
                .add(Keys.ITEM_LORE, lore);
    }

}