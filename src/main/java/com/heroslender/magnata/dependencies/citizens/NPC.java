package com.heroslender.magnata.dependencies.citizens;

import com.heroslender.magnata.HeroMagnata;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.util.NMS;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class NPC {
    private final net.citizensnpcs.api.npc.NPC npc;

    public NPC(final int ID) {
        this(CitizensAPI.getNPCRegistry().getById(ID));
    }

    public NPC(final net.citizensnpcs.api.npc.NPC npc) {
        this.npc = npc;
    }

    public NPC(final Location location, final String name) {
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "&a" + name);
        npc.getTrait(LookClose.class).lookClose(true);
        npc.data().setPersistent("player-skin-name", name);
        npc.data().setPersistent("player-skin-use-latest", false);
        npc.spawn(location);
        if (npc.isSpawned()) {
            SkinnableEntity skinnable = NMS.getSkinnable(npc.getEntity());
            if (skinnable != null) {
                skinnable.setSkinName(name);
            }
        }
    }

    public void update(final String name) {
        npc.data().setPersistent("player-skin-name", name);
        if (npc.isSpawned()) {
            SkinnableEntity skinnable = NMS.getSkinnable(npc.getEntity());
            if (skinnable != null) {
                skinnable.setSkinName(name);
            }
        }

        Location location = npc.getStoredLocation();
        if (npc.isSpawned()) {
            npc.despawn(DespawnReason.PENDING_RESPAWN);
        }
        npc.setName("&a" + HeroMagnata.getInstance().getMagnataAtual());
        npc.spawn(location);
    }

    public int getId() {
        return npc.getId();
    }

    public Location getLocation() {
        return npc.getStoredLocation().clone();
    }

    public void despawn(DespawnReason reason) {
        npc.despawn(reason);
    }
}
