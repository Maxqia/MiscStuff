package com.maxqia.miscstuff;

import java.nio.file.Path;

import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

/**
 * Main plugin class, mainly launches listeners and
 * handles configuration
 * @author Maxqia
 */
@Plugin(id = "miscstuff", name = "MiscStuff")
public class Main {
    /*@Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    ConfigurationLoader<CommentedConfigurationNode> loader =
            HoconConfigurationLoader.builder().setPath(defaultConfig).build();*/

    @Listener
    public void onLoad(GameConstructionEvent event) {
        new DisableAttackTime(this);
        new DoubleJump(this);
        new NoWeather(this);
        //new FastDespawn(this); // Keys.DESPAWN_DELAY doesn't work :(
    }
}
