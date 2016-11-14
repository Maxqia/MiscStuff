package com.maxqia.miscstuff;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.ai.task.builtin.creature.target.TargetAITask;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ai.AITaskEvent;

/**
 * This module make's it so
 * that mobs can't target players.
 * @author Maxqia
 */
public class NoTarget {
    public NoTarget(Main instance) {
        Sponge.getEventManager().registerListeners(instance, this);
    }

    @Listener
    public void onTask(AITaskEvent.Add event) {
        if (event.getTask() instanceof TargetAITask) {
            event.setCancelled(true);
        }
    }
}
