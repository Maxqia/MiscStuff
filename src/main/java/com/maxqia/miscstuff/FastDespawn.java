package com.maxqia.miscstuff;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.DespawnDelayData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

/**
 * This sets the despawn time of all items
 * to be set to a configurable time.
 * @author Maxqia
 */
public class FastDespawn {
    
    private Integer timeInTicks;
    
    public FastDespawn(Main instance) {
        Sponge.getEventManager().registerListeners(instance, this);
        short seconds = 0;
        timeInTicks = 6000 - (20 * seconds);
    } // 6000 is 5 minutes for the despawn of items;
    
    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof Item) {
                entity.offer(Keys.DESPAWN_DELAY, timeInTicks);
                Optional<DespawnDelayData> value = entity.getOrCreate(DespawnDelayData.class);
                if (value.isPresent()) {
                    value.get().delay().set(timeInTicks);
                    //entity.offer(Keys.DESPAWN_DELAY, timeInTicks);
                    //entity.offer(Keys.DESPAWN_DELAY,60);
                    entity.getClass();
                    continue;
                }
            }
        }
    }

}
