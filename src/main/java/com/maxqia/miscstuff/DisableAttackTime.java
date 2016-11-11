package com.maxqia.miscstuff;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;

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
    
    @Listener
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
    }
}
