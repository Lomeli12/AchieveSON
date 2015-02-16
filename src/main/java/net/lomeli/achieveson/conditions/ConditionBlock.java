package net.lomeli.achieveson.conditions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.lib.NBTUtil;
import net.lomeli.achieveson.lib.ParsingUtil;

public class ConditionBlock extends ConditionHandler {
    private List<BlockInfo> breakList, placeList, rightClickList;

    public ConditionBlock() {
        breakList = new ArrayList<BlockInfo>();
        placeList = new ArrayList<BlockInfo>();
        rightClickList = new ArrayList<BlockInfo>();
    }

    @SubscribeEvent
    public void blockBreakEvent(BlockEvent.BreakEvent event) {
        if (event.block != null && !event.world.isRemote && event.getPlayer() != null) {
            EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
            BlockInfo info = getBlockInfo(breakList, event.block, event.blockMetadata);
            if (player != null && info != null)
                playerUnlockedAchieve(player, info);
        }
    }

    @SubscribeEvent
    public void blockPlaceEvent(BlockEvent.PlaceEvent event) {
        if (event.block != null && !event.world.isRemote && event.player != null) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            BlockInfo info = getBlockInfo(placeList, event.block, event.blockMetadata);
            if (player != null && info != null)
                playerUnlockedAchieve(player, info);
        }
    }

    @SubscribeEvent
    public void rightClickBlockEvent(PlayerInteractEvent event) {
        if (event.entityPlayer != null && !event.world.isRemote && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
            Block block = event.world.getBlock(event.x, event.y, event.z);
            int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
            BlockInfo info = getBlockInfo(rightClickList, block, meta);
            if (player != null && info != null)
                playerUnlockedAchieve(player, info);
        }
    }

    public void playerUnlockedAchieve(EntityPlayerMP player, BlockInfo info) {
        if (info != null && info.block != null && info.achievement != null && player != null && !player.func_147099_x().hasAchievementUnlocked(info.getAchievement()) && player.func_147099_x().canUnlockAchievement(info.getAchievement())) {
            int count = 1 + NBTUtil.getInt(player, info.achievement.statId);
            NBTUtil.setInt(player, info.achievement.statId, count);
            if (count >= info.getCount()) {
                player.addStat(info.getAchievement(), 1);
                NBTUtil.removeTag(player, info.achievement.statId);
            }
        }
    }

    public BlockInfo getBlockInfo(List<BlockInfo> list, Block block, int meta) {
        if (list != null && !list.isEmpty() && block != null) {
            for (BlockInfo info : list)
                if (info != null && info.match(block, meta)) return info;
        }
        return null;
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {
        if (achievement != null && args != null && (args.length >= 2 && args.length <= 4)) {
            String type = args[0];
            String itemName = args[1];
            int meta = 0, count = 1;
            if (args.length > 2) {
                String ex0 = args[2];
                String ex1 = args.length == 4 ? args[3] : null;
                if (ex0.startsWith("count="))
                    count = ParsingUtil.parseInt(ex0.substring(6));
                else
                    meta = ParsingUtil.parseInt(ex0);
                if (ex1 != null && ex1.startsWith("count="))
                    count = ParsingUtil.parseInt(ex1.substring(6));
            }
            ItemStack item = ParsingUtil.getStackFromString(itemName, meta);
            if (item == null)
                return;
            Block block = Block.getBlockFromItem(item.getItem());
            if (block == null)
                return;
            if (type.equalsIgnoreCase("place"))
                placeList.add(new BlockInfo(achievement, block, item.getItemDamage(), count));
            else if (type.equalsIgnoreCase("break"))
                breakList.add(new BlockInfo(achievement, block, item.getItemDamage(), count));
            else if (type.equalsIgnoreCase("interact"))
                rightClickList.add(new BlockInfo(achievement, block, item.getItemDamage(), count));
        }
    }

    @Override
    public void registerEvent() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String conditionID() {
        return "block";
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    private static class BlockInfo {
        private Block block;
        private int metadata, count;
        private Achievement achievement;

        public BlockInfo(Achievement achievement, Block block, int meta, int count) {
            this.achievement = achievement;
            this.block = block;
            this.metadata = meta;
            this.count = count;
        }

        public boolean match(Block bk, int meta) {
            return bk != null && block != null && block == bk && (metadata == Short.MAX_VALUE ? true : metadata == meta);
        }

        public Achievement getAchievement() {
            return this.achievement;
        }

        public int getCount() {
            return count;
        }
    }
}