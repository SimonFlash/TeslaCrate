package com.mcsimonflash.sponge.teslacrate.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Component;
import com.mcsimonflash.sponge.teslalibs.inventory.Action;
import com.mcsimonflash.sponge.teslalibs.inventory.Displayable;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.Page;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public enum Inventory {;

    public static final ImmutableList<Element> PANES = ImmutableList.of(
            Element.of(Utils.createItem(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.RED).build()),
            Element.of(Utils.createItem(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.ORANGE).build()),
            Element.of(Utils.createItem(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.YELLOW).build()),
            Element.of(Utils.createItem(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.LIME).build()),
            Element.of(Utils.createItem(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).build()),
            Element.of(Utils.createItem(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.BLUE).build()),
            Element.of(Utils.createItem(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.PURPLE).build()),
            Element.of(Utils.createItem(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.MAGENTA).build()),
            Element.of(Utils.createItem(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.PINK).build()));
    public static final Element
            BACK = Element.builder().build(),
            CLOSE = Element.of(Utils.createItem(ItemTypes.BARRIER, Utils.toText("&4Close"), Utils.toText("&cClose the componentMenu")).build(), a -> inTask(a.getPlayer()::closeInventory));
    private static final Element
            CRATES = createMenuIcon(Registry.CRATES, "Crates", "They're Crrreat!", "ZjYyNGM5MjdjZmVhMzEzNTU0Mjc5OTNkOGI3OTcxMmU4NmY5NGQ1OTUzNDMzZjg0ODg0OWEzOWE2ODc5In19fQ=="),
            EFFECTS = createMenuIcon(Registry.EFFECTS, "Effects", "It's super effective!", "YzJiMGEyNzA5YWQyN2M1NzgzYmE3YWNiZGFlODc4N2QxNzY3M2YwODg4ZjFiNmQ0ZTI0ZWUxMzI5OGQ0In19fQ=="),
            KEYS = createMenuIcon(Registry.KEYS, "Keys", "Confused?", "NjBjZTQzZTBjZGNlYTk4ZGNjNmYwYmUzN2IwZjc3NDVkYWFmMmE3ZGMyOWJmMTNiM2U3OGE2NWM2ZSJ9fX0="),
            PRIZES = createMenuIcon(Registry.PRIZES, "Prizes", "Sur-prize!", "ZWM3MDA3ZDE2YWJjZmFjOWM2ODMwYzc0ZDM3Y2ZkNDM5YTI2MzczNDU3ZDkxNDUyYzFhOTZiOGUwNGE2ZCJ9fX0="),
            REWARDS = createMenuIcon(Registry.REWARDS, "Rewards", "Oohh, shiny!", "NmNlZjlhYTE0ZTg4NDc3M2VhYzEzNGE0ZWU4OTcyMDYzZjQ2NmRlNjc4MzYzY2Y3YjFhMjFhODViNyJ9fX0="),
            LOCATIONS = Element.of(Utils.createSkull(Utils.toText("&eLocations"), Utils.toText("&6'Tis a cube land..."), "Y2Y0MDk0MmYzNjRmNmNiY2VmZmNmMTE1MTc5NjQxMDI4NmE0OGIxYWViYTc3MjQzZTIxODAyNmMwOWNkMSJ9fX0=").build(), a -> inTask(() -> locationMenu().open(a.getPlayer()))),
            HOME = Element.of(Utils.createSkull(Utils.toText("&eHome"), Utils.toText("&6Please do not live in crate."), "ZTM0YTM2MTlkYzY2ZmM1Zjk0MGY2OWFhMzMxZTU4OGI1Mjg1ZjZlMmU5OTgxYjhmOTNiOTk5MTZjMjk0YjQ4In19fQ==").build(), a -> inTask(() -> openMenu(a.getPlayer())));
    private static final View MENU = displayable(View.builder(), InventoryArchetypes.CHEST, Utils.toText("&eTesla&6Crate &7Menu"))
            .build(TeslaCrate.get().getContainer())
            .define(Layout.builder()
                    .dimension(InventoryDimension.of(9, 3))
                    .checker(PANES.get(2), PANES.get(1))
                    .set(CRATES, 10)
                    .set(KEYS, 11)
                    .set(EFFECTS, 12)
                    .set(REWARDS, 13)
                    .set(PRIZES, 14)
                    .set(LOCATIONS, 16)
                    .build());
    private static final Layout TEMPLATE = Layout.builder()
            .set(PANES.get(2), 0, 2, 4, 6, 8, 18, 26, 36, 38, 40, 42, 44, 46, 52)
            .set(PANES.get(1), 1, 3, 5, 7, 9, 17, 27, 35, 37, 39, 41, 43)
            .set(BACK, 45)
            .set(Page.FIRST, 47)
            .set(Page.PREVIOUS, 48)
            .set(Page.CURRENT, 49)
            .set(Page.NEXT, 50)
            .set(Page.LAST, 51)
            .set(CLOSE, 53)
            .build();

    public static void openMenu(Player player) {
        MENU.open(player);
    }

    private static void inTask(Runnable action) {
        Task.builder().execute(action).submit(TeslaCrate.get().getContainer());
    }

    public static View confirmation(Text name, String description, ItemStackSnapshot center, Consumer<Action> action) {
        return displayable(View.builder(), InventoryArchetypes.CHEST, name)
                .build(TeslaCrate.get().getContainer())
                .define(Layout.builder()
                        .checker(PANES.get(2), PANES.get(1))
                        .set(Element.of(center), 13)
                        .set(Element.of(Utils.createItem(ItemTypes.SLIME_BALL, Utils.toText("&aConfirm"), Utils.toText(description)).build(), a -> inTask(() -> action.accept(a))), 10)
                        .set(Element.of(Utils.createItem(ItemTypes.MAGMA_CREAM, Utils.toText("&cCancel"), Utils.toText("&4Do not open this crate.")).build(), a -> inTask(a.getPlayer()::closeInventory)), 16)
                        .build());
    }

    public static <T extends Displayable.Builder> T displayable(T builder, InventoryArchetype archetype, Text name) {
        return (T) builder.archetype(archetype).property(InventoryTitle.of(name));
    }

    public static Page page(Text name, List<Element> elements, Element back) {
        return displayable(Page.builder(), InventoryArchetypes.DOUBLE_CHEST, name)
                .layout(Layout.builder()
                        .from(TEMPLATE)
                        .replace(BACK, back)
                        .build())
                .build(TeslaCrate.get().getContainer())
                .define(elements);
    }

    private static Page componentMenu(Registry<? extends Component> registry, String name, Element back) {
        return page(Utils.toText("&e" + name), registry.getComponents().getDistinct().stream()
                .map(Tuple::getFirst)
                .sorted((Comparator.comparing(Component::getId)))
                .map(c -> createComponentIcon(c, back))
                .collect(Collectors.toList()), HOME);
    }

    private static Page locationMenu() {
        return page(Utils.toText("&eLocations"), Config.getAllRegistrations().values().stream()
                .map(r -> Element.of(ItemStack.builder().fromSnapshot(r.getCrate().getDisplayItem())
                        .add(Keys.ITEM_LORE, Lists.newArrayList(Utils.toText("&e" + r.getId())))
                        .build(), a -> Utils.teleport(a.getPlayer(), r.getLocation())))
                .collect(Collectors.toList()), Inventory.CLOSE);
    }

    private static Element createMenuIcon(Registry<? extends Component> registry, String name, String lore, String texture) {
        return Element.of(Utils.createSkull(Utils.toText("&e" + name), Utils.toText("&6" + lore), texture).build(), a -> componentMenu(registry, name, a.getElement()).open(a.getPlayer()));
    }

    private static Element createComponentIcon(Component component, Element back) {
        return Element.of(component.getDisplayItem());
    }

}
