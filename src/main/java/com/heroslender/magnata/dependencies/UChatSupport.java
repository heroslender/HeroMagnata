package com.heroslender.HeroMagnata.dependencies;

import br.net.fabiozumbi12.UltimateChat.Bukkit.API.SendChannelMessageEvent;
import com.heroslender.HeroMagnata.Config;
import com.heroslender.HeroMagnata.HeroMagnata;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UChatSupport implements Listener {

    public UChatSupport() {
        Bukkit.getLogger().info("[HeroMagnata] UChat detetado!");
        Bukkit.getPluginManager().registerEvents(this, HeroMagnata.getInstance());
    }

    @EventHandler
    private void uchatListener(SendChannelMessageEvent event) {
        if (HeroMagnata.getMagnataAtual().equals(event.getSender().getName()))
            event.addTag("{magnata}", Config.TAG_MAGNATA);
    }
}
