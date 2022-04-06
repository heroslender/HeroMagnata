package com.heroslender.magnata.dependencies;

import com.heroslender.magnata.HeroMagnata;
import com.heroslender.magnata.api.events.MagnataChangeEvent;
import com.heroslender.magnata.dependencies.citizens.NPC;
import com.heroslender.magnata.dependencies.hologram.DecentHologramsHologramImpl;
import com.heroslender.magnata.dependencies.hologram.Hologram;
import com.heroslender.magnata.dependencies.hologram.HologramFactory;
import com.heroslender.magnata.dependencies.hologram.HolographicDisplaysHologramImpl;
import com.heroslender.magnata.helpers.Account;
import lombok.Data;
import lombok.val;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Heroslender.
 */
public class CitizensSupport implements Listener {
    private final HeroMagnata plugin;
    private final Logger logger;
    private final HologramFactory hologramFactory;

    private final List<MagnataNpc> npcs;

    public CitizensSupport(@NotNull HeroMagnata plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        npcs = new ArrayList<>();

        logger.info("Citizens detetado!");

        if (plugin.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            this.hologramFactory = HolographicDisplaysHologramImpl.FACTORY;
            logger.info("HolographicDisplays detetado!");
        } else if (plugin.getServer().getPluginManager().isPluginEnabled("DecentHolograms")) {
            this.hologramFactory = DecentHologramsHologramImpl.FACTORY;
            logger.info("DecentHolograms detetado!");
        } else {
            this.hologramFactory = null;
            logger.warning("Nenhum plugin de holograma suportado foi detetado!");
        }
    }

