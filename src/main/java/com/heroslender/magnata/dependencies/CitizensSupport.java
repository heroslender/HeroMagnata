package com.heroslender.magnata.dependencies;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.heroslender.magnata.HeroMagnata;
import com.heroslender.magnata.api.events.MagnataChangeEvent;
import com.heroslender.magnata.dependencies.citizens.NPC;
import com.heroslender.magnata.helpers.Account;
import lombok.Data;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Heroslender.
 */
public class CitizensSupport implements Listener {

    private List<NpcMagnata> npcs;

    public CitizensSupport() {
        HeroMagnata.getInstance().getLogger().log(Level.INFO, "Citizens e HolographicDisplays detetado!");

        npcs = new ArrayList<>();

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HeroMagnata.getInstance(), () -> {
            HeroMagnata.getInstance().getLogger().log(Level.INFO, "A carergar NPCs...");

            if (HeroMagnata.getInstance().getConfig().contains("npcs")) {
                ConfigurationSection npcSection = HeroMagnata.getInstance().getConfig().getConfigurationSection("npcs");

                for (String npcID : npcSection.getKeys(false)) {
                    addNPC(
                            new NpcMagnata(
                                    new NPC(Integer.parseInt(npcID)),
                                    npcSection.getStringList(npcID)
                            )
                    );
                }
            }

            HeroMagnata.getInstance().getLogger().log(Level.INFO, "Foram carregados {0} NPCs", npcs.size());
        }, 5L);

        Bukkit.getPluginManager().registerEvents(this, HeroMagnata.getInstance());
    }

    public void criarNPC(Location location) {
        final String magnata = HeroMagnata.getInstance().getMagnataAtual();
        NPC npc = new NPC(location, magnata);

        List<String> hologram = new ArrayList<>();
        hologram.add("&3&l&m-----&2&l Magnata &3&l&m-----");
        hologram.add("&2$&a{saldo}");

        HeroMagnata.getInstance().getConfig().set("npcs." + npc.getId(), hologram);

        HeroMagnata.getInstance().saveConfig();

        addNPC(new NpcMagnata(npc, hologram));
    }

    public void reload() {
        npcs.forEach(npcMagnata -> {
            if (!HeroMagnata.getInstance().getConfig().contains("npcs." + npcMagnata.getNpc().getId())) {
                npcMagnata.delete();
            }
            npcMagnata.setHologramText(HeroMagnata.getInstance().getConfig().getStringList("npcs." + npcMagnata.getNpc().getId()));

            npcMagnata.atualizar(HeroMagnata.getInstance().getMagnataAccount());
        });
    }

    public void disable() {
        npcs.forEach(npcMagnata -> {
            if (npcMagnata.getHologram() != null)
                npcMagnata.getHologram().delete();
        });
    }

    @EventHandler
    private void onMagnataChangeEvent(MagnataChangeEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroMagnata.getInstance(), () -> npcs.forEach(npcMagnata -> npcMagnata.atualizar(e.getNovoMagnata())));
    }

    @EventHandler
    private void onNpcRemoveEvent(NPCRemoveEvent e) {
        for (NpcMagnata npc : npcs) {
            if (npc.getNpc().getId() == e.getNPC().getId()) {
                npcs.remove(npc);
                if (npc.getHologram() != null)
                    npc.getHologram().delete();
                HeroMagnata.getInstance().getConfig().set("npcs." + npc.getNpc().getId(), null);
                HeroMagnata.getInstance().saveConfig();
                return;
            }
        }
    }

    private void addNPC(NpcMagnata npc) {
        for (NpcMagnata snpc : npcs)
            if (snpc.getNpc().getId() == npc.getNpc().getId())
                return;

        npcs.add(npc);
    }

    @Data
    private class NpcMagnata {
        NPC npc;
        Hologram hologram;
        List<String> hologramText;

        NpcMagnata(NPC npc, List<String> hologramText) {
            this.npc = npc;
            this.hologramText = hologramText;
            if (!hologramText.isEmpty()) {
                Location loc = npc.getLocation().add(0, 2.37 + hologramText.size() * 0.24, 0);
                hologram = HologramsAPI.createHologram(HeroMagnata.getInstance(), loc);

                Account magnata = HeroMagnata.getInstance().getMagnataAccount();
                hologramText.forEach(s -> hologram.appendTextLine(magnata.format(s)));
            }
        }

        void atualizar(Account novoMagnata) {
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

                // Cleanup extra lines
                int i = hologramText.size();
                TextLine line = (TextLine) hologram.getLine(i);
                while (line != null) {
                    line.removeLine();

                    i++;
                    line = (TextLine) hologram.getLine(i);
                }
            }
        }

        void delete() {
            if (hologram != null)
                hologram.delete();
            npc.despawn(DespawnReason.REMOVAL);
            HeroMagnata.getInstance().getConfig().set("npcs." + npc.getId(), null);
            HeroMagnata.getInstance().saveConfig();
        }
    }
}
