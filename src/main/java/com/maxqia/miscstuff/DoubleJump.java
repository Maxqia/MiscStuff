package com.maxqia.miscstuff;

import org.spongepowered.api.Sponge;

public class DoubleJump {
    public DoubleJump(Main instance) {
        Sponge.getEventManager().registerListeners(instance, this);
    }
}
