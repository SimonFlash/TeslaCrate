package com.mcsimonflash.sponge.teslacrate.internal;

import com.google.common.collect.Lists;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {

    public static Text toText(String message) {
        return TextSerializers.FORMATTING_CODE.deserialize(message);
    }

    public static ItemStack createItem(ItemType type, String name, String lore, boolean deserialize) {
        return createItem(type, name, Lists.newArrayList(lore), deserialize);
    }

    public static ItemStack createItem(ItemType type, String name, List<String> lore, boolean deserialize) {
        return ItemStack.builder()
                .itemType(type)
                .add(Keys.DISPLAY_NAME, deserialize ? toText("&e" + name) : Text.of(TextColors.YELLOW, name))
                .add(Keys.ITEM_LORE, lore.stream().map(l -> deserialize ? toText("&6" + l) : Text.of(TextColors.GOLD, l)).collect(Collectors.toList()))
                .build();
    }

    public static ItemStack createPane(DyeColor color) {
        return ItemStack.builder()
                .itemType(ItemTypes.STAINED_GLASS_PANE)
                .add(Keys.DYE_COLOR, color)
                .build();
    }

    public static ItemStack createSkull(String name, String lore, String texture) {
        GameProfile profile = GameProfile.of(UUID.randomUUID(), null);
        profile.addProperty(ProfileProperty.of("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + texture));
        ItemStack skull = createItem(ItemTypes.SKULL, name, lore, false);
        skull.offer(Keys.SKULL_TYPE, SkullTypes.PLAYER);
        skull.offer(Keys.REPRESENTED_PLAYER, profile);
        return skull;
    }

    public static void playSound(Player player, SoundType sound) {
        player.playSound(sound, player.getLocation().getPosition(), 1);
    }

    public static void spawnFirework(FireworkEffect effect, Location<World> location) {
        Entity firework = location.getExtent().createEntity(EntityTypes.FIREWORK, location.getPosition());
        firework.offer(Keys.FIREWORK_EFFECTS, Lists.newArrayList(effect));
        location.getExtent().spawnEntity(firework);
    }

    public static InventoryTransactionResult offerItem(User user, ItemStack item, int quantity) {
        return user.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class))
                .union(user.getInventory())
                .offer(ItemStack.builder().from(item).quantity(quantity).build());
    }

    public static List<String> printItem(ItemStack item) {
        List<String> info = Lists.newArrayList("id=" + item.getType().getId().replace("minecraft:", ""));
        item.toContainer().getInt(DataQuery.of("UnsafeDamage")).filter(d -> d != 0).ifPresent(d -> info.add("data=" + d));
        info.add("quantity=" + item.getQuantity());
        for (Key key : item.getKeys()) {
            info.add(key.getId().replaceFirst("sponge:", "").replace("_", "-") + "=" + print(item.get(key).get()));
        }
        return info;
    }

    private static String print(Object object) {
        if (object instanceof Iterable<?>) {
            StringBuilder sb = new StringBuilder();
            ((Iterable<?>) object).forEach(o -> sb.append("\n-").append(print(o)));
            return sb.toString();
        } else if (object instanceof Text) {
            return TextSerializers.FORMATTING_CODE.serialize((Text) object);
        }
        return object.toString();
    }

}