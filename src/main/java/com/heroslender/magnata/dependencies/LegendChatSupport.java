package com.heroslender.magnata.dependencies;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.heroslender.magnata.Config;
import com.heroslender.magnata.HeroMagnata;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Heroslender.
 */
public class LegendChatSupport implements Listener {
    private static final String TAG_NAME = "magnata";

    public LegendChatSupport() {
        Bukkit.getLogger().info("[HeroMagnata] Legendchat detetado!");
        Bukkit.getPluginManager().registerEvents(this, HeroMagnata.getInstance());
    }

    @EventHandler
    private void onChat(ChatMessageEvent e) {
        Player sender = e.getSender();
        if (sender != null && HeroMagnata.getInstance().getMagnata().equals(sender.getName()) && e.getTags().contains(TAG_NAME)) {
            e.setTagValue(TAG_NAME, Config.TAG_MAGNATA);
        }
    }
}
