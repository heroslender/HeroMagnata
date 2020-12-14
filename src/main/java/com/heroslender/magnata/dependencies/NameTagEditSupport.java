package com.heroslender.magnata.dependencies;

import com.heroslender.magnata.HeroMagnata;
import com.heroslender.magnata.api.events.MagnataChangeEvent;
import com.nametagedit.plugin.NametagEdit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NameTagEditSupport implements Listener {

    public NameTagEditSupport() {
        Bukkit.getLogger().info("[HeroMagnata] NameTagEdit detetado!");
        Bukkit.getPluginManager().registerEvents(this, HeroMagnata.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    private void onMagnataUpdate(final MagnataChangeEvent e) {
        Player player = Bukkit.getPlayer(e.getAntigoMagnata().getPlayer());
        if (player != null) {
            NametagEdit.getApi().applyTagToPlayer(player, false);
        }

        player = Bukkit.getPlayer(e.getNovoMagnata().getPlayer());
        if (player != null) {
            NametagEdit.getApi().applyTagToPlayer(player, false);
        }
    }
}
