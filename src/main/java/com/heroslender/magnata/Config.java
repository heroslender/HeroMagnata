package com.heroslender.magnata;

import com.heroslender.magnata.action.Action;
import com.heroslender.magnata.action.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Heroslender.
 */
public class Config {
    private static final Pattern RANKUP_ACTION_MATCHER = Pattern.compile("([\\[][a-zA-Z_]+[]])");

    public static String TAG_MAGNATA;
    public static String COMANDO_MAGNATA;
    public static List<Action> ACOES_ATIVAR;
    public static List<Action> ACOES_ENTRAR;
    public static List<Action> ACOES_SAIR;
    public static int DELAY_ATUALIZAR;
    public static boolean ONLINE_ONLY;
    public static String[] MONEY_TAGS;

    public static void init() {
        verificaConfig();
        loadConfig();
    }

    public static void verificaConfig() {
        FileConfiguration config = HeroMagnata.getInstance().getConfig();
        if (!config.contains("apenas-online"))
            config.set("apenas-online", true);
        if (!config.contains("legendchat-tag"))
            config.set("legendchat-tag", "&2[MAGNATA] ");
        if (!config.contains("comando-magnata"))
            config.set("comando-magnata", "&2[$] &aMagnata atual: &7{novo_prefix}{novo_nome}{novo_suffix} &8- &e{novo_saldo}");
        if (!config.contains("action.ativar"))
            config.set("action.ativar", Collections.singletonList("[broadcast] &2[Magnata] &a&l{novo_prefix}{novo_nome} &a\\u00E9 o novo magnata com um balan\\u00E7o de &a&l${novo_saldo}"));
        checkFor1Dot3("action.ativar");
        if (!config.contains("action.entrar"))
            config.set("action.entrar", Collections.singletonList("[broadcast] &2[Magnata] &a&l{novo_nome} &aentrou no servidor!"));
        checkFor1Dot3("action.entrar");
        if (!config.contains("action.sair"))
            config.set("action.sair", Collections.singletonList("[broadcast] &2[Magnata] &a&l{novo_nome} &asaiu do servidor"));
        checkFor1Dot3("action.sair");
        if (!config.contains("delay-atualizar"))
            config.set("delay-atualizar", 300);
        if (!config.contains("money-tags"))
            config.set("money-tags", "K;M;B;T;Q");
        if (!config.contains("magnata-atual"))
            config.set("magnata-atual", Bukkit.getOfflinePlayers()[0].getName());

        HeroMagnata.getInstance().saveConfig();
    }

    private static void checkFor1Dot3(String path) {
        List<String> tempUpdate = new ArrayList<>();
        for (String string : HeroMagnata.getInstance().getConfig().getStringList(path)) {
            if (string.contains("\n"))
                string = string.replace("\n", "{NL}");
            tempUpdate.add(string);
        }
        HeroMagnata.getInstance().getConfig().set(path, tempUpdate);
    }

    public static void loadConfig() {
        FileConfiguration config = HeroMagnata.getInstance().getConfig();
        HeroMagnata.getInstance().setMagnataAtual(config.getString("magnata-atual", Bukkit.getOfflinePlayers()[0].getName()));
        ONLINE_ONLY = config.getBoolean("apenas-online", true);
        TAG_MAGNATA = config.getString("legendchat-tag", "&2[MAGNATA] ");
        COMANDO_MAGNATA = config.getString("comando-magnata", "&2[$] &aMagnata atual: &7{novo_nome} &8- &e{novo_saldo}");
        ACOES_ATIVAR = new ArrayList<>();
        ACOES_ATIVAR = getAcoes(config.getStringList("action.ativar"));
        ACOES_ENTRAR = new ArrayList<>();
        ACOES_ENTRAR = getAcoes(config.getStringList("action.entrar"));
        ACOES_SAIR = new ArrayList<>();
        ACOES_SAIR = getAcoes(config.getStringList("action.sair"));
        DELAY_ATUALIZAR = config.getInt("delay-atualizar", 300);
        MONEY_TAGS = config.getString("money-tags", "K;M;B;T;Q").split(";");
    }

    private static List<Action> getAcoes(List<String> lista) {
        HeroMagnata plugin = HeroMagnata.getInstance();
        List<Action> retorno = new ArrayList<>();
        for (String linhaExecutavel : lista) {
            if (!linhaExecutavel.contains(" ")) {
                plugin.getLogger().warning("Ação invalida: '" + linhaExecutavel + "'!");
                plugin.getLogger().info("Formato correto: [acao] <execução>");
            } else {
                String acao = linhaExecutavel.split(" ")[0];
                if (acao.isEmpty()) {
                    plugin.getLogger().warning("Ação invalida: '" + linhaExecutavel + "'!");
                    plugin.getLogger().info("Formato correto: [acao] <execução>");
                } else {
                    Matcher matcher = RANKUP_ACTION_MATCHER.matcher(acao);
                    if (matcher.find()) {
                        ActionType acaoTipo = ActionType.fromIdentifier(matcher.group(1));
                        if (acaoTipo == null) {
                            plugin.getLogger().warning("Ação invalida: '" + linhaExecutavel + "'!");
                        } else {
                            String execucao = linhaExecutavel.replace(matcher.group(1) + " ", "");
                            if (execucao.isEmpty()) {
                                plugin.getLogger().warning("Ação invalida: '" + linhaExecutavel + "' não tem uma execução!");
                            } else {
                                switch (acaoTipo) {
                                    case TITLE_BROADCAST:
                                    case TITLE_NOVO:
                                    case TITLE_ANTIGO:
                                        if (!execucao.contains("{NL}"))
                                            execucao += "{NL} ";
                                        break;
                                }
                                retorno.add(new Action(acaoTipo, execucao));
                            }
                        }
                    } else {
                        plugin.getLogger().warning("Ação invalida: '" + linhaExecutavel + "'!");
                        plugin.getLogger().info("Formato correto: [acao] <execução>");
                    }
                }
            }
        }
        return retorno;
    }
}
