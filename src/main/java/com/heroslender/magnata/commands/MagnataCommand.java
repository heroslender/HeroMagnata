package com.heroslender.magnata.commands;

import com.heroslender.magnata.Config;
import com.heroslender.magnata.HeroMagnata;
import com.heroslender.magnata.dependencies.VaultUtils;
import com.heroslender.magnata.tasks.MagnataCheckTask;
import com.heroslender.magnata.utils.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MagnataCommand implements CommandExecutor {
    private final HeroMagnata heroMagnata = HeroMagnata.getInstance();
    private final VaultUtils vaultUtils = heroMagnata.getVaultUtils();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("magnata")) {
            if (args.length > 0 && sender.hasPermission("magnata.admin")) {
                if (args[0].equalsIgnoreCase("atualizar")) {
                    Bukkit.getScheduler().runTaskAsynchronously(heroMagnata, new MagnataCheckTask());
                    sender.sendMessage("§aChecando...");
                    sender.sendMessage("§eVerifique a console do servidor para mais informações.");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    heroMagnata.reloadConfig();
                    Config.verificaConfig();
                    Config.loadConfig();
                    if (heroMagnata.getCitizensSupport() != null)
                        heroMagnata.getCitizensSupport().reload();
                    sender.sendMessage("§aConfig recarregada!");
                    return true;
                }
                if (args[0].equalsIgnoreCase("npc")) {
                    if (heroMagnata.getCitizensSupport() != null) {
                        heroMagnata.getCitizensSupport().criarNPC(((Player) sender).getLocation());
                        sender.sendMessage("§aNPC criado!");
                        return true;
                    }
                    sender.sendMessage("§cNão é possivel criar um NPC, verifica se tens o §7Citizens §ce o §7Holografic Displays §cno servidor.");
                    return true;
                }
            }

            String magnataAtual = heroMagnata.getMagnataAtual();
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
}
