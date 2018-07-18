package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.UUID;

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

    public static ItemStack.Builder createItem(ItemType type, Text name) {
        return ItemStack.builder()
                .itemType(type)
                .add(Keys.DISPLAY_NAME, name);
    }

    public static ItemStack.Builder createItem(ItemType type, Text name, List<Text> lore) {
        return createItem(type, name).add(Keys.ITEM_LORE, lore);
    }

    public static ItemStack.Builder createSkull(Text name, List<Text> lore, String texture) {
        GameProfile profile = GameProfile.of(UUID.randomUUID(), null);
        profile.addProperty(ProfileProperty.of("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + texture));
        return createItem(ItemTypes.SKULL, name, lore)
                .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                .add(Keys.REPRESENTED_PLAYER, profile);
    }

}