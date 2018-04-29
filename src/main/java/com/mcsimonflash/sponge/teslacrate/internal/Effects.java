package com.mcsimonflash.sponge.teslacrate.internal;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import com.mcsimonflash.sponge.teslacrate.component.Reward;
import com.mcsimonflash.sponge.teslalibs.animation.Animator;
import com.mcsimonflash.sponge.teslalibs.animation.Frame;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
                        .property(InventoryTitle.of(Utils.toText(crate.getDisplayName())))
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
                        .property(InventoryTitle.of(Utils.toText(crate.getDisplayName())))
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

}
