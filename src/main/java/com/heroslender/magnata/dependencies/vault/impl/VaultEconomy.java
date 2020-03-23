package com.heroslender.magnata.dependencies.vault.impl;

import com.heroslender.magnata.Config;
import com.heroslender.magnata.dependencies.vault.Economy;
import com.heroslender.magnata.helpers.Account;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class VaultEconomy implements Economy {
    @Getter private final net.milkbowl.vault.economy.Economy economy;

    public VaultEconomy() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            throw new RuntimeException("Não foi possivel encontrar o Vault!");
        }
        economy = rsp.getProvider();

        if (economy == null) {
            throw new RuntimeException("Não foi possivel encontrar o Vault!");
        }
    }

    @Override
    public CompletableFuture<List<Account>> getAccounts() {
        return CompletableFuture.supplyAsync(() -> {
            OfflinePlayer[] playersToCheck = Config.ONLINE_ONLY
                    ? Bukkit.getOnlinePlayers().toArray(new OfflinePlayer[0])
                    : Bukkit.getServer().getOfflinePlayers();

            return Arrays.stream(playersToCheck)
                    .map(this::getAccount)
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<Account> getAccount(String playerName) {
        return getAccount(CompletableFuture.supplyAsync(() -> Bukkit.getOfflinePlayer(playerName)));
    }

    @Override
    public CompletableFuture<Account> getAccount(OfflinePlayer player) {
        return getAccount(CompletableFuture.completedFuture(player));
    }

    @Override
    public CompletableFuture<Account> getAccount(CompletableFuture<OfflinePlayer> accountFuture) {
        return accountFuture.thenApply(offlinePlayer -> {
            try {
                return new Account(offlinePlayer.getName(), economy.getBalance(offlinePlayer));
            } catch (Exception e) {
                return null;
            }
        });
    }
}
