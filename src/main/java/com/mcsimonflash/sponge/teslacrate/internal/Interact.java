package com.mcsimonflash.sponge.teslacrate.internal;

import com.flowpowered.math.vector.Vector3d;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class Interact {

    @Listener
    public void onInteractItem(InteractItemEvent event, @Root Player player) {
        event.getItemStack().toContainer().getString(DataQuery.of("UnsafeData", "TeslaCrate", "Key")).ifPresent(i -> {
            event.setCancelled(true);
            if (!Storage.keys.containsKey(i)) {
                player.sendMessage(TeslaCrate.PREFIX.concat(Utils.toText("&cThis item is registered as a &4" + i + "&c key, but that key doesn't exist!")));
            } else {
                TeslaCrate.sendMessage(player, "teslacrate.key.interact", "key", i);
            }
        });
    }

    @Listener
    public void onInteractBlockPrimary(InteractBlockEvent.Primary.MainHand event, @Root Player player) {
        event.getTargetBlock().getLocation().ifPresent(l -> preprocess(event, l).ifPresent(r -> {
            if (player.hasPermission("teslacrate.crates." + r.getCrate().getName() + ".preview")) {
                r.getCrate().preview(player);
            } else {
                TeslaCrate.sendMessage(player, "teslacrate.crate.preview.no-permission");
            }
        }));
    }

    @Listener
    public void onInteractBlockSecondary(InteractBlockEvent.Secondary.MainHand event, @Root Player player) {
        event.getTargetBlock().getLocation().ifPresent(l -> preprocess(event, l).ifPresent(r -> process(player, r)));
    }

    @Listener
    public void onInteractEntityEventSecondary(InteractEntityEvent.Secondary.MainHand event, @Root Player player) {
        preprocess(event, event.getTargetEntity().getLocation()).ifPresent(r -> process(player, r));
    }

    private Optional<Registration> preprocess(InteractEvent event, Location<World> location) {
        Optional<Registration> optCrate = Optional.ofNullable(Storage.registry.get(location));
        event.setCancelled(optCrate.isPresent());
        return optCrate;
    }

    private void process(Player player, Registration registration) {
        if (!registration.getCrate().process(player, registration.getLocation())) {
            Vector3d norm = player.getLocation().getPosition().sub(registration.getLocation().getPosition()).normalize();
            player.setVelocity(Vector3d.from(norm.getX(), 1, norm.getZ()).mul(0.5));
            Utils.playSound(player, SoundTypes.BLOCK_ANVIL_PLACE);
        }
    }

}