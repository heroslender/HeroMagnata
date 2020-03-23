package com.heroslender.magnata.api;

import com.heroslender.magnata.HeroMagnata;
import org.bukkit.Bukkit;

/**
 * Created by Heroslender.
 */
public class HeroMagnataAPI {

    public static String getMagnata() {
        return HeroMagnata.getInstance().getMagnata();
    }

    public static boolean isMagnataOnline() {
        return Bukkit.getPlayerExact(HeroMagnata.getInstance().getMagnata()) != null;
    }
}
