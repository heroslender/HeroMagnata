package com.heroslender.magnata.dependencies.vault.impl;

import com.heroslender.magnata.dependencies.vault.Permissions;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPermissions implements Permissions {
    @Getter private final Chat chat;

    public VaultPermissions() {
        RegisteredServiceProvider<Chat> rspChat = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (rspChat == null) {
            throw new RuntimeException("Não foi possivel encontrar o Vault!");
        }

        chat = rspChat.getProvider();

        if (chat == null) {
            throw new RuntimeException("Não foi possivel encontrar o Vault!");
        }
    }

    @Override
    public String getPrefix(String playerName) {
        // Prevenir NullPointerException do LuckPerms
        try {
            return chat.getPlayerPrefix((String) null, playerName);
        } catch (Exception ignored) {
            return "";
        }
    }

    @Override
    public String getSuffix(String playerName) {
        // Prevenir NullPointerException do LuckPerms
        try {
            return chat.getPlayerSuffix((String) null, playerName);
        } catch (Exception ignored) {
            return "";
        }
    }
}
