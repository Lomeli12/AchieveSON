package net.lomeli.achieveson.conditions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;

import net.lomeli.achieveson.api.ConditionHandler;

//TODO FINISH THIS!!!!
public class ConditionItemUse extends ConditionHandler {
    private List<ItemAction> leftClick, rightClick;

    public ConditionItemUse() {
        leftClick = new ArrayList<ItemAction>();
        rightClick = new ArrayList<ItemAction>();
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {

    }

    @Override
    public void registerEvent() {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String conditionID() {
        return null;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    private static class ItemAction {
        private ItemStack item;
        private Achievement achievement;
    }
}
