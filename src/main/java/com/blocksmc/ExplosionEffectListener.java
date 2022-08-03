package com.blocksmc;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;

public class ExplosionEffectListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!event.getBlock().getType().equals(Material.TNT)){
            return;
        }
        Location loc = event.getBlockPlaced().getLocation();
        loc.setX(loc.getX() + 0.5);
        loc.setZ(loc.getZ() + 0.5);
        event.getBlock().setType(Material.AIR);

        TNTPrimed tntPrimed = loc.getWorld().spawn(loc, TNTPrimed.class);
        tntPrimed.setFuseTicks(20);

        // Change via NMS the source of the TNT by the player
        EntityLiving nmsEntityLiving = (((CraftLivingEntity) event.getPlayer()).getHandle());
        EntityTNTPrimed nmsTNT = (((CraftTNTPrimed) tntPrimed).getHandle());

        try {
            Field sourceField = EntityTNTPrimed.class.getDeclaredField("source");
            sourceField.setAccessible(true);
            sourceField.set(nmsTNT, nmsEntityLiving);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplode(EntityExplodeEvent event) {
        if(event.getEntity().getType() == EntityType.PRIMED_TNT){
            event.setCancelled(true);
            Player placer = (Player) ((TNTPrimed) event.getEntity()).getSource();

            for (Entity entity : event.getEntity().getNearbyEntities(5,5,5)) {
                if(entity.equals(placer)) continue;

                double x = entity.getLocation().getX() - event.getLocation().getX();
                double y = entity.getLocation().getY() - event.getLocation().getY();
                double z = entity.getLocation().getZ() - event.getLocation().getZ();
                entity.setVelocity(getKnockbackVector(x, y, z));
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
            event.setDamage(0);
            event.setCancelled(true);
        }
    }

    private Vector getKnockbackVector(double x, double y, double z) {
        double speed = 1;
        double multiplier = Math.sqrt((speed*speed) / (x*x + y*y + z*z));
        return new Vector(x, y, z).multiply(multiplier).setY(y);
    }
}
