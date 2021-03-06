package com.codisimus.plugins.phatloots.listeners;

import com.codisimus.plugins.phatloots.PhatLoot;
import com.codisimus.plugins.phatloots.PhatLootChest;
import com.codisimus.plugins.phatloots.PhatLoots;
import com.codisimus.plugins.phatloots.PhatLootsUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 * Listens for redstone activating PhatLoot Dispensers
 *
 * @author Cody
 */
public class DispenserListener implements Listener {
    /**
     * Checks if a PhatLoot Dispenser is powered
     *
     * @param event The BlockPhysicsEvent that occurred
     */
    @EventHandler (ignoreCancelled = true)
    public void onBlockPowered(BlockPhysicsEvent event) {
        //Check if the Block is a Dispenser/Dropper
        Block block = event.getBlock();
        switch (block.getType()) {
        case DISPENSER: break;
        case DROPPER: break;
        default: return;
        }

        //We don't care if a block loses power
        if (block.getBlockPower() == 0) {
            return;
        }

        //Return if the Dispenser is not a PhatLootChest
        if (!PhatLootChest.isPhatLootChest(block)) {
            return;
        }

        //Return if there are not any player that are close enough
        Player player = getNearestPlayer(block.getLocation());
        if (player == null) {
            return;
        }

        //Roll for linked loot
        PhatLootChest plChest = PhatLootChest.getChest(block);
        for (PhatLoot phatLoot : PhatLoots.getPhatLoots(block, player)) {
            if (PhatLootsUtil.canLoot(player, phatLoot)) {
                phatLoot.rollForChestLoot(player, plChest);
            }
        }
    }

    /**
     * Returns the Player that is closest to the given Location.
     * Returns null if no Players are within 50 Blocks
     *
     * @param location The given Location
     * @return the closest Player
     */
    private static Player getNearestPlayer(Location location) {
        Player nearestPlayer = null;
        double shortestDistance = 2500;
        for (Player player: location.getWorld().getPlayers()) {
            Location playerLocation = player.getLocation();
            //Use the squared distance because is it much less resource intensive
            double distanceToPlayer = location.distanceSquared(playerLocation);
            if (distanceToPlayer < shortestDistance) {
                nearestPlayer = player;
                shortestDistance = distanceToPlayer;
            }
        }
        return nearestPlayer;
    }
}
