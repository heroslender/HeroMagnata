package com.heroslender.magnata;

import com.heroslender.magnata.commands.MagnataCommand;
import com.heroslender.magnata.dependencies.CitizensSupport;
import com.heroslender.magnata.dependencies.LegendChatSupport;
import com.heroslender.magnata.dependencies.NameTagEditSupport;
import com.heroslender.magnata.dependencies.UChatSupport;
import com.heroslender.magnata.dependencies.placeholderapi.PAPISupport;
import com.heroslender.magnata.dependencies.vault.Economy;
import com.heroslender.magnata.dependencies.vault.Permissions;
import com.heroslender.magnata.dependencies.vault.impl.VaultEconomy;
import com.heroslender.magnata.dependencies.vault.impl.VaultPermissions;
import com.heroslender.magnata.helpers.Account;
import com.heroslender.magnata.tasks.MagnataCheckTask;
import com.heroslender.magnata.utils.Metrics;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * Created by Heroslender.
 */
public class HeroMagnata extends JavaPlugin implements Listener {
    @Getter private static HeroMagnata instance;
    @Getter private String magnata = " ";

    @Getter private Economy economy;
    @Getter private Permissions permissions;
    @Getter private CitizensSupport citizensSupport;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Config.init();

        this.economy = new VaultEconomy();
        this.permissions = new VaultPermissions(this);

        // Suporte para tag no chat
        if (getServer().getPluginManager().getPlugin("Legendchat") != null)
            new LegendChatSupport();
        if (getServer().getPluginManager().getPlugin("UltimateChat") != null)
            new UChatSupport();


        if (getServer().getPluginManager().getPlugin("NameTagEdit") != null)
            new NameTagEditSupport();
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PAPISupport();

        // Verificar novo magnata ao ligar o server :)
        getServer().getScheduler().runTaskTimerAsynchronously(this, new MagnataCheckTask(getEconomy()), 20L, Config.DELAY_ATUALIZAR * 20L);

        // Inicializar o modulo de NPCs
        if (getServer().getPluginManager().isPluginEnabled("Citizens")
                && getServer().getPluginManager().isPluginEnabled("HolographicDisplays"))
            citizensSupport = new CitizensSupport(this);

        getServer().getPluginManager().registerEvents(this, this);

        getServer().getPluginCommand("magnata").setExecutor(new MagnataCommand());

        // Metrics - https://bstats.org/plugin/bukkit/HeroMagnata
        new Metrics(this).submitData();

        if (citizensSupport != null) {
            citizensSupport.enable();
        }
    }

    @Override
    public void onDisable() {
        if (citizensSupport != null) {
            citizensSupport.disable();
        }
    }

    public CompletableFuture<Account> getMagnataAccount() {
        return getEconomy().getAccount(getMagnata());
    }

    public void setMagnata(String magnata) {
        this.magnata = magnata;
        getConfig().set("magnata-atual", magnata);
        saveConfig();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getName().equals(magnata) && Config.ACOES_ENTRAR != null && !Config.ACOES_ENTRAR.isEmpty()) {
            economy.getAccount(e.getPlayer()).whenComplete((account, throwable) -> {
                if (throwable != null) {
                    getLogger().log(Level.SEVERE, "Ocurreu um erro ao ver a conta do magnata atual.", throwable);
                    return;
                }

                Config.ACOES_ENTRAR.forEach(acaoMagnata -> acaoMagnata.executarComando(account, Account.getEmpty()));
            });
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        if (e.getPlayer().getName().equals(magnata) && Config.ACOES_SAIR != null && !Config.ACOES_SAIR.isEmpty()) {
            economy.getAccount(e.getPlayer()).whenComplete((account, throwable) -> {
                if (throwable != null) {
                    getLogger().log(Level.SEVERE, "Ocurreu um erro ao ver a conta do magnata atual.", throwable);
                    return;
                }

                Config.ACOES_SAIR.forEach(acaoMagnata -> acaoMagnata.executarComando(account, Account.getEmpty()));
            });
        }
    }
}
