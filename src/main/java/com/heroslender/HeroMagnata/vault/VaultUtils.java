package com.heroslender.HeroMagnata.vault;

import com.heroslender.HeroMagnata.Account;
import com.heroslender.HeroMagnata.Config;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class VaultUtils {
    @Getter private final Economy economy;
    @Getter private final Chat chat;

    public VaultUtils() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        RegisteredServiceProvider<Chat> rspChat = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (rsp == null || rspChat == null) {
            throw new RuntimeException("Não foi possivel encontrar o Vault!");
        }
        economy = rsp.getProvider();
        chat = rspChat.getProvider();

        if (economy == null || chat == null) {
            throw new RuntimeException("Não foi possivel encontrar o Vault!");
        }
    }

    public CompletableFuture<List<Account>> getAccounts() {
        return CompletableFuture.supplyAsync(() -> {
            OfflinePlayer[] playersToCheck = Config.ONLINE_ONLY
                    ? Bukkit.getOnlinePlayers().toArray(new OfflinePlayer[0])
                    : Bukkit.getServer().getOfflinePlayers();

            return Arrays.stream(playersToCheck)
                    .map(this::getAccount)
                    .map(CompletableFuture::join)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        });
    }

    public CompletableFuture<Optional<Account>> getAccount(final String playerName) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

        return getAccount(offlinePlayer);
    }

    public CompletableFuture<Optional<Account>> getAccount(final OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Optional.of(new Account(player.getName(), economy.getBalance(player)));
            } catch (Exception e) {
                return Optional.empty();
            }
        });
    }
}
