package com.heroslender.magnata.helpers;

import com.heroslender.magnata.utils.NumberUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public class Account {
    @Getter private static final Account empty = new Account("", 0);

    private final String player;
    private final double money;

    public String format(final String message) {
        return ChatColor.translateAlternateColorCodes('&',
                message.replace("{nome}", getPlayer())
                        .replace("{saldo}", NumberUtils.format(getMoney()))
                        .replace("{saldo_short}", NumberUtils.formatShort(getMoney()))
                        .replace('&', '§')
        );
    }
}
