package cf.heroslender.HeroMagnata.acoes;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Heroslender.
 */

@AllArgsConstructor
public enum AcaoMagnataTipo {
    CONSOLE_COMMAND("[comando_consola]"),
    COMMAND_OLDER("[comando_antigo]"),
    COMMAND_NEW("[comando_novo]"),
    BROADCAST("[broadcast]"),
    MENSAGEM_ANTIGO("[mensagem_antigo]"),
    MENSAGEM_NOVO("[mensagem_novo]"),
    TITLE_BROADCAST("[title_broadcast]"),
    TITLE_ANTIGO("[title_antigo]"),
    TITLE_NOVO("[title_novo]"),
    ACTION_BROADCAST("[actionbar_broadcast]"),
    ACTION_ANTIGO("[actionbar_antigo]"),
    ACTION_NOVO("[actionbar_novo]");

    @Getter
    private String identificador;

    public static AcaoMagnataTipo fromIdentifier(String identificador) {
        for (AcaoMagnataTipo a : values())
            if (a.getIdentificador().equals(identificador))
                return a;
        return null;
    }
}
