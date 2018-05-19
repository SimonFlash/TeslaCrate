package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Component;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.Page;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.scheduler.Task;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Inventory {

    public static final Element[] PANES = new Element[] {
            Element.of(Utils.createPane(DyeColors.RED)),
            Element.of(Utils.createPane(DyeColors.ORANGE)),
            Element.of(Utils.createPane(DyeColors.YELLOW)),
            Element.of(Utils.createPane(DyeColors.LIME)),
            Element.of(Utils.createPane(DyeColors.LIGHT_BLUE)),
            Element.of(Utils.createPane(DyeColors.BLUE)),
            Element.of(Utils.createPane(DyeColors.PURPLE)),
            Element.of(Utils.createPane(DyeColors.MAGENTA)),
            Element.of(Utils.createPane(DyeColors.PINK))};
    public static final Element COMMANDS = createIcon(Storage.commands.values(), "Commands", "#~ tar ???", "ODUxNGQyMjViMjYyZDg0N2M3ZTU1N2I0NzQzMjdkY2VmNzU4YzJjNTg4MmU0MWVlNmQ4YzVlOWNkM2JjOTE0In19fQ==");
    public static final Element CRATES = createIcon(Storage.crates.values(), "Crates", "They're Crrreat!", "ZjYyNGM5MjdjZmVhMzEzNTU0Mjc5OTNkOGI3OTcxMmU4NmY5NGQ1OTUzNDMzZjg0ODg0OWEzOWE2ODc5In19fQ==");
    public static final Element ITEMS = createIcon(Storage.items.values(), "Items", "Slimon says...", "ODk1YWVlYzZiODQyYWRhODY2OWY4NDZkNjViYzQ5NzYyNTk3ODI0YWI5NDRmMjJmNDViZjNiYmI5NDFhYmU2YyJ9fX0=");
    public static final Element KEYS = createIcon(Storage.keys.values(), "Keys", "Confused?", "NjBjZTQzZTBjZGNlYTk4ZGNjNmYwYmUzN2IwZjc3NDVkYWFmMmE3ZGMyOWJmMTNiM2U3OGE2NWM2ZSJ9fX0=");
    public static final Element REWARDS = createIcon(Storage.rewards.values(), "Rewards", "Oohh, shiny!", "NmNlZjlhYTE0ZTg4NDc3M2VhYzEzNGE0ZWU4OTcyMDYzZjQ2NmRlNjc4MzYzY2Y3YjFhMjFhODViNyJ9fX0=");
    public static final View MENU = View.builder()
            .property(InventoryTitle.of(Utils.toText("&eTesla&6Crate &7Menu")))
            .build(TeslaCrate.get().getContainer())
            .define(Layout.builder()
                    .dimension(InventoryDimension.of(9, 3))
                    .checker(PANES[2], PANES[1])
                    .set(CRATES, 11)
                    .set(REWARDS, 12)
                    .set(KEYS, 13)
                    .set(ITEMS, 14)
                    .set(COMMANDS, 15)
                    .build());
    public static final Element HOME = Element.of(Utils.createSkull("Home", "Please do not live in crate.", "ZTM0YTM2MTlkYzY2ZmM1Zjk0MGY2OWFhMzMxZTU4OGI1Mjg1ZjZlMmU5OTgxYjhmOTNiOTk5MTZjMjk0YjQ4In19fQ=="), a -> MENU.open(a.getPlayer()));
    public static final Element CLOSE = Element.of(Utils.createItem(ItemTypes.BARRIER, "&4Close", "&cClose the menu", true), a -> Task.builder().execute(t -> a.getPlayer().closeInventory()).delayTicks(1).submit(TeslaCrate.get().getContainer()));
    public static final Element BACK = Element.builder().build();
    public static final Layout TEMPLATE = Layout.builder()
            .set(PANES[2], 0, 2, 4, 6, 8, 18, 26, 36, 38, 40, 42, 44, 46, 52)
            .set(PANES[1], 1, 3, 5, 7, 9, 17, 27, 35, 37, 39, 41, 43)
            .set(BACK, 45)
            .set(Page.FIRST, 47)
            .set(Page.PREVIOUS, 48)
            .set(Page.CURRENT, 49)
            .set(Page.NEXT, 50)
            .set(Page.LAST, 51)
            .set(CLOSE, 53)
            .build();

    public static Element createIcon(Collection<? extends Component> components, String name, String lore, String texture) {
        return Element.of(Utils.createSkull(name, lore, texture), a -> menu(name, components, a.getElement()).open(a.getPlayer(), 1));
    }

    public static Element createComponent(Component component, Element back) {
        return Element.of(component.getDisplayItem(), a -> page(component.getName(), component.getMenuElements(back), back).open(a.getPlayer(), 1));
    }

    public static Element createDetail(String name, String lore) {
        return Element.of(Utils.createItem(ItemTypes.PAPER, name, lore, false));
    }

    public static Page menu(String name, Collection<? extends Component> components, Element back) {
        return page(name, components.stream()
                .sorted(((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName())))
                .map(c -> createComponent(c, back))
                .collect(Collectors.toList()), HOME);
    }

    public static Page page(String title, List<Element> elements, Element back) {
        return Page.builder()
                .layout(Layout.builder()
                        .from(TEMPLATE)
                        .replace(BACK, back)
                        .build())
                .property(InventoryTitle.of(Utils.toText(title)))
                .build(TeslaCrate.get().getContainer())
                .define(elements);
    }

}
