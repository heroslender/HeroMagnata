package com.heroslender.magnata.dependencies.hologram;

import org.bukkit.Location;

import java.util.List;

public interface HologramFactory {
    Hologram createHologram(String id, Location location, List<String> lines);
}
