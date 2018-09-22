package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Reference;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.CraftItemEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            if (!Registry.KEYS.get(k).isPresent()) {
                player.sendMessage(TeslaCrate.get().getPrefix().concat(Utils.toText("&cThis item is registered as a &4" + k + "&c key, but that key doesn't exist.")));
            }
            event.setCancelled(true);
        });
    }

    @Listener
    public final void onInteractBlock(InteractBlockEvent event, @Root Player player) {
        event.getTargetBlock().getLocation().ifPresent(l -> {
            Registration registration = preInteract(event, l).orElse(null);
            if (registration != null) {
                interact(event, player, registration, event instanceof InteractBlockEvent.Primary);
            } else {
                player.getItemInHand(HandTypes.MAIN_HAND).flatMap(i -> i.toContainer().getString(DataQuery.of("UnsafeData", "TeslaCrate", "Key"))).ifPresent(k -> {
                    if (Registry.KEYS.get(k).isPresent()) {
                        if (event instanceof HandInteractEvent && ((HandInteractEvent) event).getHandType().equals(HandTypes.MAIN_HAND)) {
                            player.sendMessage(TeslaCrate.getMessage(player, "teslacrate.key.interact", "key", k));
                        }
                        event.setCancelled(true);
                    } else {
                        player.sendMessage(TeslaCrate.get().getPrefix().concat(Utils.toText("&cThis item is registered as a &4" + k + "&c key, but that key doesn't exist.")));
                    }
                });
            }
        });
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
                player.sendMessage(TeslaCrate.getMessage(player, "teslacrate.crate.no-permission", "player", player.getName(), "crate", registration.getCrate().getId()));
            } else if (primary) {
                if (player.hasPermission("teslacrate.crates." + registration.getCrate().getId() + ".preview")) {
                    registration.getCrate().preview(player, registration.getLocation());
                    return;
                } else {
                    player.sendMessage(TeslaCrate.getMessage(player, "teslacrate.crate.preview.no-permission", "player", player.getName(), "crate", registration.getCrate().getId()));
                }
            } else {
                //TODO: Cooldown
                List<Reference<? extends Key, ?>> missing = registration.getCrate().getKeys().stream().filter(r -> r.getComponent().get(player) < r.getValue()).collect(Collectors.toList());
                if (!missing.isEmpty()) {
                    player.sendMessage(TeslaCrate.getMessage(player, "teslacrate.crate.missing-keys", "crate", registration.getCrate().getId(), "keys", String.join(", ", missing.stream()
                            .map(r -> TeslaCrate.get().getMessages().get("teslacrate.crate.missing-keys.key-format", player.getLocale()).args("key", r.getComponent().getId(), "quantity", r.getValue()).toString())
                            .collect(Collectors.toList()))));
                } else {
                    Inventory.confirmation(registration.getCrate().getName(), "&2Open this crate.", registration.getCrate().getDisplayItem(), a -> {
                        registration.getCrate().getKeys().forEach(r -> r.getComponent().take(player, r.getValue()));
                        registration.getCrate().open(player, registration.getLocation());
                    }).open(player);
                    return;
                }
            }
            List<Reference<? extends Effect, ?>> effects = registration.getCrate().getEffects(Effect.Action.ON_REJECT);
        }
    }

}
