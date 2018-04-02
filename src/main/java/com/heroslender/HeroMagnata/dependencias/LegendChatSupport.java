package com.heroslender.HeroMagnata.dependencias;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.heroslender.HeroMagnata.Config;
import com.heroslender.HeroMagnata.HeroMagnata;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Heroslender.
 */
public class LegendChatSupport implements Listener {

    public LegendChatSupport() {
        Bukkit.getLogger().info("[HeroMagnata] Legendchat detetado!");
        Bukkit.getPluginManager().registerEvents(this, HeroMagnata.getInstance());
    }

    @EventHandler
    private void onChat(ChatMessageEvent e) {
        if (e.getTags().contains("magnata") && HeroMagnata.getMagnataAtual().equals(e.getSender().getName()))
            e.setTagValue("magnata", Config.TAG_MAGNATA);
    }
}
