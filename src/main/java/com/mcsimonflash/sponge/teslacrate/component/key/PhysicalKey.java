package com.mcsimonflash.sponge.teslacrate.component.key;

import com.google.common.base.*;
import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import static com.google.common.base.Preconditions.checkState;

//TODO: Data registration to identify keys
public final class PhysicalKey extends Key<PhysicalKey> {

    public static final Type<PhysicalKey, Integer, Builder, RefBuilder> TYPE = Type.create("Physical", Builder::new, RefBuilder::new)
            .matcher(n -> !n.getNode("item").isVirtual())
            .build();

    private final ItemStackSnapshot item;

    private PhysicalKey(Builder builder) {
        super(builder);
        item = builder.item;
    }

    @Override
    public int get(User user) {
        return user.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item.createStack())).totalItems();
    }

    @Override
    public boolean check(User user, int quantity) {
        return get(user) >= quantity;
    }

    @Override
    public boolean give(User user, int quantity) {
        ItemStack stack = item.createStack();
        stack.setQuantity(quantity);
        return user.getInventory().offer(stack).getType().equals(InventoryTransactionResult.Type.SUCCESS);
    }

    @Override
    public boolean take(User user, int quantity) {
        Inventory inv = user.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item.createStack()));
        return inv.totalItems() >= quantity && inv.poll(quantity).isPresent();
    }

    public final ItemStackSnapshot getItem() {
        return item;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("item", item);
    }

    public static final class Builder extends Key.Builder<PhysicalKey, Builder> {

        private ItemStackSnapshot item;

        private Builder(String id) {
            super(id, TYPE);
        }

        public final Builder item(ItemStackSnapshot item) {
            this.item = item;
            return this;
        }

        @Override
        public final Builder deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node).item(Serializers.deserializeItem(node.getNode("item")));
        }

        @Override
        protected void resolve() throws IllegalStateException {
            super.resolve();
            checkState(item != null, "Item must be defined.");
            checkState(quantity <= item.getType().getMaxStackQuantity(), "Quantity cannot be larger than %s.", item.getType().getMaxStackQuantity());
        }

        @Override
        public final PhysicalKey build() {
            return new PhysicalKey(this);
        }

    }

    public static final class RefBuilder extends Reference.Builder<PhysicalKey, Integer, RefBuilder> {

        private RefBuilder(String id, PhysicalKey component) {
            super(id, component);
        }

        @Override
        public final RefBuilder deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node).value(node.getInt(component.getQuantity()));
        }

        @Override
        public final void resolve() {
            super.resolve();
            checkState(value <= component.item.getType().getMaxStackQuantity(), "Quantity cannot be larger than %s.", component.item.getType().getMaxStackQuantity());
        }

    }

}
