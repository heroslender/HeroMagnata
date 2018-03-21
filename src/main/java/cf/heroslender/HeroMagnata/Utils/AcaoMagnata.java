package cf.heroslender.HeroMagnata.Utils;

import cf.heroslender.HeroMagnata.Account;
import cf.heroslender.HeroMagnata.HeroMagnata;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Heroslender.
 */

@Data
public class AcaoMagnata {
    private String execucao;
    private AcaoMagnataTipo type;

    public AcaoMagnata(AcaoMagnataTipo tipo, String execucao) {
        this.setType(tipo);
        this.setExecucao(ChatColor.translateAlternateColorCodes('&', execucao));
    }

    public void executarComando(Account magnataNovo, Account magnataAntigo) {
        switch (type) {
            case CONSOLE_COMMAND:
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replacePlaceholders(magnataNovo, magnataAntigo));
                break;
            case COMMAND_NEW:
                Player player1 = Bukkit.getPlayerExact(magnataNovo.getPlayer());
                if (player1 != null)
                    player1.performCommand(replacePlaceholders(magnataNovo, magnataAntigo));
                break;
            case COMMAND_OLDER:
                Player player2 = Bukkit.getPlayerExact(magnataAntigo.getPlayer());
                if (player2 != null)
                    player2.performCommand(replacePlaceholders(magnataNovo, magnataAntigo));
                break;
            case BROADCAST:
                Bukkit.getServer().broadcastMessage(replacePlaceholders(magnataNovo, magnataAntigo));
                break;
            case MENSAGEM_ANTIGO:
                Player player3 = Bukkit.getPlayerExact(magnataAntigo.getPlayer());
                if (player3 != null)
                    player3.sendMessage(replacePlaceholders(magnataNovo, magnataAntigo).split("\n"));
                break;
            case MENSAGEM_NOVO:
                Player player4 = Bukkit.getPlayerExact(magnataNovo.getPlayer());
                if (player4 != null)
                    player4.sendMessage(replacePlaceholders(magnataNovo, magnataAntigo).split("\n"));
                break;
            case TITLE_BROADCAST:
                TitleAPI.broadcastTitle(replacePlaceholders(magnataNovo, magnataAntigo));
                break;
            case TITLE_ANTIGO:
                Player player5 = Bukkit.getPlayerExact(magnataAntigo.getPlayer());
                if (player5 != null)
                    TitleAPI.sendTitle(player5, replacePlaceholders(magnataNovo, magnataAntigo));
                break;
            case TITLE_NOVO:
                Player player6 = Bukkit.getPlayerExact(magnataNovo.getPlayer());
                if (player6 != null)
                    TitleAPI.sendTitle(player6, replacePlaceholders(magnataNovo, magnataAntigo));
                break;
            case ACTION_BROADCAST:
                TitleAPI.sendActionBar(replacePlaceholders(magnataNovo, magnataAntigo), Bukkit.getOnlinePlayers().toArray(new Player[0]));
                break;
            case ACTION_ANTIGO:
                Player player7 = Bukkit.getPlayerExact(magnataAntigo.getPlayer());
                if (player7 != null)
                    TitleAPI.sendActionBar(replacePlaceholders(magnataNovo, magnataAntigo), player7);
                break;
            case ACTION_NOVO:
                Player player8 = Bukkit.getPlayerExact(magnataNovo.getPlayer());
                if (player8 != null)
                    TitleAPI.sendActionBar(replacePlaceholders(magnataNovo, magnataAntigo), player8);
                break;
            default:
                break;
        }
    }

    private String replacePlaceholders(Account magnataAtual, Account magnataAntigo) {
        return execucao.replace("{antigo_nome}", magnataAntigo.getPlayer())
                .replace("{antigo_saldo}", HeroMagnata.getMoneyFormated(magnataAntigo))
                .replace("{antigo_prefixo}", HeroMagnata.getChat().getPlayerPrefix((String) null, magnataAntigo.getPlayer()))
                .replace("{novo_prefixo}", HeroMagnata.getChat().getPlayerPrefix((String) null, magnataAtual.getPlayer()))
                .replace("{novo_nome}", magnataAtual.getPlayer())
                .replace("{novo_saldo}", HeroMagnata.getMoneyFormated(magnataAtual));
    }
}