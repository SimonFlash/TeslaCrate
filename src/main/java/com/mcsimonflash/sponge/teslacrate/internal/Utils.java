package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;

public enum Utils {;

    public static Text toText(String msg) {
        return TextSerializers.FORMATTING_CODE.deserialize(msg);
    }

    public static String toString(Text text) {
        return TextSerializers.FORMATTING_CODE.serialize(text);
    }

    public static long getCooldown(User user, Crate crate) {
        try {
            return user.getOption("teslacrate.crates." + crate.getId() + ".cooldown").map(Long::parseLong).orElse(crate.getCooldown());
        } catch (NumberFormatException e) {
            TeslaCrate.get().getLogger().warn("User " + user.getName() + " contains a malformed cooldown option for crate " + crate.getId() + ".");
            return crate.getCooldown();
        }
    }

    public static ItemStack.Builder createItem(ItemType type, Text name, List<Text> lore) {
        return ItemStack.builder()
                .itemType(type)
                .add(Keys.DISPLAY_NAME, name)
                .add(Keys.ITEM_LORE, lore);
    }

}