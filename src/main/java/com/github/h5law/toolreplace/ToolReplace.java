package com.github.h5law.toolreplace;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class ToolReplace extends JavaPlugin {

    // In set then show debug info to player with UUID
    public HashSet<UUID> PlayerDebug = new HashSet<>();
    // In set then stop replacing tools
    public HashSet<UUID> DisableToolReplace = new HashSet<>();

    public void toggle(Player player, HashSet<UUID> set) {
        if (set.contains(player.getUniqueId())) {
            set.remove(player.getUniqueId());
        } else {
            set.add(player.getUniqueId());
        }
    }

    public void toggle(Player player, HashSet<UUID> set, Boolean bool) {
        if (bool) {
            set.add(player.getUniqueId());
        } else {
            set.remove(player.getUniqueId());
        }

    }

    private void debug(Player player, Boolean critical, String text, @Nullable Object... vars) {
        if (!PlayerDebug.contains(player.getUniqueId())) return;
        if (text != null) {
            if (vars.length > 0) {
                for (int i = 0; i < vars.length; i++) {
                    text = text.replace("{" + i + "}", vars[i].toString());
                }
            }
            player.sendMessage(
                    (critical ? ChatColor.RED : ChatColor.GREEN)
                    + "[ToolReplace] " + text);
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
        getCommand("toolreplace").setExecutor(new ToolReplaceCommand(this));
    }

    private LinkedHashMap<Integer, ? extends ItemStack> searchInventory(Player player, ItemStack broken) {
        HashMap<Integer, ? extends ItemStack> matches = player.getInventory().all(broken.getType());

        // remove the broken tool from list
        matches.entrySet()
               .removeIf(e -> {
                   // check if broken
                   if (e.getValue().getDurability() >= 100) {
                       return true;
                   // clause to catch items spawned damage
                   } else if (e.getValue().equals(broken)) {
                       return true;
                   }
                   return false;
               });

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
            debug(player, false,"No replacement found");
            return;
        };

        // extract most damaged match from inventory
        Integer slot = matches.keySet().stream().findFirst().get();
        ItemStack item = matches.get(slot);

        PlayerInventory inv = player.getInventory();
        debug(
                player,
                false,
                "Replacing {0} with item from slot {1} with damage {2}",
                item.getType(),
                slot,
                item.getDurability()
        );
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
