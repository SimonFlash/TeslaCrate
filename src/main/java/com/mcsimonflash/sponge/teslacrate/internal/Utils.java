package com.mcsimonflash.sponge.teslacrate.internal;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Component;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.Page;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {

    public static final Element YELLOW = Element.of(ItemStack.builder()
            .itemType(ItemTypes.STAINED_GLASS_PANE)
            .add(Keys.DYE_COLOR, DyeColors.YELLOW)
            .build());
    public static final Element GOLD = Element.of(ItemStack.builder()
            .itemType(ItemTypes.STAINED_GLASS_PANE)
            .add(Keys.DYE_COLOR, DyeColors.ORANGE)
            .build());
    public static final Element COMMAND = Element.of(createSkull("ODUxNGQyMjViMjYyZDg0N2M3ZTU1N2I0NzQzMjdkY2VmNzU4YzJjNTg4MmU0MWVlNmQ4YzVlOWNkM2JjOTE0In19fQ==", "Commands", "#~ tar ???"), p -> menu(Storage.commands.values()).open(p));
    public static final Element CRATE = Element.of(createSkull("ZjYyNGM5MjdjZmVhMzEzNTU0Mjc5OTNkOGI3OTcxMmU4NmY5NGQ1OTUzNDMzZjg0ODg0OWEzOWE2ODc5In19fQ==", "Crates", "They're Crrreat!"), p -> menu(Storage.crates.values()).open(p));
    public static final Element ITEM = Element.of(createSkull("ODk1YWVlYzZiODQyYWRhODY2OWY4NDZkNjViYzQ5NzYyNTk3ODI0YWI5NDRmMjJmNDViZjNiYmI5NDFhYmU2YyJ9fX0=", "Items", "Slimon says..."), p -> menu(Storage.items.values()).open(p));
    public static final Element KEY = Element.of(createSkull("NjBjZTQzZTBjZGNlYTk4ZGNjNmYwYmUzN2IwZjc3NDVkYWFmMmE3ZGMyOWJmMTNiM2U3OGE2NWM2ZSJ9fX0=", "Keys", "Confused?"), p -> menu(Storage.keys.values()).open(p));
    public static final Element REWARD = Element.of(createSkull("NmNlZjlhYTE0ZTg4NDc3M2VhYzEzNGE0ZWU4OTcyMDYzZjQ2NmRlNjc4MzYzY2Y3YjFhMjFhODViNyJ9fX0=", "Rewards", "Oohh, shiny!"), p -> menu(Storage.rewards.values()).open(p));
    public static final Layout TEMPLATE = Layout.builder()
            .slots(YELLOW, 0, 2, 4, 6, 8, 18, 26, 36, 38, 40, 42, 44)
            .slots(GOLD, 1, 3, 5, 7, 9, 17, 27, 35, 37, 39, 41, 43, 45, 47, 51, 53)
            .slot(Page.FIRST, 46)
            .slot(Page.PREVIOUS, 48)
            .slot(Page.CURRENT, 49)
            .slot(Page.NEXT, 50)
            .slot(Page.LAST, 52)
            .build();
    public static final View MENU = View.of(InventoryArchetypes.CHEST, TeslaCrate.getTesla().Container).define(Layout.builder()
            .dimension(9, 3)
            .checker(YELLOW, GOLD)
            .slot(CRATE, 11)
            .slot(KEY, 12)
            .slot(REWARD, 13)
            .slot(COMMAND, 14)
            .slot(ITEM, 15)
            .build());
    //public static final Element HOME = Element.of(createSkull("ZTM0YTM2MTlkYzY2ZmM1Zjk0MGY2OWFhMzMxZTU4OGI1Mjg1ZjZlMmU5OTgxYjhmOTNiOTk5MTZjMjk0YjQ4In19fQ==", "Menu", "Home sweet home"), MENU::open);

    public static Page menu(Collection<? extends Component> components) {
        List<Component> list = Lists.newArrayList(components);
        list.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        return page(list.stream().map(c -> Element.of(c.getDisplayItem(), p -> page(c.getMenuElements()).open(p))).collect(Collectors.toList()));
    }

    public static Page page(List<Element> elements) {
        return Page.of(TEMPLATE, TeslaCrate.getTesla().Container).define(elements);
    }

    public static Text toText(String message) {
        return TextSerializers.FORMATTING_CODE.deserialize(message);
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

    public static ItemStack createSkull(String texture, String name, String lore) {
        GameProfile profile = GameProfile.of(UUID.randomUUID(), null);
        profile.addProperty(ProfileProperty.of("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + texture));
        return ItemStack.builder()
                .from(createItem(ItemTypes.SKULL, name, lore, false))
                .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                .add(Keys.REPRESENTED_PLAYER, profile)
                .build();
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

    public static void playSound(Player player, SoundType sound) {
        player.playSound(sound, player.getLocation().getPosition(), 1);
    }

    public static void spawnFirework(Location<World> location) {
        Entity firework = location.getExtent().createEntity(EntityTypes.FIREWORK, location.getPosition());
        firework.offer(Keys.FIREWORK_EFFECTS, Lists.newArrayList(FireworkEffect.builder()
                .shape(FireworkShapes.LARGE_BALL)
                .colors(Color.YELLOW, Color.ofRgb(0xFFA500))
                .flicker(true)
                .build()));
        location.getExtent().spawnEntity(firework);
    }

}