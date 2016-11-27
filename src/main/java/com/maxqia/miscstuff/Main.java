package com.maxqia.miscstuff;

import java.io.IOException;
import java.nio.file.Path;

import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import ninja.leaping.configurate.ConfigurationOptions;
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
    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path path;

    ConfigurationLoader<CommentedConfigurationNode> loader;

    CommentedConfigurationNode rootNode;
    CommentedConfigurationNode node;


    @Listener
    public void onPreInit(GamePreInitializationEvent event) { // setup configuration
        loader = HoconConfigurationLoader.builder().setPath(path).build();
        try {
            this.rootNode = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        node = rootNode.getNode("MiscStuff").setComment("The configuration for MiscStuff!");
        //rootNode.get
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        new DisableAttackTime(this);
        new DoubleJump(this);
        new NoWeather(this);
        new NoTarget(this);
        //new FastDespawn(this); // Keys.DESPAWN_DELAY doesn't work :(
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent event) {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
