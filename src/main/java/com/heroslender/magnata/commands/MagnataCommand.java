package com.heroslender.magnata.commands;

import com.heroslender.magnata.Config;
import com.heroslender.magnata.HeroMagnata;
import com.heroslender.magnata.dependencies.vault.Economy;
import com.heroslender.magnata.dependencies.vault.Permissions;
import com.heroslender.magnata.tasks.MagnataCheckTask;
import com.heroslender.magnata.utils.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class MagnataCommand implements CommandExecutor {
    private final HeroMagnata heroMagnata = HeroMagnata.getInstance();
    private final Economy economy = heroMagnata.getEconomy();
    private final Permissions permissions = heroMagnata.getPermissions();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("magnata")) {
            if (args.length > 0 && sender.hasPermission("magnata.admin")) {
                if (args[0].equalsIgnoreCase("atualizar")) {
                    Bukkit.getScheduler().runTaskAsynchronously(heroMagnata, new MagnataCheckTask(economy));
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
                        heroMagnata.getCitizensSupport().createNPC(((Player) sender).getLocation());
                        sender.sendMessage("§aNPC criado!");
                        return true;
                    }
                    sender.sendMessage("§cNão é possivel criar um NPC, verifica se tens o §7Citizens §ce o §7Holografic Displays §cno servidor.");
                    return true;
                }
            }

            heroMagnata.getMagnataAccount()
                    .whenComplete((account, throwable) -> {
                        if (throwable != null) {
                            heroMagnata.getLogger().log(Level.SEVERE, "Ocurreu um erro ao ver a conta do magnata atual.", throwable);
                            return;
                        }

                        String msg = Config.COMANDO_MAGNATA
                                .replace("{novo_nome}", account.getPlayer())
                                .replace("{novo_saldo}", NumberUtils.format(account.getMoney()))
                                .replace("{novo_saldo_short}", NumberUtils.formatShort(account.getMoney()))
                                .replace("{novo_prefix}", permissions.getPrefix(account.getPlayer()))
                                .replace("{novo_suffix}", permissions.getSuffix(account.getPlayer()));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    });
            return true;
        }
        return false;
    }
}
