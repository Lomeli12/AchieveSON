package net.lomeli.achieveson.conditions;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import net.lomeli.achieveson.api.ConditionHandler;
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
            for (Map.Entry<ItemStack, Achievement> entry : craftList.entrySet()) {
                if (entry != null && ParsingUtil.doStacksMatch(event.crafting, entry.getKey())) {
                    achievement = entry.getValue();
                    break;
                }
            }
            if (achievement != null) {
                EntityPlayerMP player = (EntityPlayerMP) event.player;
                if (!player.func_147099_x().hasAchievementUnlocked(achievement) && player.func_147099_x().canUnlockAchievement(achievement))
                    player.addStat(achievement, 1);
            }
        }
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {
        if (achievement != null && args != null && (args.length == 1 || args.length == 2)) {
            ItemStack stack = args.length == 2 ? ParsingUtil.getStackFromString(args[0], Integer.parseInt(args[1])) : ParsingUtil.getStackFromString(args[0]);
            if (stack != null && stack.getItem() != null)
                craftList.put(stack, achievement);
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
}
