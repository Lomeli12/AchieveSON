package net.lomeli.achieveson.conditions;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.lib.NBTUtil;
import net.lomeli.achieveson.lib.ParsingUtil;

public class ConditionCraft extends ConditionHandler {
    private HashMap<ItemStack, Achievement> craftList;

    public ConditionCraft() {
        craftList = new HashMap<ItemStack, Achievement>();
    }

    @SubscribeEvent
    public void craftItemEvent(PlayerEvent.ItemCraftedEvent event) {
        if (event.player != null && !event.player.worldObj.isRemote && !craftList.isEmpty()) {
            Achievement achievement = null;
            ItemStack targetStack = null;
            for (Map.Entry<ItemStack, Achievement> entry : craftList.entrySet()) {
                if (entry != null && ParsingUtil.doStacksMatch(entry.getKey(), event.crafting)) {
                    achievement = entry.getValue();
                    targetStack = entry.getKey();
                    break;
                }
            }
            if (achievement != null && targetStack != null && targetStack.getItem() != null) {
                EntityPlayerMP player = (EntityPlayerMP) event.player;
                if (!player.func_147099_x().hasAchievementUnlocked(achievement) && player.func_147099_x().canUnlockAchievement(achievement) && playerMetCount(player, achievement, targetStack, event.crafting))
                    player.addStat(achievement, 1);
            }
        }
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {
        if (achievement != null && args != null && (args.length >= 1 || args.length <= 3)) {
            String itemName = args[0];
            int count = 1;
            if (args.length >= 2) {
                if (args[1].startsWith("meta="))
                    itemName += " " + args[1];
                if (args[1].startsWith("count="))
                    count = ParsingUtil.getCountFromString(args[1]);
                if (args.length == 3 && args[2].startsWith("count="))
                    count = ParsingUtil.getCountFromString(args[2]);
            }
            ItemStack item = ParsingUtil.getStackFromString(itemName);
            item.stackSize = count;
            if (item == null && item.getItem() != null)
                return;
            craftList.put(item, achievement);
        }
    }

    @Override
    public void registerEvent() {
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public String conditionID() {
        return "craft";
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    public boolean playerMetCount(EntityPlayer player, Achievement achievement, ItemStack targetStack, ItemStack stack) {
        if (player != null && achievement != null && stack != null && stack.getItem() != null && targetStack != null && targetStack.getItem() != null) {
            int count = 1 + NBTUtil.getInt(player, achievement.statId);
            NBTUtil.setInt(player, achievement.statId, count);
            if (count >= targetStack.stackSize) {
                NBTUtil.removeTag(player, achievement.statId);
                return true;
            }
        }
        return false;
    }
}
