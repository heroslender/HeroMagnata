package com.heroslender.HeroMagnata.dependencias;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.heroslender.HeroMagnata.API.Eventos.MagnataChangeEvent;
import com.heroslender.HeroMagnata.Account;
import com.heroslender.HeroMagnata.HeroMagnata;
import com.heroslender.HeroMagnata.Utils.NumberUtils;
import lombok.Data;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.util.NMS;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heroslender.
 */
public class CitizensSupport implements Listener {

    private List<NpcMagnata> npcs;

    public CitizensSupport() {
        Bukkit.getLogger().info("[HeroMagnata] Citizens e HolographicDisplays detetado!");

        npcs = new ArrayList<>();

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HeroMagnata.getInstance(), () -> {
            Bukkit.getLogger().info("[HeroMagnata] A carergar npcs...");
            if (HeroMagnata.getInstance().getConfig().contains("npcs"))
                for (String npcID : HeroMagnata.getInstance().getConfig().getConfigurationSection("npcs").getKeys(false)) {
                    addNPC(new NpcMagnata(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(npcID)), HeroMagnata.getInstance().getConfig().getStringList("npcs." + npcID)));
                }
            Bukkit.getLogger().info("[HeroMagnata] " + npcs.size() + " npcs carregados!");
        }, 5L);

        Bukkit.getPluginManager().registerEvents(this, HeroMagnata.getInstance());
    }

    private static Location getHologramLoc(Location NPCLocation, int linhasHolograma) {
        return NPCLocation.clone().add(0, 2.37 + linhasHolograma * 0.24, 0);
    }

    private static Hologram getHologram(Location locationMaquina, int linhasHolograma) {
        for (Hologram hologram : HologramsAPI.getHolograms(HeroMagnata.getInstance())) {
            if (hologram.getLocation().equals(getHologramLoc(locationMaquina, linhasHolograma))) {
                return hologram;
            }
        }
        return HologramsAPI.createHologram(HeroMagnata.getInstance(), getHologramLoc(locationMaquina, linhasHolograma));
    }

    public void criarNPC(Location location) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "&a" + HeroMagnata.getMagnataAtual());
        npc.getTrait(LookClose.class).lookClose(true);
        npc.data().setPersistent("player-skin-name", HeroMagnata.getMagnataAtual());
        npc.data().setPersistent("player-skin-use-latest", false);
        npc.spawn(location);
        if (npc.isSpawned()) {
            SkinnableEntity skinnable = NMS.getSkinnable(npc.getEntity());
            if (skinnable != null) {
                skinnable.setSkinName(HeroMagnata.getMagnataAtual());
            }
        }

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

            npcMagnata.atualizar(new Account(HeroMagnata.getMagnataAtual(), HeroMagnata.getEcon().getBalance(HeroMagnata.getMagnataAtual())));
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
        NpcMagnata npc = null;
        for (NpcMagnata snpc : npcs)
            if (snpc.getNpc().getId() == e.getNPC().getId()) {
                npc = snpc;
                break;
            }
        if (npc == null)
            return;
        npcs.remove(npc);
        if (npc.getHologram() != null)
            npc.getHologram().delete();
        HeroMagnata.getInstance().getConfig().set("npcs." + npc.getNpc().getId(), null);
        HeroMagnata.getInstance().saveConfig();
    }

    private void addNPC(NpcMagnata npc) {
        if (contains(npc.getNpc().getId()))
            return;
        npcs.add(npc);
    }

    private boolean contains(int npcID) {
        for (NpcMagnata snpc : npcs)
            if (snpc.getNpc().getId() == npcID)
                return true;
        return false;
    }

    @Data
    private class NpcMagnata {
        NPC npc;
        Hologram hologram;
        List<String> hologramText;
        List<TextLine> textLines;

        NpcMagnata(NPC npc, List<String> hologramText) {
            this.npc = npc;
            this.hologramText = hologramText;
            textLines = new ArrayList<>();
            if (!hologramText.isEmpty()) {
                hologram = CitizensSupport.getHologram(npc.getStoredLocation(), hologramText.size());
                Account magnata = new Account(HeroMagnata.getMagnataAtual(), HeroMagnata.getEcon().getBalance(HeroMagnata.getMagnataAtual()));
                hologramText.forEach(s -> textLines.add(hologram.appendTextLine(s
                        .replace("{nome}", magnata.getPlayer())
                        .replace("{saldo}", NumberUtils.format(magnata.getMoney()))
                        .replace("{saldo_short}", NumberUtils.formatShort(magnata.getMoney()))
                        .replace('&', '§'))));
            }
        }

        void atualizar(Account novoMagnata) {
            Location location = npc.getStoredLocation();
            if (npc.isSpawned()) {
                npc.despawn(DespawnReason.PENDING_RESPAWN);
            }
            npc.setName("&a" + HeroMagnata.getMagnataAtual());
            npc.spawn(location);
            // Defenir skin
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroMagnata.getInstance(), () -> {
//            npc.data().setPersistent("player-skin-use-latest", false);
                npc.data().setPersistent("player-skin-name", novoMagnata.getPlayer());
                if (npc.isSpawned()) {
                    SkinnableEntity skinnable = NMS.getSkinnable(npc.getEntity());
                    if (skinnable != null) {
                        skinnable.setSkinName(novoMagnata.getPlayer());
                    }
                }
            }, 20L);

            if (hologram != null) {
                for (int i = 0; i < hologramText.size(); i++) {
                    String linhaNova = hologramText.get(i)
                            .replace("{nome}", novoMagnata.getPlayer())
                            .replace("{saldo}", NumberUtils.format(novoMagnata.getMoney()))
                            .replace("{saldo_short}", NumberUtils.formatShort(novoMagnata.getMoney()))
                            .replace('&', '§');
                    if (textLines.size() > i) {
                        if (!textLines.get(i).getText().equals(linhaNova)) {
                            textLines.get(i).setText(linhaNova);
                        }
                    } else textLines.add(hologram.appendTextLine(linhaNova));
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
