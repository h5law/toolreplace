package com.github.h5law.toolreplace;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;

public class ToolReplaceListener implements Listener {
    private final ToolReplace plugin;

    public ToolReplaceListener(ToolReplace plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        if (plugin.DisableToolReplace.contains(event.getPlayer().getUniqueId())) return;
        plugin.swapItem(event);
    }
}
