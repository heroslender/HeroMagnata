package com.heroslender.magnata.dependencies.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;

import java.util.List;

public class DecentHologramsHologramImpl implements Hologram {
    public static final HologramFactory FACTORY = new DecentHologramsHologramFactory();

    private static class DecentHologramsHologramFactory implements HologramFactory {
        @Override
        public Hologram createHologram(String id, Location location, List<String> lines) {
            eu.decentsoftware.holograms.api.holograms.Hologram hologram =
                    DHAPI.createHologram(id, location, false, lines);

            return new DecentHologramsHologramImpl(hologram);
        }
    }

    private final eu.decentsoftware.holograms.api.holograms.Hologram hologram;

    public DecentHologramsHologramImpl(eu.decentsoftware.holograms.api.holograms.Hologram hologram) {
        this.hologram = hologram;
    }

    @Override
    public void addLine(String line) {
        DHAPI.addHologramLine(hologram, line);
    }

    @Override
    public void removeLine(int line) {
        if (line < size()) {
            DHAPI.removeHologramLine(hologram, line);
        }
    }

    @Override
    public void setLine(int line, String text) {
        if (line < size()) {
            DHAPI.setHologramLine(hologram, line, text);
        } else {
            addLine(text);
        }
    }

    @Override
    public int size() {
        return hologram.size();
    }

    @Override
    public void remove() {
        DHAPI.removeHologram(hologram.getId());
    }
}
