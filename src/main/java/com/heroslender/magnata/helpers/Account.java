package com.heroslender.magnata.helpers;

import com.heroslender.magnata.utils.ChatColorUtils;
import com.heroslender.magnata.utils.NumberUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Account {
    @Getter private static final Account empty = new Account("", 0);

    private final String player;
    private final double money;

    public String format(final String message) {
        return ChatColorUtils.translateColors(
                message.replace("{nome}", getPlayer())
                        .replace("{saldo}", NumberUtils.format(getMoney()))
                        .replace("{saldo_short}", NumberUtils.formatShort(getMoney()))
                        .replace('&', '§')
        );
    }

    @Override
    public String toString() {
        return "Account(name='" + player + "', money=" + money + ")";
    }
}
