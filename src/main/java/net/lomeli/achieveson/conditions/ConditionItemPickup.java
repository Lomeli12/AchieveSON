package net.lomeli.achieveson.conditions;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import net.lomeli.achieveson.Logger;
import net.lomeli.achieveson.api.ConditionHandler;

public class ConditionItemPickup extends ConditionHandler {
    private static HashMap<ItemStack, Achievement> registeredAchievements;

    public ConditionItemPickup() {
        registeredAchievements = new HashMap<ItemStack, Achievement>();
    }

    @SubscribeEvent
    public void pickUpItem(EntityItemPickupEvent event) {
        if (!event.item.worldObj.isRemote && event.entityPlayer != null) {
            ItemStack stack = event.item.getEntityItem();
            stack.stackSize = 1;
            Achievement achievement = registeredAchievements.get(stack);
            if (achievement != null) {
                EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
                if (!player.func_147099_x().hasAchievementUnlocked(achievement) && player.func_147099_x().canUnlockAchievement(achievement))
                    player.addStat(achievement, 1);
            }
            Logger.logInfo("ConditionItemPickup: Picked up " + stack.getDisplayName());
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
            if (name.split(":").length == 2) {
                String modID = name.split(":")[0];
                String itemName = name.split(":")[1];
                ItemStack stack;
                Item item = GameRegistry.findItem(modID, itemName);
                if (item != null) {
                    if (args.length == 2) {
                        int meta = Integer.parseInt(args[1]);
                        stack = new ItemStack(item, 1, meta);
                    } else
                        stack = new ItemStack(item);
                    if (stack != null)
                        registeredAchievements.put(stack, achievement);
                }
            }
        }
    }

    @Override
    public String conditionID() {
        return "pickupitem";
    }
}
