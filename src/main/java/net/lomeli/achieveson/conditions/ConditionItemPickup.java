package net.lomeli.achieveson.conditions;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.lib.ParsingUtil;

public class ConditionItemPickup extends ConditionHandler {
    private HashMap<ItemStack, Achievement> registeredAchievements;

    public ConditionItemPickup() {
        registeredAchievements = new HashMap<ItemStack, Achievement>();
    }

    @SubscribeEvent
    public void pickUpItem(EntityItemPickupEvent event) {
        if (!event.item.worldObj.isRemote && event.entityPlayer != null) {
            ItemStack stack = event.item.getEntityItem();
            stack.stackSize = 1;
            Achievement achievement = null;
            if (registeredAchievements != null && !registeredAchievements.isEmpty()) {
                for (Map.Entry<ItemStack, Achievement> entry : registeredAchievements.entrySet()) {
                    if (entry != null && ParsingUtil.doStacksMatch(stack, entry.getKey())) {
                        achievement = entry.getValue();
                        break;
                    }
                }
            }
            if (achievement != null) {
                EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
                if (!player.func_147099_x().hasAchievementUnlocked(achievement) && player.func_147099_x().canUnlockAchievement(achievement))
                    player.addStat(achievement, 1);
            }
        }
    }

    @Override
    public void registerEvent() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {
        if (achievement != null && args != null && (args.length == 1 || args.length == 2)) {
            String name = args[0];
            String[] array = name.split(":");
            if (array.length == 2 || array.length == 3) {
                ItemStack stack = ParsingUtil.getStackFromString(name);
                if (stack != null)
                        registeredAchievements.put(stack, achievement);
            }
        }
    }

    @Override
    public String conditionID() {
        return "pickupitem";
    }

    @Override
    public boolean isClientSide() {
        return false;
    }
}
