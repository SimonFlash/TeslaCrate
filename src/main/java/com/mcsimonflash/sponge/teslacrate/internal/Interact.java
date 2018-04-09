package com.mcsimonflash.sponge.teslacrate.internal;

import com.flowpowered.math.vector.Vector3d;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class Interact {

    @Listener
    public void onInteractItemPrimary(InteractItemEvent event, @Root Player player) {
        event.getItemStack().toContainer().getString(DataQuery.of("UnsafeData", "TeslaCrate", "Key")).ifPresent(i -> {
            //Registration registration = event.getInteractionPoint().flatMap(v -> preInteract(event, new Location<>(player.getWorld(), v))).orElse(null);
            if (!Storage.keys.containsKey(i)) {
                player.sendMessage(TeslaCrate.PREFIX.concat(Utils.toText("&cThis item is registered as a &4" + i + "&c key, but that key doesn't exist!")));
            //} else if (registration != null) {
            //    interact(event, player, registration, event instanceof InteractItemEvent.Primary);
            } else if (event instanceof HandInteractEvent && ((HandInteractEvent) event).getHandType() == HandTypes.MAIN_HAND) {
                TeslaCrate.sendMessage(player, "teslacrate.key.interact", "key", i);
            }
            event.setCancelled(true);
        });
    }

    @Listener
    public void onInteractBlock(InteractBlockEvent event, @Root Player player) {
        event.getTargetBlock().getLocation().ifPresent(l -> preInteract(event, l).ifPresent(r -> interact(event, player, r, event instanceof InteractBlockEvent.Primary)));
    }

    @Listener
    public void onInteractEntityEvent(InteractEntityEvent event, @Root Player player) {
        preInteract(event, event.getTargetEntity().getLocation()).ifPresent(r -> interact(event, player, r, event instanceof InteractEntityEvent.Primary));
    }

    private Optional<Registration> preInteract(InteractEvent event, Location<World> location) {
        Optional<Registration> optCrate = Optional.ofNullable(Storage.registry.get(new Location<>(location.getExtent(), location.getBlockPosition())));
        event.setCancelled(optCrate.isPresent());
        return optCrate;
    }

    private void interact(InteractEvent event, Player player, Registration registration, boolean primary) {
        if (event instanceof HandInteractEvent && ((HandInteractEvent) event).getHandType() == HandTypes.MAIN_HAND) {
            if (primary) {
                if (player.hasPermission("teslacrate.crates." + registration.getCrate().getName() + ".preview")) {
                    registration.getCrate().preview(player);
                } else {
                    TeslaCrate.sendMessage(player, "teslacrate.crate.preview.no-permission");
                }
            } else {
                if (!registration.getCrate().process(player, registration.getLocation())) {
                    Vector3d norm = player.getLocation().getPosition().sub(registration.getLocation().getPosition()).normalize();
                    player.setVelocity(Vector3d.from(norm.getX(), 1, norm.getZ()).mul(0.5));
                    Utils.playSound(player, SoundTypes.BLOCK_ANVIL_PLACE);
                }
            }
        }
    }

}