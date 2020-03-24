package com.heroslender.magnata.dependencies.citizens;

import com.heroslender.magnata.HeroMagnata;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NPC {
    private final net.citizensnpcs.api.npc.NPC npc;

    public NPC(@NotNull final net.citizensnpcs.api.npc.NPC npc) {
        Objects.requireNonNull(npc, "NPC is null");

        this.npc = npc;
    }

    public NPC(final Location location, final String name) {
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "&a" + name);
        npc.getTrait(LookClose.class).lookClose(true);
        npc.data().setPersistent("player-skin-name", name);
        npc.spawn(location);
        if (npc.getEntity() instanceof SkinnableEntity) {
            SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
            if (skinnable != null) {
                skinnable.setSkinName(name, true);
            }
        }
    }

    public void update(final String name) {
        npc.data().setPersistent("player-skin-name", name);
        if (npc.getEntity() instanceof SkinnableEntity) {
            SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
            if (skinnable != null) {
                skinnable.setSkinName(name, true);
            }
        }

        Location location = npc.getStoredLocation();
        if (npc.isSpawned()) {
            npc.despawn(DespawnReason.PENDING_RESPAWN);
        }
        npc.setName("&a" + HeroMagnata.getInstance().getMagnata());
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
