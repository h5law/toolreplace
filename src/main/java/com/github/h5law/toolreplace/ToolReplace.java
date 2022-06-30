package com.github.h5law.toolreplace;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class ToolReplace extends JavaPlugin {

    private void debug(Player player, String text, @Nullable Object ...vars) {
        if (text != null) {
            player.sendMessage(String.format("[DEBUG] " + text, vars));
        }
    }

    @Override
    public void onEnable() {
        PluginDescriptionFile pdFile = getDescription();
        Logger logger = getLogger();
        logger.log(
                Level.INFO,
                "{0} has been enabled v{1}",
                new Object[]{ pdFile.getName(), pdFile.getVersion() }
        );

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ToolReplaceListener(this), this);
    }

    private LinkedHashMap<Integer, ? extends ItemStack> searchInventory(Player player, ItemStack broken) {
        HashMap<Integer, ? extends ItemStack> matches = player.getInventory().all(broken.getType());

        // remove the broken tool from list
        matches.entrySet()
               .removeIf(e -> (e.getValue().equals(broken) || e.getValue().getDurability() >= 100));

        // sort based on damage
        Comparator<Map.Entry<Integer, ? extends ItemStack>> durabilityComparator =
                (e1, e2) -> e1.getValue().getDurability() < e2.getValue().getDurability() ? 1 : -1;

        // return sorted map of matching items <Slot, Item>
        return matches.entrySet().stream()
                .sorted(durabilityComparator)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                          (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void swapItem(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack broken = event.getBrokenItem();

        LinkedHashMap<Integer, ? extends ItemStack> matches = searchInventory(player, broken);
        if (matches.size() == 0) {
            debug(player, "No replacement found");
            return;
        };

        // extract most damaged match from inventory
        Integer slot = matches.keySet().stream().findFirst().get();
        ItemStack item = matches.get(slot);

        PlayerInventory inv = player.getInventory();
        debug(player, "Replacing  %s with item from slot %s", item.getType(), slot);
        // move match to slot
        inv.setItem(inv.getHeldItemSlot(), item);
        // remove match from original slot
        inv.setItem(slot, null);
        return;
    }

    @Override
    public void onDisable() {
        PluginDescriptionFile pdFile = getDescription();
        Logger logger = getLogger();
        logger.log(
                Level.INFO,
                "{0} has been disabled v{1}",
                new Object[]{ pdFile.getName(), pdFile.getVersion() }
        );
    }
}
