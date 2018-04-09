package com.mcsimonflash.sponge.teslacrate.internal;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import com.mcsimonflash.sponge.teslacrate.component.Reward;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;
import com.mcsimonflash.sponge.teslalibs.animation.Animator;
import com.mcsimonflash.sponge.teslalibs.animation.Frame;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Effects {

    public enum Gui {

        INSTANT {
            @Override
            public void run(Player player, Crate crate, Location<World> location) {
                Reward reward = crate.getRandomReward();
                View.builder()
                        .archetype(InventoryArchetypes.DISPENSER)
                        .build(TeslaCrate.get().getContainer())
                        .define(Layout.builder()
                                .dimension(InventoryDimension.of(3, 3))
                                .checker(Inventory.PANES[2], Inventory.PANES[1])
                                .center(Element.of(reward.getDisplayItem()))
                                .build())
                        .open(player);
                Utils.playSound(player, SoundTypes.ENTITY_PLAYER_LEVELUP);
                crate.give(player, reward, location);
            }
        },
        NONE {
            @Override
            public void run(Player player, Crate crate, Location<World> location) {
                crate.give(player, crate.getRandomReward(), location);
            }
        },
        ROULETTE {
            @Override
            public void run(Player player, Crate crate, Location<World> location) {
                int selection = (int) (9 * Math.random()), slow = 0;
                List<Reward> rewards = IntStream.range(0, 9).mapToObj(i -> crate.getRandomReward()).collect(Collectors.toList());
                List<Element> panes = Lists.newArrayList(Inventory.PANES);
                List<Integer> times = Lists.newArrayList(0, 2, 4, 6, 8, 10, 13, 16, 20, 24, 29, 34, 40, 47, 55, 64);
                List<Frame<Layout>> frames = Lists.newArrayList();
                for (int i = 0; i < 76 + selection; i++) {
                    Layout.Builder builder = Layout.builder();
                    IntStream.range(0, 9).forEach(j -> builder.set(j != 4 ? panes.get(j) : Element.of(ItemStack.builder().fromSnapshot(panes.get(j).getItem()).add(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.POWER, 7))).build()), j, j + 18));
                    Collections.rotate(panes, -1);
                    if (i < 11 + selection || times.contains(slow++)) {
                        IntStream.range(0, 9).forEach(j -> builder.set(Element.of(rewards.get(j).getDisplayItem()), j + 9));
                        Collections.rotate(rewards, 1);
                    }
                    frames.add(Frame.of(builder.build(), 50));
                }
                frames.add(Frame.of(Layout.builder().build(), 50));
                AtomicBoolean closeable = new AtomicBoolean();
                View view = View.builder()
                        .onClose(a -> a.getEvent().setCancelled(!closeable.get()))
                        .build(TeslaCrate.get().getContainer());
                Animator<View, Layout> animator = Animator.of(view, frames, TeslaCrate.get().getContainer());
                animator.onCompletion(() -> {
                    closeable.set(true);
                    Utils.playSound(player, SoundTypes.ENTITY_PLAYER_LEVELUP);
                    crate.give(player, rewards.get(5), location);
                });
                view.open(player);
                animator.start(0, false);
            }
        };

        public abstract void run(Player player, Crate crate, Location<World> location);

    }

    public enum Particle {

        CIRCLE(0) {
            @Override
            public void run(Location<World> location, float radians) {
                AnimUtils.spawn(location, AnimUtils.particle(Math.random() > 0.5 ? Color.BLACK : DARK_GRAY), AnimUtils.circle(radians).mul(1.5F));
            }
        },
        HELIX(-AnimUtils.TAU / 4) {
            @Override
            public void run(Location<World> location, float radians) {
                ParticleEffect particle = AnimUtils.particle(AnimUtils.rainbow(4 * radians / 7));
                for (Vector3f vec : AnimUtils.parametric(radians, 3)) {
                    AnimUtils.spawn(location, particle, vec);
                }
            }
        },
        NONE(0) {
            @Override
            public void run(Location<World> location, float radians) {}
        },
        RINGS(AnimUtils.TAU / 4) {
            @Override
            public void run(Location<World> location, float radians) {
                float[] shifts = AnimUtils.shift(radians, (float) Math.PI);
                float cos = AnimUtils.cos(shifts[0]);
                AnimUtils.spawn(location, AnimUtils.particle(Color.YELLOW), new Vector3f(AnimUtils.sin(shifts[0]), -cos, cos).normalize().mul(1.2F));
                AnimUtils.spawn(location, AnimUtils.particle(ORANGE), new Vector3f(AnimUtils.sin(shifts[1]), cos, cos).normalize().mul(1.2F));
            }
        };

        public static final float INCREASE = AnimUtils.TAU / 90;
        public static final Color DARK_GRAY = Color.ofRgb(0x404040);
        public static final Color ORANGE = Color.ofRgb(0xFFA500);

        public final float SHIFT;

        Particle(float shift) {
            SHIFT = shift;
        }

        public abstract void run(Location<World> location, float radians);

        public static class Runner implements Consumer<Task> {

                private final Particle particle;
                private final Location<World> location;
                private float radians;

                public Runner(Particle particle, Location<World> location) {
                    this.particle = particle;
                    this.location = location;
                    this.radians = particle.SHIFT;
                }

                @Override
                public void accept(Task task) {
                    particle.run(location, radians);
                    radians += Particle.INCREASE;
                }

        }

    }

}
