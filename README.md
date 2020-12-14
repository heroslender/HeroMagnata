<img src="https://avatars1.githubusercontent.com/u/16785313?s=96&v=4" alt="Heroslender" title="Heroslender" align="right" height="96" width="96"/>

# HeroMagnata

[![GitHub stars](https://img.shields.io/github/stars/heroslender/HeroMagnata.svg)](https://github.com/heroslender/HeroMagnata/stargazers)
[![bStats Servers](https://img.shields.io/bstats/servers/1621.svg?color=1bcc1b)](https://bstats.org/plugin/bukkit/HeroMagnata)
[![GitHub All Releases](https://img.shields.io/github/downloads/heroslender/HeroMagnata/total.svg?logoColor=fff)](https://github.com/heroslender/HeroMagnata/releases/latest)
[![GitHub issues](https://img.shields.io/github/issues-raw/heroslender/HeroMagnata.svg?label=issues)](https://github.com/heroslender/HeroMagnata/issues)
[![GitHub last commit](https://img.shields.io/github/last-commit/heroslender/HeroMagnata.svg)](https://github.com/heroslender/HeroMagnata/commit)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5239a5b8f99b4ed49902b6aaee63f1b2)](https://app.codacy.com/app/heroslender/HeroMagnata?utm_source=github.com&utm_medium=referral&utm_content=heroslender/HeroMagnata&utm_campaign=Badge_Grade_Dashboard)
[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)

Um plugin de magnata, com sistema de npc, title, actionbar, etc!

![Preview](https://github.com/heroslender/HeroMagnata/raw/master/assets/preview_npc.png)
![Preview](https://github.com/heroslender/HeroMagnata/raw/master/assets/preview.png)
![Preview](https://github.com/heroslender/HeroMagnata/raw/master/assets/preview_title.png)

## Comandos

  - `/magnata` - Ver o magnata atual;
  - `/maganta npc` - Criar o NPC de magnata;
    > Requer `Citizens` e `HolographicDisplays` no servidor.
  - `/maganta atualizar` - Forçar a atualização do magnata;
  - `/magnata reload` - Recarregar a configuração do plugin.

## Placeholders - *PlaceholderAPI* 

  - `%heromagnata_magnata%` - O magnata atual;
  - `%heromagnata_magnata_money%` - O money do magnata atual, sem formatação;
  - `%heromagnata_magnata_money_formatted%` - O money do magnata atual, formatado;
  - `%heromagnata_magnata_tag%` - A tag de magnata, se o player for o magnata;
  - `%heromagnata_magnata_tag_chat%` - A tag de magnata no chat, se o player for o magnata.

## Config

```yaml
apenas-online: false
legendchat-tag: '&2[MAGNATA] '
comando-magnata: '&2[$] &aMagnata atual: &7{novo_prefix}{novo_nome}{novo_suffix} &8- &e{novo_saldo}'
acoes:
  ativar:
    - '[broadcast] &r'
    - '[broadcast] &2[Magnata] &a&l{novo_prefix}{novo_nome} &aE o novo magnata com um balanco de &a&l${novo_saldo}'
    - '[broadcast] &r'
    - '[title_broadcast] &2{novo_nome}{NL}&aE o novo magnata!'
  entrar:
    - '[broadcast] &2[Magnata] &a&l{novo_nome} &aentrou no servidor!'
  sair:
    - '[broadcast] &2[Magnata] &a&l{novo_nome} &asaiu do servidor'
delay-atualizar: 900
money-tags: K;M;B;T;Q
```

### Ações

-   `[comando_consola]` - Executar um comando pela consola
-   `[comando_antigo]` - Fazer o magnata antigo executar um comando
-   `[comando_novo]` - Fazer o novo magnata executar um comando
-   `[broadcast]` - Enviar uma mensagem para o servidor
-   `[mensagem_antigo]` - Enviar uma mensagem para o magnata antigo
-   `[mensagem_novo]` - Enviar uma mensagem para o novo magnata
-   `[title_broadcast]` - Enviar um title para o servidor
-   `[title_antigo]` - Enviar um title para o magnata antigo
-   `[title_novo]` - Enviar um title para o novo magnata
-   `[actionbar_broadcast]` - Enviar um title para o novo magnata
-   `[actionbar_antigo]` - Enviar um title para o novo magnata
-   `[actionbar_novo]` - Enviar um title para o novo magnata

#### Placeholders

-   `{antigo_nome}` - Nome do antigo magnata
-   `{antigo_prefix}` - Prefixo do antigo magnata
-   `{antigo_suffix}` - Sufixo do antigo magnata
-   `{antigo_saldo}` - Money do antigo magnata
-   `{antigo_saldo_short}` - Money formatado do antigo magnataa
-   `{novo_nome}` - Nome do magnata
-   `{novo_prefix}` - Prefixo do magnata
-   `{novo_suffix}` - Sufixo do magnata
-   `{novo_saldo}` - Money do magnata
-   `{novo_saldo_short}` - Money formatado do magnata
