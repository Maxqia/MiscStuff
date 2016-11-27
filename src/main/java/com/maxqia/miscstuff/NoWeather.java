package com.maxqia.miscstuff;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.world.weather.Weathers;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

/**
 * This module disables weather
 * @author Maxqia
 */
public class NoWeather {
    public NoWeather(Main instance) {
        CommentedConfigurationNode node = instance.node.getNode("NoWeather");
        node.setComment("Hate rain? This module is for you!");
        if (node.getNode("enabled").getBoolean(false))
            Sponge.getEventManager().registerListeners(instance, this);
    }

    @Listener
    public void onWeatherChangeEvent(ChangeWorldWeatherEvent event) {
        event.setWeather(Weathers.CLEAR);
    }
}
