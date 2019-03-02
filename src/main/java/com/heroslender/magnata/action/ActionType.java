package com.heroslender.magnata.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Heroslender.
 */

@AllArgsConstructor
public enum ActionType {
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

    @Getter private String identifier;

    public static ActionType fromIdentifier(String identificador) {
        for (ActionType actionType : values())
            if (actionType.getIdentifier().equals(identificador))
                return actionType;
        return null;
    }
}
