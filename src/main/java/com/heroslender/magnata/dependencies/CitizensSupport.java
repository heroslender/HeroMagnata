package com.heroslender.magnata.dependencies;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.heroslender.magnata.HeroMagnata;
import com.heroslender.magnata.api.events.MagnataChangeEvent;
import com.heroslender.magnata.dependencies.citizens.NPC;
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

    private final List<MagnataNpc> npcs;

    public CitizensSupport(@NotNull HeroMagnata plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        npcs = new ArrayList<>();

        logger.info("Citizens e HolographicDisplays detetado!");
    }

    public void enable() {
        // Schedule to load npcs 5 ticks after the server start, to make sure citizens fully loaded.
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            logger.info("A carergar NPCs...");

            ConfigurationSection npcSection = plugin.getConfig().getConfigurationSection("npcs");
            if (npcSection != null) {
                loadNpcs(npcSection);
                logger.log(Level.INFO, "Foram carregados {0} NPCs", npcs.size());
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

    public void loadNpcs(@NotNull ConfigurationSection config) {
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

                addNPC(
                        new MagnataNpc(new NPC(citizensNpc), config.getStringList(npcID))
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

    public void criarNPC(@NotNull Location location) {
        Objects.requireNonNull(location, "Location to create NPC is null.");

        final String magnata = plugin.getMagnata();
        NPC npc = new NPC(location, magnata);

        List<String> hologram = new ArrayList<>();
        hologram.add("&3&l&m-----&2&l Magnata &3&l&m-----");
        hologram.add("&2$&a{saldo}");

        plugin.getConfig().set("npcs." + npc.getId(), hologram);

        plugin.saveConfig();

        addNPC(new MagnataNpc(npc, hologram));
    }

    @EventHandler
    private void onMagnataChangeEvent(MagnataChangeEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> npcs.forEach(npcMagnata -> npcMagnata.update(e.getNovoMagnata())));
    }

    @EventHandler
    private void onNpcRemoveEvent(NPCRemoveEvent e) {
        for (MagnataNpc npc : npcs) {
            if (npc.getNpc().getId() == e.getNPC().getId()) {
                npcs.remove(npc);

                npc.delete(false);
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
        @Nullable final Hologram hologram;
        final List<String> hologramText;
        NPC npc;

        MagnataNpc(@NotNull NPC npc, @NotNull List<String> hologramText) {
            Objects.requireNonNull(npc, "NPC for magnata is null");
            Objects.requireNonNull(hologramText, "NPC hologram text is null");

            this.npc = npc;
            this.hologramText = hologramText;

            if (!hologramText.isEmpty()) {
                Location loc = npc.getLocation().add(0, 2.37 + hologramText.size() * 0.24, 0);
                hologram = HologramsAPI.createHologram(plugin, loc);

                plugin.getMagnataAccount().whenComplete((account, throwable) -> {
                    if (throwable == null && account != null) {
                        hologramText.forEach(s -> hologram.appendTextLine(account.format(s)));
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
                    String linhaNova = novoMagnata.format(hologramText.get(i));

                    TextLine line = (TextLine) hologram.getLine(i);
                    if (line != null) {
                        line.setText(linhaNova);
                    } else {
                        hologram.appendTextLine(linhaNova);
                    }
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
                hologram.delete();
            }
        }

        void delete() {
            delete(true);
        }

        void delete(final boolean despawnNpc) {
            if (hologram != null)
                hologram.delete();
            plugin.getConfig().set("npcs." + npc.getId(), null);
            plugin.saveConfig();

            if (despawnNpc) {
                npc.despawn(DespawnReason.REMOVAL);
            }
        }
    }
}
