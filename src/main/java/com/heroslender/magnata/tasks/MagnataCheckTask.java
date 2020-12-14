package com.heroslender.magnata.tasks;

import com.heroslender.magnata.Config;
import com.heroslender.magnata.HeroMagnata;
import com.heroslender.magnata.api.events.MagnataChangeEvent;
import com.heroslender.magnata.dependencies.vault.Economy;
import com.heroslender.magnata.helpers.Account;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.logging.Level;

@RequiredArgsConstructor
public class MagnataCheckTask implements Runnable {
    private final Economy economy;

    @Override
    public void run() {
        HeroMagnata.getInstance().getLogger().info("§6Verificando se existe um novo magnata...");
        long milliseconds = System.currentTimeMillis();

        if (Bukkit.getOnlinePlayers().size() < 1) {
            HeroMagnata.getInstance().getLogger().warning("Sem jogadores online no servidor, ignorando verificacao de novo magnata!");
            return;
        }

        economy.getAccounts()
                .thenApply(this::sortDesc)
                .thenApply(accounts -> accounts.get(0))
                .whenComplete((novoMagnata, throwable) -> {
                    try {
                        if (throwable != null) {
                            HeroMagnata.getInstance().getLogger().log(Level.WARNING, "Ocurreu um erro ao atualizar o magnata atual!", throwable);
                            return;
                        }

                        if (novoMagnata == null) {
                            HeroMagnata.getInstance().getLogger().warning("Ocurreu um erro ao verificar o novo Magnata. O magnata é null?!? '-'");
                            return;
                        }

                        HeroMagnata.getInstance().getLogger()
                                .info("[LOG] Novo magnata encontrado: " + novoMagnata.toString() + "; Antigo: " + HeroMagnata.getInstance().getMagnata());
                        if (!novoMagnata.getPlayer().equals(HeroMagnata.getInstance().getMagnata())) {
                            HeroMagnata.getInstance().getLogger().log(Level.INFO, "§aSetando novo magnata para {0}!", novoMagnata.getPlayer());
                            Account magnataAntigo = HeroMagnata.getInstance().getMagnataAccount().join();

                            HeroMagnata.getInstance().setMagnata(novoMagnata.getPlayer());

                            MagnataChangeEvent magnataChangeEvent = new MagnataChangeEvent(novoMagnata, magnataAntigo);
                            Bukkit.getServer().getPluginManager().callEvent(magnataChangeEvent);

                            if (Config.ACOES_ATIVAR != null && !Config.ACOES_ATIVAR.isEmpty()) {
                                Config.ACOES_ATIVAR.forEach(acaoMagnata -> acaoMagnata.executarComando(novoMagnata, magnataAntigo));
                            }

                        } else {
                            HeroMagnata.getInstance().getLogger().info("Não tem um novo magnata :(");
                        }
                    } catch (Exception e) {
                        HeroMagnata.getInstance().getLogger().log(
                                Level.SEVERE,
                                "Ocurreu um erro ao atualizar o magnata:",
                                e
                        );
                    } finally {
                        HeroMagnata.getInstance().getLogger().info("[LOG] Executada a task de verificar o magnata.(" + (-milliseconds + System.currentTimeMillis()) + "ms)");
                    }
                });
    }

    private List<Account> sortDesc(List<Account> accounts) {
        HeroMagnata.getInstance().getLogger().log(Level.INFO, "§6Verificando {0} contas...", accounts.size());
        accounts.sort((acc1, acc2) -> Double.compare(acc2.getMoney(), acc1.getMoney()));
        return accounts;
    }
}
