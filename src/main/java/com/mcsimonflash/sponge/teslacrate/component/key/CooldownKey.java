package com.mcsimonflash.sponge.teslacrate.component.key;

import com.google.common.primitives.Ints;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Config;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.concurrent.TimeUnit;

public final class CooldownKey extends Key<CooldownKey> {

    public static final Type<CooldownKey, Integer> TYPE = new Type<>("Cooldown", CooldownKey::new, n -> !n.getNode("cooldown").isVirtual(), TeslaCrate.get().getContainer());

    private int cooldown = 0;
    private TimeUnit unit = TimeUnit.MILLISECONDS;

    private CooldownKey(String id) {
        super(id);
    }

    @Override
    public final int get(User user) {
        long cooldown = unit.toMillis(getCooldown(user));
        long time = cooldown == 0 ? 0 : cooldown - System.currentTimeMillis() + Config.getCooldown(user.getUniqueId(), this);
        return time > 0 ? Ints.saturatedCast(time) : 0;
    }

    @Override
    public boolean check(User user, int quantity) {
        return get(user) < quantity;
    }

    @Override
    public final boolean give(User user, int quantity) {
        return false;
    }

    @Override
    public final boolean take(User user, int quantity) {
        return Config.resetCooldown(user.getUniqueId(), this);
    }

    private long getCooldown(User user) {
        try {
            return user.getOption("teslacrate.keys." + getId() + ".cooldown").map(Long::parseLong).orElse((long) cooldown);
        } catch (NumberFormatException e) {
            TeslaCrate.get().getLogger().warn("User " + user.getName() + " contains a malformed cooldown option for key " + getId() + ".");
            return cooldown;
        }
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        if (node.getNode("cooldown").hasMapChildren()) {
            cooldown = node.getNode("cooldown", "cooldown").getInt(0);
            NodeUtils.ifAttached(node.getNode("cooldown", "unit"), n -> unit = Serializers.enumeration(n, TimeUnit.class));
        } else {
            cooldown = node.getNode("cooldown").getInt(0);
        }
        super.deserialize(node);
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Integer value) {
        return Utils.createItem(ItemTypes.CLOCK, getName()).build().createSnapshot();
    }

}