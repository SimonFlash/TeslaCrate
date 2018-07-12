package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.*;
import org.spongepowered.api.world.*;

import java.util.Optional;

public final class Listeners {

    @Listener
    public final void onCraftItem(CraftItemEvent event, @Root Player player) {
        event.getTransactions().forEach(t -> t.getOriginal().toContainer().getString(DataQuery.of("UnsafeData", "TeslaCrate", "Key")).ifPresent(k -> {
            if (!Registry.KEYS.get(k).isPresent()) {
                player.sendMessage(TeslaCrate.get().getPrefix().concat(Utils.toText("&cThis item is registered as a &4" + k + "&c key, but that key doesn't exist!")));
            } else {
                player.sendMessage(TeslaCrate.getMessage(player, "teslacrate.key.craft", "key", k));
            }
            event.setCancelled(true);
        }));
    }

    @Listener
    public final void onInteractItem(InteractItemEvent event, @Root Player player) {
        event.getItemStack().toContainer().getString(DataQuery.of("UnsafeData", "TeslaCrate", "Key")).ifPresent(k -> {
            Location<World> location = event.getInteractionPoint().map(p -> new Location<>(player.getWorld(), p)).orElse(null);
            if (!Registry.KEYS.get(k).isPresent()) {
                player.sendMessage(TeslaCrate.get().getPrefix().concat(Utils.toText("&cThis item is registered as a &4" + k + "&c key, but that key doesn't exist!")));
            } else if (location != null) {
                Registration registration = preInteract(event, location).orElseGet(() -> preInteract(event, location.add(location.getPosition().getX() % 1 == 0 ? -1 : 0, location.getPosition().getY() % 1 == 0 ? -1 : 0, location.getPosition().getZ() % 1 == 0 ? -1 : 0)).orElse(null));
                if (registration != null) {
                    interact(event, player, registration, event instanceof InteractItemEvent.Primary);
                } else if (event instanceof HandInteractEvent && ((HandInteractEvent) event).getHandType() == HandTypes.MAIN_HAND) {
                    player.sendMessage(TeslaCrate.getMessage(player, "teslacrate.key.interact", "key", k));
                }
            }
            event.setCancelled(true);
        });
    }

    @Listener
    public final void onInteractBlock(InteractBlockEvent event, @Root Player player) {
        event.getTargetBlock().getLocation().ifPresent(l -> preInteract(event, l).ifPresent(r -> interact(event, player, r, event instanceof InteractBlockEvent.Primary)));
    }

    @Listener
    public final void onInteractEntity(InteractEntityEvent event, @Root Player player) {
        preInteract(event, event.getTargetEntity().getLocation()).ifPresent(r -> interact(event, player, r, event instanceof InteractEntityEvent.Primary));
    }

    private Optional<Registration> preInteract(InteractEvent event, Location<World> location) {
        Optional<Registration> optReg = Config.getRegistration(new Location<>(location.getExtent(), location.getBlockPosition()));
        event.setCancelled(optReg.isPresent());
        return optReg;
    }

    private void interact(InteractEvent event, Player player, Registration registration, boolean primary) {
        if (event instanceof HandInteractEvent && ((HandInteractEvent) event).getHandType() == HandTypes.MAIN_HAND) {
            if (!player.hasPermission("teslacrate.crates." + registration.getCrate().getId() + ".base")) {
                player.sendMessage(TeslaCrate.getMessage(player, "teslacrate.crate.no-permission", "player", player.getName()));
            } else if (primary) {
                if (player.hasPermission("teslacrate.crates." + registration.getCrate().getId() + ".preview")) {
                    registration.getCrate().preview(player, registration.getLocation());
                } else {
                    player.sendMessage(TeslaCrate.getMessage(player, "teslacrate.crate.preview.no-permission", "player", player.getName()));
                }
            } else if (registration.getCrate().takeKeys(player)) {
                registration.getCrate().open(player, registration.getLocation());
            }
        }
    }

}
