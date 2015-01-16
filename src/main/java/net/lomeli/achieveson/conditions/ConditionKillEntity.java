package net.lomeli.achieveson.conditions;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.lomeli.achieveson.api.ConditionHandler;

public class ConditionKillEntity extends ConditionHandler {
    private HashMap<String, Achievement> registeredAchievements;

    public ConditionKillEntity() {
        registeredAchievements = new HashMap<String, Achievement>();
    }

    @SubscribeEvent
    public void entityDeathEvent(LivingDeathEvent event) {
        if (event.entityLiving != null && !event.entityLiving.worldObj.isRemote && event.source != null) {
            if (event.source.getSourceOfDamage() != null && damageFromPlayer(event.source) && !registeredAchievements.isEmpty()) {
                Achievement achievement = registeredAchievements.get(event.entityLiving.getClass().getName());
                if (achievement != null) {
                    EntityPlayerMP player = null;
                    if (event.source.getSourceOfDamage() instanceof EntityArrow) {
                        if (((EntityArrow) event.source.getSourceOfDamage()).shootingEntity != null && ((EntityArrow) event.source.getSourceOfDamage()).shootingEntity instanceof EntityPlayer)
                            player = (EntityPlayerMP) ((EntityArrow) event.source.getSourceOfDamage()).shootingEntity;
                    } else if (event.source.getDamageType().equals("player"))
                        player = (EntityPlayerMP) event.source.getSourceOfDamage();
                    if (player != null && !player.func_147099_x().hasAchievementUnlocked(achievement) && player.func_147099_x().canUnlockAchievement(achievement))
                        player.addStat(achievement, 1);
                }
            }
        }
    }

    public boolean damageFromPlayer(DamageSource source) {
        if (source.getDamageType().equals("player"))
            return true;
        if (source.getSourceOfDamage() instanceof EntityArrow) {
            if (((EntityArrow) source.getSourceOfDamage()).shootingEntity != null) {
                if (((EntityArrow) source.getSourceOfDamage()).shootingEntity instanceof EntityPlayer)
                    return true;
            }
        }
        return false;
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {
        if (achievement != null && args != null && args.length == 1)
            registeredAchievements.put(args[0], achievement);
    }

    @Override
    public void registerEvent() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String conditionID() {
        return "killentity";
    }

    @Override
    public boolean isClientSide() {
        return false;
    }
}
