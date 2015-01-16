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
            Achievement achievement = getAchievementForBlock(breakList, event.block, event.blockMetadata);
            if (player != null && achievement != null) {
                if (!player.func_147099_x().hasAchievementUnlocked(achievement) && player.func_147099_x().canUnlockAchievement(achievement))
                    player.addStat(achievement, 1);
            }
        }
    }

    @SubscribeEvent
    public void blockPlaceEvent(BlockEvent.PlaceEvent event) {
        if (event.block != null && !event.world.isRemote && event.player != null) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            Achievement achievement = getAchievementForBlock(placeList, event.block, event.blockMetadata);
            if (player != null && achievement != null) {
                if (!player.func_147099_x().hasAchievementUnlocked(achievement) && player.func_147099_x().canUnlockAchievement(achievement))
                    player.addStat(achievement, 1);
            }
        }
    }

    @SubscribeEvent
    public void rightClickBlockEvent(PlayerInteractEvent event) {
        if (event.entityPlayer != null && !event.world.isRemote && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
            Block block = event.world.getBlock(event.x, event.y, event.z);
            int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
            Achievement achievement = getAchievementForBlock(rightClickList, block, meta);
            if (player != null && achievement != null) {
                if (!player.func_147099_x().hasAchievementUnlocked(achievement) && player.func_147099_x().canUnlockAchievement(achievement))
                    player.addStat(achievement, 1);
            }
        }
    }

    public Achievement getAchievementForBlock(List<BlockInfo> list, Block block, int meta) {
        if (list != null && !list.isEmpty() && block != null) {
            for (BlockInfo info : list)
                if (info != null && info.match(block, meta)) return info.getAchievement();
        }
        return null;
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {
        if (achievement != null && args != null && (args.length == 2 || args.length == 3)) {
            String type = args[0];
            ItemStack item = args.length == 2 ? ParsingUtil.getStackFromString(args[1]) : args.length == 3 ? ParsingUtil.getStackFromString(args[1], Integer.parseInt(args[2])) : null;
            if (item == null) return;
            Block block = Block.getBlockFromItem(item.getItem());
            if (block == null) return;
            if (type.equalsIgnoreCase("place"))
                placeList.add(new BlockInfo(achievement, block, item.getItemDamage()));
            else if (type.equalsIgnoreCase("break"))
                breakList.add(new BlockInfo(achievement, block, item.getItemDamage()));
            else if (type.equalsIgnoreCase("interact"))
                rightClickList.add(new BlockInfo(achievement, block, item.getItemDamage()));
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
        private int metadata;
        private Achievement achievement;

        public BlockInfo(Achievement achievement, Block block, int meta) {
            this.achievement = achievement;
            this.block = block;
            this.metadata = meta;
        }

        public boolean match(Block bk, int meta) {
            return bk != null && block != null && block == bk && (metadata == Short.MAX_VALUE ? true : metadata == meta);
        }

        public Achievement getAchievement() {
            return this.achievement;
        }
    }
}