package net.lomeli.achieveson.conditions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.lib.NBTUtil;
import net.lomeli.achieveson.lib.ParsingUtil;

public class ConditionItemUse extends ConditionHandler {
    private List<ItemAction> rightClick;

    public ConditionItemUse() {
        rightClick = new ArrayList<ItemAction>();
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent event) {
        if (event.entityPlayer != null && event.entityPlayer.getCurrentEquippedItem() != null && !rightClick.isEmpty() && !event.entityPlayer.worldObj.isRemote && (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
            ItemStack usedStack = event.entityPlayer.getCurrentEquippedItem();
            Achievement achievement = null;
            ItemStack targetStack = null;
            for (ItemAction action : rightClick) {
                if (action != null && action.getItem() != null && action.getAchievement() != null) {
                    if (ParsingUtil.doStacksMatch(action.getItem(), usedStack)) {
                        targetStack = action.getItem();
                        achievement = action.getAchievement();
                        break;
                    }
                }
            }
            if (achievement != null && targetStack != null && targetStack.getItem() != null && FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
                EntityPlayerMP player = (EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(event.entityPlayer.dimension).func_152378_a(event.entityPlayer.getUniqueID());
                if (!player.func_147099_x().hasAchievementUnlocked(achievement) && player.func_147099_x().canUnlockAchievement(achievement) && playerMetCount(player, achievement, targetStack, event.entityPlayer.getCurrentEquippedItem()))
                    player.addStat(achievement, 1);
            }
        }
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {
        if (achievement != null && args != null && (args.length >= 2 && args.length <= 4)) {
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
            rightClick.add(new ItemAction(item, achievement));
        }
    }

    @Override
    public void registerEvent() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String conditionID() {
        return "useitem";
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

    private static class ItemAction {
        private final ItemStack item;
        private final Achievement achievement;

        public ItemAction(ItemStack item, Achievement achievement) {
            this.item = item;
            this.achievement = achievement;
        }

        public ItemStack getItem() {
            return item;
        }

        public Achievement getAchievement() {
            return achievement;
        }
    }
}
