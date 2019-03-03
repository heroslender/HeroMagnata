package com.heroslender.magnata.api;

import com.heroslender.magnata.HeroMagnata;
import org.bukkit.Bukkit;

/**
 * Created by Heroslender.
 */
public class HeroMagnataAPI {

    public static String getMagnata() {
        return HeroMagnata.getInstance().getMagnataAtual();
    }

    public static boolean isMagnataOnline() {
        return Bukkit.getPlayerExact(HeroMagnata.getInstance().getMagnataAtual()) != null;
    }
}
