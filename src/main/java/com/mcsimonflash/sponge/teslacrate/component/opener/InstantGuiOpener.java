package com.mcsimonflash.sponge.teslacrate.component.opener;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import com.mcsimonflash.sponge.teslacrate.api.component.Reference;
import com.mcsimonflash.sponge.teslacrate.api.component.Reward;
import com.mcsimonflash.sponge.teslacrate.internal.Inventory;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class InstantGuiOpener extends Opener {

    public static final InstantGuiOpener INSTANCE = new InstantGuiOpener();

    @Override
    public final void open(Player player, Location<World> location, Crate crate) {
        Reference<? extends Reward, Double> reward = crate.selectReward(player);
        Inventory.displayable(View.builder(), InventoryArchetypes.DISPENSER, crate.getName())
                .build(TeslaCrate.get().getContainer())
                .define(Layout.builder()
                        .dimension(InventoryDimension.of(3, 3))
                        .checker(Inventory.PANES.get(2), Inventory.PANES.get(1))
                        .center(Element.of(reward.getDisplayItem()))
                        .build())
                .open(player);
        crate.give(player, location, reward);
    }

}