    public void enable() {
        // Schedule to load npcs 5 ticks after the server start, to make sure citizens fully loaded.
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            logger.info("A carergar NPCs...");

            ConfigurationSection npcSection = plugin.getConfig().getConfigurationSection("npcs");
            if (npcSection != null) {
                loadNPCs(npcSection);
                logger.log(Level.INFO, "Foram carregados {0} NPCs", npcs.size());
                return;
            }
            logger.info("Não foram encontrados NPCs na config.");
        }, 5L);


        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void disable() {
        // Unload NPCs
        val it = npcs.iterator();
        while (it.hasNext()) {
            val npc = it.next();
            npc.unload();

            it.remove();
        }

        // Unregister the event listener
        HandlerList.unregisterAll(this);
    }

    public void reload() {
        disable();
        enable();
    }

    public void loadNPCs(@NotNull ConfigurationSection config) {
        Objects.requireNonNull(config, "NPCs configuration is null.");

        for (String npcID : config.getKeys(false)) {
            try {
                val id = Integer.parseInt(npcID);
                val citizensNpc = CitizensAPI.getNPCRegistry().getById(id);
                if (citizensNpc == null) {
                    logger.log(
                            Level.WARNING,
                            "Não existe nenhum NPC com o id {0}, ignorando.",
                            npcID
                    );
                    continue;
                }

                if (config.isList(npcID)) {
                    // Migrating config from the old format
                    plugin.getLogger().log(Level.INFO, "Atualizando a config do NPC {0}", npcID);
                    config.set(npcID + ".hologram.text", config.getStringList(npcID));
                    config.set(npcID + ".hologram.offset.x", 0);
                    config.set(npcID + ".hologram.offset.y", 2.37);
                    config.set(npcID + ".hologram.offset.z", 0);
                    plugin.saveConfig();
                }

                addNPC(
                        new MagnataNpc(
                                new NPC(citizensNpc),
                                config.getStringList(npcID + ".hologram.text"),
                                config.getDouble(npcID + ".hologram.offset.x", 0),
                                config.getDouble(npcID + ".hologram.offset.y", 2.37),
                                config.getDouble(npcID + ".hologram.offset.z", 0)
                        )
                );
            } catch (NumberFormatException e) {
                logger.log(
                        Level.WARNING,
                        "ID do npc não é um número inteiro válido! Valor obtido: {0}",
                        npcID
                );
            }
        }
    }

    public void createNPC(@NotNull Location location) {
        Objects.requireNonNull(location, "Location to create NPC is null.");

        final String magnata = plugin.getMagnata();
        NPC npc = new NPC(location, magnata);

        List<String> hologram = new ArrayList<>();
        if (isHologramsEnabled()) {
            hologram.add("&3&l&m-----&2&l Magnata &3&l&m-----");
            hologram.add("&2$&a{saldo}");

            plugin.getConfig().set("npcs." + npc.getId() + ".hologram.text", hologram);
            plugin.getConfig().set("npcs." + npc.getId() + ".hologram.offset.x", 0);
            plugin.getConfig().set("npcs." + npc.getId() + ".hologram.offset.y", 2.37);
            plugin.getConfig().set("npcs." + npc.getId() + ".hologram.offset.z", 0);
            plugin.saveConfig();
        }

        addNPC(new MagnataNpc(npc, hologram, 0, 2.37, 0));
    }

    @EventHandler
    private void onMagnataChangeEvent(MagnataChangeEvent e) {
        Bukkit.getScheduler().runTask(plugin, () -> npcs.forEach(npcMagnata -> npcMagnata.update(e.getNovoMagnata())));
    }

    @EventHandler
    private void onNpcRemoveEvent(NPCRemoveEvent e) {
        for (MagnataNpc npc : npcs) {
            if (npc.getNpc().getId() == e.getNPC().getId()) {
                npcs.remove(npc);

                npc.delete();
                return;
            }
        }
    }

    private void addNPC(@NotNull MagnataNpc npc) {
        Objects.requireNonNull(npc, "NPC to add to cache is null");

        for (MagnataNpc snpc : npcs)
            if (snpc.getNpc().getId() == npc.getNpc().getId())
                return;

        npcs.add(npc);
    }

    @Data
    private class MagnataNpc {
        @Nullable
        final Hologram hologram;
        final List<String> hologramText;
        NPC npc;

        MagnataNpc(@NotNull NPC npc, @NotNull List<String> hologramText, double offsetX, double offsetY, double offsetZ) {
            Objects.requireNonNull(npc, "NPC for magnata is null");
            Objects.requireNonNull(hologramText, "NPC hologram text is null");

            this.npc = npc;
            this.hologramText = hologramText;

            if (isHologramsEnabled() && !hologramText.isEmpty()) {
                Location loc = npc.getLocation().add(offsetX, offsetY + hologramText.size() * 0.24, offsetZ);
                hologram = hologramFactory.createHologram("magnata_hd_" + npc.getId(), loc, Collections.emptyList());

                plugin.getMagnataAccount().whenComplete((account, throwable) -> {
                    try {
                        Bukkit.getScheduler().runTask(plugin, () ->
                                hologramText.forEach(s -> hologram.addLine(account.format(s)))
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                hologram = null;
            }
        }

        void update(@NotNull Account novoMagnata) {
            Objects.requireNonNull(novoMagnata, "NPC update null magnata");
            npc.update(novoMagnata.getPlayer());

            if (hologram != null) {
                for (int i = 0; i < hologramText.size(); i++) {
                    String newLine = novoMagnata.format(hologramText.get(i));

                    hologram.setLine(i, newLine);
                }

                try {
                    // Cleanup extra lines
                    if (hologramText.size() < hologram.size()) {
                        for (int i = hologramText.size(); i < hologram.size(); i++) {
                            hologram.removeLine(i);
                        }
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }

        void unload() {
            if (hologram != null) {
                hologram.remove();
            }
        }

        void delete() {
            if (hologram != null)
                hologram.remove();
            plugin.getConfig().set("npcs." + npc.getId(), null);
            plugin.saveConfig();
            npc.despawn(DespawnReason.REMOVAL);
        }

    }

    public boolean isHologramsEnabled() {
        return hologramFactory != null;
    }
}
