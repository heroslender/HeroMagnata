package cf.heroslender.HeroMagnata.API;

import cf.heroslender.HeroMagnata.HeroMagnata;
import org.bukkit.Bukkit;

/**
 * Created by Heroslender.
 */
public class HeroMagnataAPI {

    public static String getMagnataAtual() {
        return HeroMagnata.getMagnataAtual();
    }

    public static boolean isMagnataOnline() {
        return Bukkit.getPlayerExact(HeroMagnata.getMagnataAtual()) != null;
    }
}
