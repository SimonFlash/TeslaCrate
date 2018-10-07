package com.mcsimonflash.sponge.teslacrate.component.key;

import com.google.common.primitives.Ints;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DoublePlantTypes;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;

import java.math.BigDecimal;
import java.util.Optional;

public final class MoneyKey extends Key<MoneyKey> {

    public static final Type<MoneyKey, Integer> TYPE = new Type<>("Money", MoneyKey::new, n -> !n.getNode("money").isVirtual(), TeslaCrate.get().getContainer());

    private int money = 0;
    private Currency currency = Sponge.getServiceManager().provideUnchecked(EconomyService.class).getDefaultCurrency();

    private MoneyKey(String id) {
        super(id);
    }

    @Override
    public Integer getValue() {
        return money;
    }

    @Override
    public final int get(User user) {
        return getAccount(user).map(a -> Ints.saturatedCast(a.getBalance(currency).longValue())).orElse(0);
    }

    @Override
    public final boolean give(User user, int quantity) {
        return getAccount(user).map(a -> a.deposit(currency, BigDecimal.valueOf(quantity), Sponge.getCauseStackManager().getCurrentCause()).getResult() == ResultType.SUCCESS).orElse(false);
    }

    @Override
    public final boolean take(User user, int quantity) {
        return getAccount(user).map(a -> a.withdraw(currency, BigDecimal.valueOf(quantity), Sponge.getCauseStackManager().getCurrentCause()).getResult() == ResultType.SUCCESS).orElse(false);
    }

    private Optional<UniqueAccount> getAccount(User user) {
        return Sponge.getServiceManager().provideUnchecked(EconomyService.class).getOrCreateAccount(user.getUniqueId());
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        if (node.getNode("money").hasMapChildren()) {
            money = node.getNode("money", "money").getInt(0);
            NodeUtils.ifAttached(node.getNode("money", "currency"), n -> currency = Serializers.catalogType(n, Currency.class));
        } else {
            money = node.getNode("money").getInt(0);
        }
        super.deserialize(node);
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Integer value) {
        return Utils.createItem(ItemTypes.DOUBLE_PLANT, getName()).add(Keys.DOUBLE_PLANT_TYPE, DoublePlantTypes.SUNFLOWER).build().createSnapshot();
    }

}