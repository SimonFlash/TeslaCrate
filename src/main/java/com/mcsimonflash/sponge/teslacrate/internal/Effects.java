package com.mcsimonflash.sponge.teslacrate.internal;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacore.utils.AnimUtils;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import com.mcsimonflash.sponge.teslacrate.component.Reward;
import com.mcsimonflash.sponge.teslalibs.animation.Animator;
import com.mcsimonflash.sponge.teslalibs.animation.Frame;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Effects {

    private static final ItemStack[] PANES = new ItemStack[]{
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.WHITE).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.ORANGE).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.MAGENTA).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.YELLOW).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.LIME).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.PINK).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.GRAY).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.SILVER).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.CYAN).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.PURPLE).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.BLUE).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.BROWN).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.GREEN).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.RED).quantity(1).build(),
            ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.BLACK).quantity(1).build()};

    public enum Gui {

        INSTANT {
            @Override
            public void run(Player player, Crate crate, Location<World> location) {
                Reward reward = crate.getRandomReward();
                View.of(InventoryArchetypes.DISPENSER, TeslaCrate.getTesla().Container).define(Layout.builder()
                        .dimension(3, 3)
                        .checker(Utils.YELLOW, Utils.GOLD)
                        .center(Element.of(reward.getDisplayItem()))
                        .build()).open(player);
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
                List<ItemStack> panes = Lists.newArrayList(PANES[14], PANES[1], PANES[4], PANES[5], PANES[3], PANES[11], PANES[10], PANES[2], PANES[6]);
                List<Integer> times = Lists.newArrayList(0, 2, 4, 6, 8, 10, 13, 16, 20, 24, 29, 34, 40, 47, 55, 64);
                List<Frame<Layout>> frames = Lists.newArrayList();
                for (int i = 0; i < 76 + selection; i++) {
                    Layout.Builder builder = Layout.builder();
                    IntStream.range(0, 9).forEach(j -> builder.slots(Element.of(j != 4 ? panes.get(j) : ItemStack.builder().fromContainer(panes.get(j).toContainer()).add(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(new ItemEnchantment(Enchantments.POWER, 7))).build()), j, j + 18));
                    Collections.rotate(panes, -1);
                    if (i < 11 + selection || times.contains(slow++)) {
                        IntStream.range(0, 9).forEach(j -> builder.slot(Element.of(rewards.get(j).getDisplayItem()), j + 9));
                        Collections.rotate(rewards, 1);
                    }
                    frames.add(Frame.of(builder.build(), 50));
                }
                frames.add(Frame.of(Layout.builder().build(), 50));
                View view = View.of(InventoryArchetypes.CHEST, TeslaCrate.getTesla().Container);
                Animator<View, Layout> animator = Animator.of(view, frames, TeslaCrate.getTesla().Container);
                animator.onCompletion(() -> {
                    Utils.playSound(player, SoundTypes.ENTITY_PLAYER_LEVELUP);
                    crate.give(player, rewards.get(5), location);
                    view.setCloseable(true);
                });
                view.open(player);
                view.setCloseable(false);
                animator.start(0, false);
            }
        };

        public abstract void run(Player player, Crate crate, Location<World> location);

    }

    public enum Particle {

        CIRCLE(0) {
            @Override
            public void run(Location<World> location, float radians) {
                AnimUtils.spawn(location, AnimUtils.particle(Math.random() > 0.5 ? Color.BLACK : DARK_GRAY), AnimUtils.circle(radians, 1.5F));
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
                float cos1 = AnimUtils.cos(shifts[0]), cos2 = AnimUtils.cos(shifts[0]);
                AnimUtils.spawn(location, AnimUtils.particle(Color.YELLOW), new Vector3f(AnimUtils.sin(shifts[0]), -cos1, cos1).normalize().mul(1.2F));
                AnimUtils.spawn(location, AnimUtils.particle(ORANGE), new Vector3f(AnimUtils.sin(shifts[1]), cos2, cos2).normalize().mul(1.2F));
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
