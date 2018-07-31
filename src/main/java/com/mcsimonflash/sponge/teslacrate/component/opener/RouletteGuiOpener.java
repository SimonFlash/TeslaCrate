package com.mcsimonflash.sponge.teslacrate.component.opener;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import com.mcsimonflash.sponge.teslacrate.api.component.Reward;
import com.mcsimonflash.sponge.teslacrate.internal.Inventory;
import com.mcsimonflash.sponge.teslalibs.animation.Animator;
import com.mcsimonflash.sponge.teslalibs.animation.Frame;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class RouletteGuiOpener extends Opener {

    public static final RouletteGuiOpener INSTANCE = new RouletteGuiOpener();

    @Override
    public final void open(Player player, Location<World> location, Crate crate) {
        int selection = (int) (9 * Math.random()), slow = 0;
        List<Reward.Ref> rewards = IntStream.range(0, 9).mapToObj(i -> crate.selectReward(player)).collect(Collectors.toList());
        List<Element> panes = Lists.newArrayList(Inventory.PANES);
        List<Integer> times = Lists.newArrayList(0, 2, 4, 6, 8, 10, 13, 16, 20, 24, 29, 34, 40, 47, 55, 64);
        List<Frame<Layout>> frames = new ArrayList<>(76 + selection);
        for (int i = 0; i < 76 + selection; i++) {
            Layout.Builder builder = Layout.builder();
            IntStream.range(0, 9).forEach(j -> builder.set(j != 4 ? panes.get(j) : Element.of(ItemStack.builder()
                    .fromSnapshot(panes.get(j).getItem())
                    .add(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.of(EnchantmentTypes.POWER, 7)))
                    .build()), j, j + 18));
            Collections.rotate(panes, -1);
            if (i < 11 + selection || times.contains(slow++)) {
                IntStream.range(0, 9).forEach(j -> builder.set(Element.of(rewards.get(j).getDisplayItem()), j + 9));
                Collections.rotate(rewards, 1);
            }
            frames.add(Frame.of(builder.build(), 50));
        }
        frames.add(Frame.of(Layout.builder().build(), 50));
        AtomicBoolean received = new AtomicBoolean(false);
        Runnable close = () -> {
            if (!received.getAndSet(true)) {
                crate.give(player, location, rewards.get(5));
            }
        };
        View view = Inventory.displayable(View.builder(), InventoryArchetypes.CHEST, crate.getName())
                .onClose(a -> close.run())
                .build(TeslaCrate.get().getContainer());
        Animator animator = Animator.of(view, frames, TeslaCrate.get().getContainer());
        animator.onCompletion(close);
        view.open(player);
        animator.start(0, false);
    }

}