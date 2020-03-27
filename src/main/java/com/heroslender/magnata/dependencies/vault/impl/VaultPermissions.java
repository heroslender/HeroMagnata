package com.heroslender.magnata.dependencies.vault.impl;

import com.heroslender.magnata.dependencies.vault.Permissions;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

public class VaultPermissions implements Permissions {
    @Getter private final Chat chat;

    public VaultPermissions(Plugin plugin) {
        RegisteredServiceProvider<Chat> rspChat = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (rspChat != null) {
            chat = rspChat.getProvider();
        } else {
            chat = null;
        }

        if (chat == null) {
            plugin.getLogger().log(Level.SEVERE, "Não foi possivel encontrar o Vault para permissões!");
        }
    }

    @Override
    public String getPrefix(String playerName) {
        if (chat == null) {
            return "";
        }

        // Prevenir NullPointerException do LuckPerms
        try {
            return chat.getPlayerPrefix((String) null, playerName);
        } catch (Exception ignored) {
            return "";
        }
    }

    @Override
    public String getSuffix(String playerName) {
        if (chat == null) {
            return "";
        }

        // Prevenir NullPointerException do LuckPerms
        try {
            return chat.getPlayerSuffix((String) null, playerName);
        } catch (Exception ignored) {
            return "";
        }
    }
}
