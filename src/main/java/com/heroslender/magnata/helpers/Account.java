package com.heroslender.magnata.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Account {
    @Getter private static final Account empty = new Account("", 0);

    private final String player;
    private final double money;
}
