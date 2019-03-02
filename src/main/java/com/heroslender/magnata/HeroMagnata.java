package com.heroslender.magnata;

import com.heroslender.magnata.dependencies.CitizensSupport;
import com.heroslender.magnata.dependencies.LegendChatSupport;
import com.heroslender.magnata.dependencies.UChatSupport;
import com.heroslender.magnata.helpers.Account;
import com.heroslender.magnata.tasks.MagnataCheckTask;
import com.heroslender.magnata.utils.Metrics;
import com.heroslender.magnata.utils.NumberUtils;
import com.heroslender.magnata.dependencies.VaultUtils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * Created by Heroslender.
 */
public class HeroMagnata extends JavaPlugin implements Listener {
    @Getter private static HeroMagnata instance;
    @Getter private String magnataAtual = " ";
    @Getter private VaultUtils vaultUtils;
    private CitizensSupport citizensSupport;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Config.init();

        this.vaultUtils = new VaultUtils();

        // Suporte para tag no chat
        if (getServer().getPluginManager().getPlugin("Legendchat") != null)
            new LegendChatSupport();
        if (getServer().getPluginManager().getPlugin("UltimateChat") != null)
            new UChatSupport();

        // Verificar novo magnata ao ligar o server :)
        getServer().getScheduler().runTaskTimerAsynchronously(this, MagnataCheckTask::new, 20L, Config.DELAY_ATUALIZAR * 20L);

        // Inicializar o modulo de NPCs
        if (getServer().getPluginManager().getPlugin("Citizens") != null && getServer().getPluginManager().getPlugin("HolographicDisplays") != null)
            citizensSupport = new CitizensSupport();

        getServer().getPluginManager().registerEvents(this, this);

        // Metrics - https://bstats.org/plugin/bukkit/HeroMagnata
        new Metrics(this).submitData();
    }

    @Override
    public void onDisable() {
        if (citizensSupport != null)
            citizensSupport.disable();
    }

    public void setMagnataAtual(String magnataAtual) {
        this.magnataAtual = magnataAtual;
        getConfig().set("magnata-atual", magnataAtual);
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("magnata")) {
            if (args.length > 0 && sender.hasPermission("magnata.admin")) {
                if (args[0].equalsIgnoreCase("atualizar")) {
                    getServer().getScheduler().runTaskAsynchronously(this, MagnataCheckTask::new);
                    sender.sendMessage("§aChecando... §eVerifique a console do servidor para mais informações.");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    reloadConfig();
                    Config.verificaConfig();
                    Config.loadConfig();
                    if (citizensSupport != null)
                        citizensSupport.reload();
                    sender.sendMessage("§aConfig recarregada!");
                    return true;
                }
                if (args[0].equalsIgnoreCase("npc")) {
                    if (citizensSupport != null) {
                        citizensSupport.criarNPC(((Player) sender).getLocation());
                        sender.sendMessage("§aNPC criado!");
                        return true;
                    }
                    sender.sendMessage("§cNão é possivel criar um NPC, verifica se tens o §7Citizens §ce o §7Holografic Displays §cno servidor.");
                    return true;
                }
            }
            String msg = Config.COMANDO_MAGNATA
                    .replace("{novo_nome}", magnataAtual)
                    .replace("{novo_saldo}", NumberUtils.format(vaultUtils.getEconomy().getBalance(magnataAtual, "")))
                    .replace("{novo_saldo_short}", NumberUtils.formatShort(vaultUtils.getEconomy().getBalance(magnataAtual, "")));
            // Prevenir NullPointerException do LuckPerms
            try {
                msg = msg
                        .replace("{novo_prefix}", vaultUtils.getChat().getPlayerPrefix((String) null, magnataAtual))
                        .replace("{novo_suffix}", vaultUtils.getChat().getPlayerSuffix((String) null, magnataAtual));
            } catch (Exception ignored) {
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return true;
        }
        return false;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getName().equals(magnataAtual))
            if (Config.ACOES_ENTRAR != null && !Config.ACOES_ENTRAR.isEmpty()) {
                Optional<Account> magnata = vaultUtils.getAccount(getMagnataAtual()).join();
                magnata.ifPresent(account -> {
                    Config.ACOES_ENTRAR.forEach(acaoMagnata -> acaoMagnata.executarComando(account, Account.getEmpty()));
                });
            }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        if (e.getPlayer().getName().equals(magnataAtual))
            if (Config.ACOES_SAIR != null && !Config.ACOES_SAIR.isEmpty()) {
                Optional<Account> magnata = vaultUtils.getAccount(getMagnataAtual()).join();
                magnata.ifPresent(account -> {
                    Config.ACOES_SAIR.forEach(acaoMagnata -> acaoMagnata.executarComando(account, Account.getEmpty()));
                });
            }
    }
}
