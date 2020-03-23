package com.heroslender.magnata.dependencies.vault;

import com.heroslender.magnata.helpers.Account;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Economy {
    public CompletableFuture<List<Account>> getAccounts();

    public CompletableFuture<Account> getAccount(final String playerName);

    public CompletableFuture<Account> getAccount(final OfflinePlayer player);

    public CompletableFuture<Account> getAccount(final CompletableFuture<OfflinePlayer> player);
}
