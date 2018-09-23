package com.mcsimonflash.sponge.teslacrate.component.effect;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;

public final class PotionEffect extends Effect<PotionEffect, Integer> {

    public static final Type<PotionEffect, Integer> TYPE = new Type<>("Potion", PotionEffect::new, n -> !n.getNode("potion").isVirtual(), TeslaCrate.get().getContainer());

    private PotionEffectType type = PotionEffectTypes.SPEED;
    private int duration = 10;
    private int amplifier = 1;
    private boolean ambient = false;
    private boolean particles = false;

    private PotionEffect(String id) {
        super(id);
    }

    @Override
    public final Integer getValue() {
        return duration;
    }

    @Override
    public final void run(Player player, Location<World> location, Integer duration) {
        List<org.spongepowered.api.effect.potion.PotionEffect> effects = player.get(Keys.POTION_EFFECTS).orElseGet(Lists::newArrayList);
        effects.add(org.spongepowered.api.effect.potion.PotionEffect.builder()
                .potionType(type).duration(20 * duration).amplifier(amplifier).ambience(ambient).particles(particles).build());
        player.offer(Keys.POTION_EFFECTS, effects);
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
       if (node.getNode("potion").hasMapChildren()) {
            NodeUtils.ifAttached(node.getNode("potion", "type"), n -> type = Serializers.catalogType(n, PotionEffectType.class));
            duration = node.getNode("potion", "duration").getInt(100);
            amplifier = node.getNode("potion", "amplifier").getInt(1);
            ambient = node.getNode("potion", "ambient").getBoolean(false);
            particles = node.getNode("potion", "particles").getBoolean(false);
        } else {
           NodeUtils.ifAttached(node.getNode("potion", "type"), n -> type = Serializers.catalogType(n, PotionEffectType.class));
       }
        super.deserialize(node);
    }

    @Override
    protected final Integer deserializeValue(ConfigurationNode node) {
        return node.getInt(duration);
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Integer duration) {
        return Utils.createItem(ItemTypes.SPLASH_POTION, getName()).build().createSnapshot();
    }

}