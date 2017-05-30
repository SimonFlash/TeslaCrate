package com.mcsimonflash.sponge.teslacrate.objects.rewards;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Arrays;

public abstract class CrateReward {

    public enum RewardType {
        COMMAND("Command"),
        COMPOUND("Compound"),
        ITEM("Item");

        RewardType(String name) {
            Name = name;
        }

        public String Name;

        public static RewardType parseRewardType(String type) throws EnumConstantNotPresentException {
            return Arrays.stream(RewardType.values()).filter(t -> t.Name.equalsIgnoreCase(type)).findAny().orElseThrow(() -> new EnumConstantNotPresentException(RewardType.class, type));
        }
    }

    public String Display;

    public CrateReward(String display) {
        Display = display;
    }

    public abstract void processReward(Player player);
}