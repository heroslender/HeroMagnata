package cf.heroslender.HeroMagnata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Account {
    @Getter private final String player;
    @Getter private final double money;
}
