package com.mcsimonflash.sponge.teslacrate;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.commands.*;
import com.mcsimonflash.sponge.teslacrate.managers.Config;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.Crate;
import com.mcsimonflash.sponge.teslacrate.objects.CrateLocation;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

@Plugin(id = "teslacrate", name = "TeslaCrate", version = "mc1.10.2-v1.0.1", authors = "Simon_Flash", description = "The shockingly easy way to manage crates")
public class TeslaCrate {

    private static TeslaCrate plugin;
    public static TeslaCrate getPlugin() {
        return plugin;
    }

    private static URL wiki;
    public static URL getWiki() {
        return wiki;
    }

    private static URL discord;
    public static URL getDiscord() {
        return discord;
    }

    private static EconomyService economyService;
    public static EconomyService getEconServ() {
        return economyService;
    }

    @Inject
    private Logger logger;
    public Logger getLogger() {
        return logger;
    }

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;
    public Path getDefaultConfig() {
        return defaultConfig;
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent event) {
        plugin = this;
        logger.info("+=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=+");
        logger.info("|     TeslaCrate -- Version 1.0.1     |");
        logger.info("|      Developed By: Simon_Flash      |");
        logger.info("+=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=+");
        Config.readConfig();
        try {
            wiki = new URL("https://github.com/SimonFlash/TeslaCrate/wiki");
        } catch (MalformedURLException ignored) {
            getLogger().error("Unable to locate TeslaCrate Wiki!");
        }
        try {
            discord = new URL("https://discordapp.com/invite/4wayq37");
        } catch (MalformedURLException ignored) {
            getLogger().error("Unable to locate Support Discord!");
        }
        CommandSpec BuyKey = CommandSpec.builder()
                .executor(new BuyKey())
                .description(Text.of("Buys crate keys"))
                .permission("teslacrate.buykey.base")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("crate-name"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("quantity"))))
                .build();
        CommandSpec DeleteLoc = CommandSpec.builder()
                .executor(new DeleteLoc())
                .description(Text.of("Deletes a crate from a location"))
                .permission("teslacrate.deleteloc.base")
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("world-name"))),
                        GenericArguments.optional(GenericArguments.vector3d(Text.of("crate-pos"))))
                .build();
        CommandSpec GiveKey = CommandSpec.builder()
                .executor(new GiveKey())
                .description(Text.of("Gives a user crate keys"))
                .permission("teslacrate.givekey.base")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("crate-name"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("quantity"))))
                .build();
        CommandSpec ListCrates = CommandSpec.builder()
                .executor(new ListCrates())
                .description(Text.of("Displays a list of crates"))
                .permission("teslacrate.listcrates.base")
                .build();
        CommandSpec ListKeys = CommandSpec.builder()
                .executor(new ListKeys())
                .description(Text.of("Displays a list of user keys"))
                .permission("teslacrate.listkeys.base")
                .arguments(
                        GenericArguments.optional(GenericArguments.user(Text.of("opt-user"))))
                .build();
        CommandSpec Lookup = CommandSpec.builder()
                .executor(new Lookup())
                .description(Text.of("Returns crates matches flags"))
                .permission("teslacrate.lookup.base")
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("flags"))))
                .build();
        CommandSpec SellKey = CommandSpec.builder()
                .executor(new SellKey())
                .description(Text.of("Sells crate keys"))
                .permission("teslacrate.sellkey.base")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("crate-name"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("quantity"))))
                .build();
        CommandSpec SetLoc = CommandSpec.builder()
                .executor(new SetLoc())
                .description(Text.of("Sets a crate to a location"))
                .permission("teslacrate.setloc.base")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("crate-name"))),
                        GenericArguments.optional(GenericArguments.string(Text.of("world-name"))),
                        GenericArguments.optional(GenericArguments.vector3d(Text.of("crate-pos"))))
                .build();
        CommandSpec Base = CommandSpec.builder()
                .executor(new Base())
                .description(Text.of("Opens in-game documentation"))
                .permission("teslacrate.base")
                .child(BuyKey, "BuyKey", "Buy", "bk")
                .child(DeleteLoc, "DeleteLoc", "DelLoc", "Del", "dl")
                .child(GiveKey, "GiveKey", "Give", "gk")
                .child(ListCrates, "ListCrates", "Crates", "lc")
                .child(ListKeys, "ListKeys", "Keys", "lk")
                .child(Lookup, "Lookup", "lu")
                .child(SellKey, "SellKey", "Sell", "sk")
                .child(SetLoc, "SetLoc", "Set", "sl")
                .build();
        Sponge.getCommandManager().register(plugin, Base, Lists.newArrayList("TeslaCrate", "TCrate", "Crate", "tc"));
        Sponge.getCommandManager().register(plugin, ListCrates, Lists.newArrayList("ListCrates", "Crates"));
        Sponge.getCommandManager().register(plugin, ListKeys, Lists.newArrayList("ListKeys", "Keys"));
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        Config.readConfig();
    }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if (event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    @Listener
    public void onInteractBlockPrimary(InteractBlockEvent.Primary event, @First Player player) {
        Location<World> loc = event.getTargetBlock().getLocation().orElse(null);
        if (loc != null) {
            Crate crate = Util.getRegisteredCrate(new CrateLocation(player.getWorld().getName(), loc.getBlockPosition()));
            if (crate != null) {
                event.setCancelled(true);
                if (Util.notSpamClick(player)) {
                    player.sendMessage(Util.prefix.concat(Util.toText("&7Woah! This block is registered as a &f" + crate.Name + " &7crate!")));
                }
            }
        }
    }

    @Listener
    public void onInteractBlockSecondary(InteractBlockEvent.Secondary event, @First Player player) {
        Location<World> loc = event.getTargetBlock().getLocation().orElse(null);
        if (loc != null) {
            Crate crate = Util.getRegisteredCrate(new CrateLocation(player.getWorld().getName(), loc.getBlockPosition()));
            if (crate != null) {
                event.setCancelled(true);
                processInteract(crate, player);
            }
        }
    }

    @Listener
    public void onInteractEntityEventSecondary(InteractEntityEvent.Secondary event, @First Player player) {
        Crate crate = Util.getRegisteredCrate(new CrateLocation(player.getWorld().getName(), event.getTargetEntity().getLocation().getBlockPosition()));
        if (crate != null) {
            event.setCancelled(true);
            processInteract(crate, player);
        }
    }

    public void processInteract(Crate crate, Player player) {
        if (Util.notSpamClick(player)) {
            if (player.hasPermission("teslacrate.crates." + crate.Name + ".base")) {
                if (crate.Keydata.Physical) {
                    ItemStack handItem = player.getItemInHand(HandTypes.MAIN_HAND).orElse(null);
                    if (handItem != null) {
                        List<Text> lore = handItem.get(Keys.ITEM_LORE).orElse(null);
                        if (lore != null && lore.equals(Util.toText(Lists.newArrayList("Usable on a " + crate.Name + " crate")))) {
                            if (handItem.getQuantity() >= crate.Keydata.Consumed) {
                                crate.process(player);
                                return;
                            } else {
                                player.sendMessage(Util.displayPrefix.concat(Util.toText("&7You must have at least &f" + crate.Keydata.Consumed + "&7 keys for this crate!")));
                            }
                        } else {
                            player.sendMessage(Util.displayPrefix.concat(Util.toText("&7You are not holding a key for this crate!")));
                        }
                    } else {
                        player.sendMessage(Util.displayPrefix.concat(Util.toText("&7You are not holding a key for this crate!")));
                    }
                } else {
                    if (Config.getKeys(player, crate) >= crate.Keydata.Consumed) {
                        crate.process(player);
                        return;
                    } else {
                        player.sendMessage(Util.displayPrefix.concat(Util.toText("&7You must have at least &f" + crate.Keydata.Consumed + "&7 keys for this crate!")));
                    }
                }
                if (crate.Keydata.BuyCost > 0 && player.hasPermission("teslacrate.buykey.base") && player.hasPermission("teslacrate.crates." + crate.Name + ".buykey")) {
                    UniqueAccount account = economyService.getOrCreateAccount(player.getUniqueId()).orElse(null);
                    if (account != null) {
                        if (account.getBalance(economyService.getDefaultCurrency()).doubleValue() >= crate.Keydata.BuyCost) {
                            player.sendMessage(Util.displayPrefix.concat(Util.toText("&7&7Click ").concat(Text.builder("here")
                                    .color(TextColors.WHITE)
                                    .onHover(TextActions.showText(Util.toText("&fClick to suggest the command")))
                                    .onClick(TextActions.suggestCommand("/TeslaCrate BuyKey " + crate.Name + " 1"))
                                    .build()).concat(Util.toText("&7 to purchase a key for this crate for &f" + crate.Keydata.BuyCost + "!"))));
                        } else {
                            player.sendMessage(Util.displayPrefix.concat(Util.toText("&7Oh no! Looks like you'll need to save up a bit more for this one.")));
                        }
                    } else {
                        player.sendMessage(Util.prefix.concat(Util.toText("&7An unexpected error occurred locating your account!")));
                    }
                }
            } else {
                player.sendMessage(Util.displayPrefix.concat(Util.toText("&7You do not have permission to use this crate!")));
            }
        }
    }
}
