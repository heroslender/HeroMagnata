package com.heroslender.magnata.dependencies.placeholderapi;

import com.heroslender.magnata.HeroMagnata;
import org.bukkit.Bukkit;

public class PAPISupport {
    public PAPISupport() {
        Bukkit.getLogger().info("[HeroMagnata] PlaceholderAPI detetado!");
        new MagnataExpansion(HeroMagnata.getInstance()).register();
    }
}
