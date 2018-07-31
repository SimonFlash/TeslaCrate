package com.mcsimonflash.sponge.teslacrate.component.effect;

import com.google.common.base.MoreObjects;
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
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;

import java.util.List;

public final class PotionEffect extends Effect<Integer> {

    public static final Type<PotionEffect> TYPE = new Type<>("Potion", PotionEffect::new, n -> !n.getNode("potion").isVirtual(), TeslaCrate.get().getContainer());

    private PotionEffectType type = PotionEffectTypes.SPEED;
    private int duration = 10;
    private int amplifier = 1;
    private boolean ambient = false;
    private boolean particles = false;

    private PotionEffect(String id) {
        super(id);
    }

    public final PotionEffectType getType() {
        return type;
    }

    public final void setType(PotionEffectType type) {
        this.type = type;
    }

    public final int getDuration() {
        return duration;
    }

    public final void setDuration(int duration) {
        this.duration = duration;
    }

    public final int getAmplifier() {
        return amplifier;
    }

    public final void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public final boolean isAmbient() {
        return ambient;
    }

    public final void setAmbient(boolean ambient) {
        this.ambient = ambient;
    }

    public final boolean isParticles() {
        return particles;
    }

    public final void setParticles(boolean particles) {
        this.particles = particles;
    }

    @Override
    public final void run(Player player, Location location, Integer duration) {
        List<org.spongepowered.api.effect.potion.PotionEffect> effects = player.get(Keys.POTION_EFFECTS).orElseGet(Lists::newArrayList);
        effects.add(org.spongepowered.api.effect.potion.PotionEffect.builder()
                .potionType(type).duration(20 * duration).amplifier(amplifier).ambience(ambient).particles(particles).build());
        player.offer(Keys.POTION_EFFECTS, effects);
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
       if (node.getNode("potion").hasMapChildren()) {
            NodeUtils.ifAttached(node.getNode("potion", "type"), n -> setType(Serializers.deserializeCatalogType(n, PotionEffectType.class)));
            setDuration(node.getNode("potion", "duration").getInt(100));
            setAmplifier(node.getNode("potion", "amplifier").getInt(1));
            setAmbient(node.getNode("potion", "ambient").getBoolean(false));
            setParticles(node.getNode("potion", "particles").getBoolean(false));
        } else {
           NodeUtils.ifAttached(node.getNode("potion", "type"), n -> setType(Serializers.deserializeCatalogType(n, PotionEffectType.class)));
       }
        super.deserialize(node);
    }

    @Override
    protected final ItemStack.Builder createDisplayItem(Integer duration) {
        return Utils.createItem(ItemTypes.SPLASH_POTION, getName(), Lists.newArrayList(getDescription()));
    }

    @Override
    protected final MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "type", type.getId())
                .add(indent + "duration", duration)
                .add(indent + "amplifier", amplifier)
                .add(indent + "ambient", ambient)
                .add(indent + "particles", particles);
    }

    @Override
    public final Integer getRefValue() {
        return duration;
    }

    @Override
    public final Effect.Ref<? extends Effect<Integer>, Integer> createRef(String id) {
        return new Ref(id, this);
    }

    public static final class Ref extends Effect.Ref<PotionEffect, Integer> {

        private Ref(String id, PotionEffect component) {
            super(id, component);
        }

        @Override
        public final Integer deserializeValue(ConfigurationNode node) {
            return node.getInt(getComponent().getRefValue());
        }

    }

}
