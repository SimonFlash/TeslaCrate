package com.mcsimonflash.sponge.teslacrate.component.prize;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Prize;
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
import org.spongepowered.api.service.economy.transaction.ResultType;

import java.math.BigDecimal;

public final class MoneyPrize extends Prize<MoneyPrize, Integer> {

    public static final Type<MoneyPrize, Integer> TYPE = new Type<>("Money", MoneyPrize::new, n -> !n.getNode("money").isVirtual(), TeslaCrate.get().getContainer());

    private int money;
    private Currency currency = Sponge.getServiceManager().provideUnchecked(EconomyService.class).getDefaultCurrency();

    private MoneyPrize(String id) {
        super(id);
    }

    @Override
    public final Integer getValue() {
        return money;
    }

    @Override
    public final boolean give(User user, Integer value) {
        return Sponge.getServiceManager().provideUnchecked(EconomyService.class).getOrCreateAccount(user.getUniqueId())
                .map(a -> a.deposit(currency, BigDecimal.valueOf(money), Sponge.getCauseStackManager().getCurrentCause()).getResult() == ResultType.SUCCESS)
                .orElse(false);
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
        if (node.getNode("money").hasMapChildren()) {
            money = node.getNode("money", "money").getInt(0);
            NodeUtils.ifAttached(node.getNode("money", "currency"), n -> currency = Serializers.catalogType(n, Currency.class));
        } else {
            money = node.getNode("money").getInt(0);
        }
        super.deserialize(node);
    }

    @Override
    protected final Integer deserializeValue(ConfigurationNode node) {
        return node.getInt(money);
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Integer value) {
        return Utils.createItem(ItemTypes.DOUBLE_PLANT, getName(), getDescription()).add(Keys.DOUBLE_PLANT_TYPE, DoublePlantTypes.SUNFLOWER).build().createSnapshot();
    }

}
