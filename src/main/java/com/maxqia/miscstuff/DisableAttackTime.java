package com.maxqia.miscstuff;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This module disables the 1.9 attack time
 * when they join and resets it when they leave
 * @author Maxqia
 */
public class DisableAttackTime {
    public DisableAttackTime(Main instance) {
        Sponge.getEventManager().registerListeners(instance, this);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        EntityPlayer handle = (EntityPlayer) event.getTargetEntity();
        handle.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(1024);
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        EntityPlayer handle = (EntityPlayer) event.getTargetEntity();
        handle.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(4);
    }

    /*@Listener //TODO move to auto-give shield module
    public void onPlayerClickInventoryEvent(ClickInventoryEvent event) {
        if (event.getTargetInventory().getArchetype() == InventoryArchetypes.PLAYER) {
            System.out.println(event.getCause());
            Slot slot = ((PlayerInventory) event.getCause().get(NamedCause.OWNER, Player.class).get().getInventory()).getOffhand();
            for (SlotTransaction trans : event.getTransactions()) {
                if (trans.getSlot().equals(slot)) {
                    trans.setValid(false);
                }
            }
        }
    }*/
}
