package cf.heroslender.HeroMagnata.API.Eventos;

import cf.heroslender.HeroMagnata.Account;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Heroslender.
 */
public class MagnataChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter private Account novoMagnata;
    @Getter private Account antigoMagnata;

    public MagnataChangeEvent(Account novoMagnata, Account antigoMagnata) {
        this.novoMagnata = novoMagnata;
        this.antigoMagnata = antigoMagnata;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
