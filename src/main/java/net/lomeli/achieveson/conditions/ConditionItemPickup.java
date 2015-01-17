package net.lomeli.achieveson.conditions;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.lib.NBTUtil;
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
            EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
            unlockAchievement(player, stack);
        }
    }

    public void unlockAchievement(EntityPlayerMP playerMP, ItemStack stack) {
        if (playerMP != null && stack != null && stack.getItem() != null && stack.stackSize > 0) {
            Achievement achievement = null;
            ItemStack baseItem = null;
            if (registeredAchievements != null && !registeredAchievements.isEmpty()) {
                for (Map.Entry<ItemStack, Achievement> entry : registeredAchievements.entrySet()) {
                    if (entry != null && ParsingUtil.doStacksMatch(stack, entry.getKey())) {
                        achievement = entry.getValue();
                        baseItem = entry.getKey();
                        break;
                    }
                }
            }
            if (achievement != null && baseItem != null && !playerMP.func_147099_x().hasAchievementUnlocked(achievement) && playerMP.func_147099_x().canUnlockAchievement(achievement)) {
                int count = stack.stackSize + NBTUtil.getInt(playerMP, achievement.statId);
                NBTUtil.setInt(playerMP, achievement.statId, count);
                if (count >= baseItem.stackSize) {
                    playerMP.addStat(achievement, 1);
                    NBTUtil.removeTag(playerMP, achievement.statId);
                }
            }
        }
    }

    @Override
    public void registerEvent() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {
        if (achievement != null && args != null && (args.length == 1 || args.length == 2 || args.length == 3)) {
            String name = args[0];
            ItemStack stack = (args.length >= 2 && !args[1].startsWith("count=")) ? ParsingUtil.getStackFromString(name, ParsingUtil.parseInt(args[1])) : ParsingUtil.getStackFromString(name);
            if (stack != null && args.length == 2 && args[1].startsWith("count="))
                stack.stackSize = ParsingUtil.parseInt(args[1].substring(6));
            if (stack != null && args.length == 3 && args[2].startsWith("count="))
                stack.stackSize = ParsingUtil.parseInt(args[2].substring(6));
            if (stack != null && stack.getItem() != null && stack.stackSize > 0)
                registeredAchievements.put(stack, achievement);
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
