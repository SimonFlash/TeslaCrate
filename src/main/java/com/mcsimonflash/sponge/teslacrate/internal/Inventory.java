package com.mcsimonflash.sponge.teslacrate.internal;

import com.google.common.collect.ImmutableList;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Component;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.Page;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Inventory {

    public static final Element
            BACK = Element.builder().build(),
            CLOSE = Element.of(Utils.createItem(ItemTypes.BARRIER, Utils.toText("&4Close"), ImmutableList.of(Utils.toText("&cClose the menu"))).build(), a -> inTask(a.getPlayer()::closeInventory));
    private static final Element
            YELLOW = Element.of(Utils.createItem(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.YELLOW).build()),
            ORANGE = Element.of(Utils.createItem(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.ORANGE).build()),
            CRATES = createMenuIcon(Registry.CRATES, "Crates", "They're Crrreat!", "ZjYyNGM5MjdjZmVhMzEzNTU0Mjc5OTNkOGI3OTcxMmU4NmY5NGQ1OTUzNDMzZjg0ODg0OWEzOWE2ODc5In19fQ=="),
            EFFECTS = createMenuIcon(Registry.EFFECTS, "Effects", "It's super effective!", "YzJiMGEyNzA5YWQyN2M1NzgzYmE3YWNiZGFlODc4N2QxNzY3M2YwODg4ZjFiNmQ0ZTI0ZWUxMzI5OGQ0In19fQ=="),
            KEYS = createMenuIcon(Registry.KEYS, "Keys", "Confused?", "NjBjZTQzZTBjZGNlYTk4ZGNjNmYwYmUzN2IwZjc3NDVkYWFmMmE3ZGMyOWJmMTNiM2U3OGE2NWM2ZSJ9fX0="),
            PRIZES = createMenuIcon(Registry.PRIZES, "Prizes", "Sur-prize!", "ZWM3MDA3ZDE2YWJjZmFjOWM2ODMwYzc0ZDM3Y2ZkNDM5YTI2MzczNDU3ZDkxNDUyYzFhOTZiOGUwNGE2ZCJ9fX0="),
            REWARDS = createMenuIcon(Registry.REWARDS, "Rewards", "Oohh, shiny!", "NmNlZjlhYTE0ZTg4NDc3M2VhYzEzNGE0ZWU4OTcyMDYzZjQ2NmRlNjc4MzYzY2Y3YjFhMjFhODViNyJ9fX0="),
            HOME = Element.of(Utils.createSkull(Utils.toText("&6Home"), ImmutableList.of(Utils.toText("&ePlease do not live in crate.")), "ZTM0YTM2MTlkYzY2ZmM1Zjk0MGY2OWFhMzMxZTU4OGI1Mjg1ZjZlMmU5OTgxYjhmOTNiOTk5MTZjMjk0YjQ4In19fQ==").build(), a -> inTask(() -> openMenu(a.getPlayer())));
    private static final View MENU = View.builder()
            .property(InventoryTitle.of(Utils.toText("&eTesla&6Crate &7Menu")))
            .build(TeslaCrate.get().getContainer())
            .define(Layout.builder()
                    .dimension(InventoryDimension.of(9, 3))
                    .checker(ORANGE, YELLOW)
                    .set(CRATES, 11)
                    .set(KEYS, 12)
                    .set(EFFECTS, 13)
                    .set(REWARDS, 14)
                    .set(PRIZES, 15)
                    .build());
    private static final Layout TEMPLATE = Layout.builder()
            .set(YELLOW, 0, 2, 4, 6, 8, 18, 26, 36, 38, 40, 42, 44, 46, 52)
            .set(ORANGE, 1, 3, 5, 7, 9, 17, 27, 35, 37, 39, 41, 43)
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

    public static Page page(Text name, List<Element> elements, Element back) {
        return Page.builder()
                .layout(Layout.builder()
                        .from(TEMPLATE)
                        .replace(BACK, back)
                        .build())
                .property(InventoryTitle.of(name))
                .build(TeslaCrate.get().getContainer())
                .define(elements);
    }

    private static Page menu(Registry<? extends Component> registry, String name, Element back) {
        return page(Utils.toText("&e" + name), registry.getComponents().getDistinct().stream()
                .map(Tuple::getFirst)
                .sorted((Comparator.comparing(Component::getId)))
                .map(c -> createComponentIcon(c, back))
                .collect(Collectors.toList()), HOME);
    }

    private static Element createMenuIcon(Registry<? extends Component> registry, String name, String lore, String texture) {
        return Element.of(Utils.createSkull(Utils.toText("&e" + name), ImmutableList.of(Utils.toText("&6" + lore)), texture).build(), a -> menu(registry, name, a.getElement()).open(a.getPlayer()));
    }

    private static Element createComponentIcon(Component component, Element back) {
        return Element.of(component.getDisplayItem());
    }

}
