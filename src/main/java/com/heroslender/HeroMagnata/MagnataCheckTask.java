package com.heroslender.HeroMagnata;

import com.heroslender.HeroMagnata.API.Eventos.MagnataChangeEvent;
import com.heroslender.HeroMagnata.vault.VaultUtils;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class MagnataCheckTask implements Runnable {
    @Override
    public void run() {
        final VaultUtils vaultUtils = HeroMagnata.getInstance().getVaultUtils();

        HeroMagnata.getInstance().getLogger().info("§6Verificando se existe um novo magnata...");
        long miliseconds = System.currentTimeMillis();

        if (Bukkit.getOnlinePlayers().size() < 1) {
            HeroMagnata.getInstance().getLogger().warning("Sem jogadores online no servidor, ignorando verificacao de novo magnata!");
            return;
        }

        CompletableFuture
                .supplyAsync(() -> vaultUtils.getAccounts().join())
                .thenApply(this::sortDesc)
                .thenApply(accounts -> accounts.get(0))
                .whenCompleteAsync((novoMagnata, throwable) -> {
                    if (throwable != null) {
                        HeroMagnata.getInstance().getLogger().log(Level.WARNING, "Ocurreu um erro ao atualizar o magnata atual!", throwable);
                        return;
                    }

                    if (novoMagnata == null) {
                        HeroMagnata.getInstance().getLogger().warning("Ocurreu um erro ao verificar o novo Magnata. O magnata é null?!? '-'");
                        return;
                    }

                    if (!novoMagnata.getPlayer().equals(HeroMagnata.getInstance().getMagnataAtual())) {
                        Account magnataAntigo = vaultUtils
                                .getAccount(HeroMagnata.getInstance().getMagnataAtual())
                                .join()
                                .orElse(new Account("Inexistente", 0));

                        MagnataChangeEvent magnataChangeEvent = new MagnataChangeEvent(novoMagnata, magnataAntigo);
                        Bukkit.getServer().getPluginManager().callEvent(magnataChangeEvent);

                        if (Config.ACOES_ATIVAR != null && !Config.ACOES_ATIVAR.isEmpty()) {
                            Config.ACOES_ATIVAR.forEach(acaoMagnata -> acaoMagnata.executarComando(novoMagnata, magnataAntigo));
                        }

                        HeroMagnata.getInstance().setMagnataAtual(novoMagnata.getPlayer());
                    } else {
                        HeroMagnata.getInstance().getLogger().info("Não tem um novo magnata :(");
                    }
                });
        HeroMagnata.getInstance().getLogger().info("[LOG] Executada a task de verificar o magnata.(" + (-miliseconds + System.currentTimeMillis()) + "ms)");
    }

    private List<Account> sortDesc(List<Account> accounts) {
        accounts.sort((acc1, acc2) -> Double.compare(acc2.getMoney(), acc1.getMoney()));
        return accounts;
    }
}
