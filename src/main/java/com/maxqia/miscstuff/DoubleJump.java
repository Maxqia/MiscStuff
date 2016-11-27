package com.maxqia.miscstuff;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
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
    HashSet<Player> flyingCooldown = new HashSet<Player>();
    HashSet<Player> sneakCooldown = new HashSet<Player>();
    HashMap<Player, Integer> poundCooldown = new HashMap<Player, Integer>();
    int test = 0;

    public DoubleJump(Main instance) {
        CommentedConfigurationNode node = instance.node.getNode("DoubleJump");
        node.setComment("This module makes it so you can fly! (kind of)");
        if (node.getNode("enabled").getBoolean(false))
            Sponge.getEventManager().registerListeners(instance, this);
    }

    @Listener
    // Launch players if they start flying
    public void onPlayerMove(MoveEntityEvent event) {
        for (Map.Entry<String, Object> entry : event.getCause().getNamedCauses().entrySet()) {
            if (entry.getValue() instanceof Player) {
                Player player = (Player) entry.getValue();
                if (player.get(Keys.GAME_MODE).orElse(null) != GameModes.SURVIVAL) return;

                if (player.isOnGround()) {
                    player.offer(Keys.CAN_FLY, true);
                    sneakCooldown.remove(player);
                } else {
                    if (player.get(Keys.IS_FLYING).get()){
                        flyingCooldown.add(player);
                        player.offer(Keys.CAN_FLY, false);
                        player.offer(Keys.IS_FLYING, false);

                        //Double x = player.getVelocity().getX();
                        //Double z = player.getVelocity().getZ();
                        Vector3d rawRotation = player.getRotation();
                        player.setVelocity(getRotation(player.getRotation())
                                .mul(1.6,0,1.6).add(0, 1, 0));
                    }
                    if (player.get(Keys.IS_SNEAKING).orElse(false) && !sneakCooldown.contains(player) /*&& !poundCooldown.containsKey(player)*/) {
                        Vector3d rawRotation = player.getRotation();
                        player.setVelocity(player.getVelocity().add(0, -5, 0));
                        test++;
                        sneakCooldown.add(player);
                    }
                }
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
        System.out.println("whaat");
        if (event.getTargetEntity() instanceof Player) {
            Player player = (Player) event.getTargetEntity();
            DamageSource source = event.getCause().get("Source", DamageSource.class).orElse(null);
            if (source != null) {
                if (source.getType() == DamageTypes.FALL) {
                    event.setCancelled(true);
                    if (/*!poundCooldown.containsKey(player) &&*/ player.get(Keys.IS_SNEAKING).get()) {
                        blockBreak(player);
                        //poundCooldown.put(player, 50);
                    }
                }
            }

        }
    }

    private void blockBreak(Player player) {
        Location<World> loc = player.getLocation();
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
            Collection<Entity> nearby = player.getNearbyEntities(5);
            for (Entity entity : nearby) {
                if (entity.equals(player)) continue;
                if (entity instanceof Living) {
                    ((Living) entity).damage(1, DamageSource.builder().type(DamageTypes.CUSTOM).build()); // Damage Entity
                    Vector3d vector = entity.getLocation().getPosition().sub(player.getLocation().getPosition().normalize()); // Get relative direction
                    entity.setVelocity(vector.mul(1, 0, 1)); // Set upward force
                }
            }
        //}
    }
}
