package com.heroslender.HeroMagnata;

import com.heroslender.HeroMagnata.API.Eventos.MagnataChangeEvent;
import com.heroslender.HeroMagnata.Utils.Exceptions.HeroException;
import com.heroslender.HeroMagnata.Utils.Metrics;
import com.heroslender.HeroMagnata.Utils.NumberUtils;
import com.heroslender.HeroMagnata.dependencias.CitizensSupport;
import com.heroslender.HeroMagnata.dependencias.LegendChatSupport;
import com.heroslender.HeroMagnata.dependencias.UChatSupport;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heroslender.
 */
public class HeroMagnata extends JavaPlugin implements Listener {

    @Getter private static HeroMagnata instance;
    @Getter
    @Setter
    private static String magnataAtual = " ";
    @Getter private static Economy econ = null;
    @Getter private static Chat chat = null;
    private CitizensSupport citizensSupport;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Config.init();

        // Conectar ao vault
        if (!setupVault()) {
            getLogger().severe(" - Falha ao ligar ao vault!");
            getServer().getPluginManager().disablePlugin(this);
        }

        // Suporte para tag no chat
        if (getServer().getPluginManager().getPlugin("Legendchat") != null)
            new LegendChatSupport();
        if (getServer().getPluginManager().getPlugin("UltimateChat") != null)
            new UChatSupport();

        // Verificar novo magnata ao ligar o server :)
        checkMagnata();

        // Inicializar o modulo de NPCs
        if (getServer().getPluginManager().getPlugin("Citizens") != null && getServer().getPluginManager().getPlugin("HolographicDisplays") != null)
            citizensSupport = new CitizensSupport();

        getServer().getPluginManager().registerEvents(this, this);

        // Metrics - https://bstats.org/plugin/bukkit/HeroMagnata
        new Metrics(this).submitData();
    }

    private boolean setupVault() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        RegisteredServiceProvider<Chat> rspChat = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (rsp == null || rspChat == null) {
            return false;
        }
        econ = rsp.getProvider();
        chat = rspChat.getProvider();
        return econ != null && chat != null;
    }

    @Override
    public void onDisable() {
        if (citizensSupport != null)
            citizensSupport.disable();
    }

    private void checkMagnata() {
        getServer().getScheduler().runTaskTimerAsynchronously(this, this::forceCheckNovo, 20L, Config.DELAY_ATUALIZAR * 20L);
    }

    private void forceCheckNovo() {
        try {
            getLogger().info("§6Verificando se existe um novo magnata...");
            Long miliseconds = System.currentTimeMillis();
            if (Bukkit.getOnlinePlayers().size() < 1)
                throw new HeroException("Sem jogadores online no servidor, ignorando verificacao de novo magnata!");

            List<Account> accounts = new ArrayList<>();
            int erros = 0;
            for (OfflinePlayer player : (Config.ONLINE_ONLY ? Bukkit.getOnlinePlayers().toArray(new OfflinePlayer[0]) : Bukkit.getServer().getOfflinePlayers())) {
                try {
                    accounts.add(new Account(player.getName(), econ.getBalance(player)));
                } catch (Exception e) {
                    erros++;
                }
            }
            if (erros != 0)
                getLogger().warning("Ocorreu um erro ao checkar o saldo de " + erros + " jogadores!");

            getLogger().info("A verificar o magnata dentro de " + accounts.size() + " jogadores.");
            Account novoMagnata = null;
            for (Account account : accounts) {
                if (novoMagnata == null) novoMagnata = account;
                else if (account.getMoney() > novoMagnata.getMoney()) novoMagnata = account;
            }

            if (novoMagnata == null)
                throw new HeroException("Ocurreu um erro ao verificar o novo Magnata. Sem jogadores registados no servidor.");

            getLogger().info("[LOG] Magnata verificado.(" + (-miliseconds + System.currentTimeMillis()) + "ms)");
            if (!novoMagnata.getPlayer().equals(magnataAtual)) {
                Account magnataNovo = novoMagnata;
                Account magnataAntigo = new Account(getMagnataAtual(), econ.getBalance(getMagnataAtual()));
                MagnataChangeEvent magnataChangeEvent = new MagnataChangeEvent(magnataNovo, magnataAntigo);
                getServer().getPluginManager().callEvent(magnataChangeEvent);

                if (Config.ACOES_ATIVAR != null && !Config.ACOES_ATIVAR.isEmpty()) {
                    Config.ACOES_ATIVAR.forEach(acaoMagnata -> acaoMagnata.executarComando(magnataNovo, magnataAntigo));
                }

                magnataAtual = novoMagnata.getPlayer();
                getConfig().set("magnata-atual", magnataAtual);
                saveConfig();
            } else
                getLogger().info("Não tem um novo magnata :(");
        } catch (HeroException ex) {
            Bukkit.getLogger().warning(ex.getMessage());
        } catch (Exception erro) {
            erro.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("magnata")) {
            if (args.length > 0 && sender.hasPermission("magnata.admin")) {
                if (args[0].equalsIgnoreCase("atualizar")) {
                    getServer().getScheduler().runTaskAsynchronously(this, this::forceCheckNovo);
                    sender.sendMessage("§aMagnata checado!");
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
                    .replace("{novo_saldo}", NumberUtils.format(econ.getBalance(magnataAtual, "")))
                    .replace("{novo_saldo_short}", NumberUtils.formatShort(econ.getBalance(magnataAtual, "")));
            // Prevenir NullPointerException do LuckPerms
            try {
                msg = msg
                        .replace("{novo_prefix}", HeroMagnata.getChat().getPlayerPrefix((String) null, magnataAtual))
                        .replace("{novo_suffix}", HeroMagnata.getChat().getPlayerSuffix((String) null, magnataAtual));
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
            if (Config.ACOES_ENTRAR != null && !Config.ACOES_ENTRAR.isEmpty())
                Config.ACOES_ENTRAR.forEach(acaoMagnata -> acaoMagnata.executarComando(new Account(getMagnataAtual(), econ.getBalance(getMagnataAtual())), new Account("", 0.0D)));
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        if (e.getPlayer().getName().equals(magnataAtual))
            if (Config.ACOES_SAIR != null && !Config.ACOES_SAIR.isEmpty())
                Config.ACOES_SAIR.forEach(acaoMagnata -> acaoMagnata.executarComando(new Account(getMagnataAtual(), econ.getBalance(getMagnataAtual())), new Account("", 0.0D)));
    }
}
