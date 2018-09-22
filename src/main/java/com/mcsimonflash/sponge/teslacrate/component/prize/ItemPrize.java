package com.mcsimonflash.sponge.teslacrate.component.prize;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Prize;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.InventoryTransformations;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

public final class ItemPrize extends Prize<ItemPrize, Integer> {

    public static final Type<ItemPrize, Integer> TYPE = new Type<>("Item", ItemPrize::new, TeslaCrate.get().getContainer());

    private ItemStackSnapshot item = ItemStackSnapshot.NONE;

    private ItemPrize(String id) {
        super(id);
    }

    @Override
    protected final Integer getValue() {
        return item.getQuantity();
    }

    @Override
    public final boolean give(User user, Integer value) {
        return user.getInventory()
                .transform(InventoryTransformations.PLAYER_MAIN_HOTBAR_FIRST)
                .offer(ItemStack.builder().fromSnapshot(item).quantity(value).build())
                .getType() == InventoryTransactionResult.Type.SUCCESS;
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
        if (node.getNode("item").hasMapChildren()) {
            item = Serializers.deserializeItem(node.getNode("item"));
        } else {
            item = ItemStack.of(Serializers.deserializeCatalogType(node.getNode("item"), ItemType.class), 1).createSnapshot();
        }
        super.deserialize(node);
    }

    @Override
    protected final Integer deserializeValue(ConfigurationNode node) {
        return node.getInt(item.getQuantity());
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Integer value) {
        return value == item.getQuantity() ? item : ItemStack.builder().fromSnapshot(item).quantity(value).build().createSnapshot();
    }

}
