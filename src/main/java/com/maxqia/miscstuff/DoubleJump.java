package com.maxqia.miscstuff;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import com.flowpowered.math.vector.Vector3d;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

/**
 * This module gives you a double jump ability!
 * Ideas based off my old plugin, Double Jump Minus
 * Some code taken from one of my unreleased plugins ...
 * @author Maxqia
 */
public class DoubleJump {
    Main instance;
    HashSet<Player> flyingCooldown = new HashSet<Player>();
    HashSet<Player> sneakCooldown = new HashSet<Player>();
    HashMap<Player, Integer> poundCooldown = new HashMap<Player, Integer>();

    public DoubleJump(Main instance) {
        this.instance = instance;
        CommentedConfigurationNode node = instance.node.getNode("DoubleJump");
        node.setComment("This module makes it so you can fly! (kind of)");
        if (node.getNode("enabled").getBoolean(false))
            Sponge.getEventManager().registerListeners(instance, this);
    }

    @Listener
    // Launch players if they start flying
    public void onPlayerMove(MoveEntityEvent event, @First Player player) {
        if (player.get(Keys.GAME_MODE).orElse(null) != GameModes.SURVIVAL) return;

        if (flyingCooldown.contains(player)) {
            player.getWorld().spawnParticles(ParticleEffect.builder().type(ParticleTypes.FIRE_SMOKE).option(ParticleOptions.DIRECTION, Direction.NONE).build(), player.getLocation().getPosition());
        } // TODO get Sponge API to make cataloged types have a builder option

        if (player.isOnGround()) {
            player.offer(Keys.CAN_FLY, true);
            if (flyingCooldown.remove(player)) {
                //System.out.println("On Ground!");
                if (sneakCooldown.remove(player)) {
                    blockBreak(event.getToTransform().getLocation(), player);
                }
            }
        } else {
            if (player.get(Keys.IS_FLYING).get()){
                flyingCooldown.add(player);
                player.offer(Keys.CAN_FLY, false);
                player.offer(Keys.IS_FLYING, false);

                player.setVelocity(getRotation(player.getRotation())
                        .mul(1.6,0,1.6).add(0, 1, 0));
                player.getWorld().spawnParticles(ParticleEffect.builder().type(ParticleTypes.MOBSPAWNER_FLAMES).build(), player.getLocation().getPosition()); // TODO get SpongeAPI to default implement with player's position
                player.getWorld().playSound(SoundTypes.ENTITY_GHAST_SHOOT, player.getLocation().getPosition(), 1);
            }
            if (player.get(Keys.IS_SNEAKING).orElse(false) && !sneakCooldown.contains(player) /*&& !poundCooldown.containsKey(player)*/) {
                player.setVelocity(new Vector3d(0, -5, 0));
                event.setCancelled(true); // cancel or it causes movement errors
                sneakCooldown.add(player);
            }
        }
    }

    private Vector3d getRotation(Vector3d rawRotation) {
        double xz = Math.cos(Math.toRadians(rawRotation.getX()));
        return new Vector3d(
                -xz * Math.sin(Math.toRadians(rawRotation.getY())),
                -Math.sin(Math.toRadians(rawRotation.getX())),
                xz * Math.cos(Math.toRadians(rawRotation.getY())));
    }

    @Listener // TODO this doesn't get fired ...
    // Don't damage player with their own Ground Pound
    public void onFall(DamageEntityEvent event) {
        //System.out.println("whaat");
        if (event.getTargetEntity() instanceof Player) {
            Player player = (Player) event.getTargetEntity();
            DamageSource source = event.getCause().get(NamedCause.SOURCE, DamageSource.class).orElse(null);
            if (source != null) {
                if (source.getType() == DamageTypes.FALL) {
                    event.setCancelled(true);
                    /*if (/*!poundCooldown.containsKey(player) &&*/ /*player.get(Keys.IS_SNEAKING).get()) {
                        blockBreak(player);
                        //poundCooldown.put(player, 50);
                    }*/
                }
            }

        }
    }

    private void blockBreak(Location<World> loc, Player player) {
        //System.out.println("block break!");
        // Play land effect in radius around player
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++)
                for (int z = -2; z <= 2; z++) {
                    Location<World> pos = loc.add(x, y, z);
                    loc.getExtent().spawnParticles(ParticleEffect.builder()
                            .type(ParticleTypes.BREAK_BLOCK).option(ParticleOptions.BLOCK_STATE, pos.getBlock()).build(),
                            pos.getPosition());
                }
        }

        // Damage Entities in radius
        //if (p.hasPermission("DJN.damage")) {
            Collection<Entity> nearby = loc.getExtent().getEntities(entity -> entity.getTransform().getPosition().distance(loc.getPosition()) <= 5); // getNearbyEntities but on a location
            for (Entity entity : nearby) {
                if (entity.equals(player)) continue;
                if (entity instanceof Living) {
                    ((Living) entity).damage(1, DamageSource.builder().type(DamageTypes.CUSTOM).build()); // Damage Entity
                    Vector3d vector = entity.getLocation().getPosition().sub(loc.getPosition().normalize()); // Get relative direction
                    entity.setVelocity(vector.mul(1, 0, 1)); // Set upward force
                }
            }
        //}
    }
}
