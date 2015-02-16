package net.lomeli.achieveson.conditions;

import java.util.HashMap;

import net.minecraft.stats.Achievement;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.lomeli.achieveson.api.ConditionHandler;

public class ConditionOpenGui extends ConditionHandler {
    private HashMap<String, Achievement> classList;

    public ConditionOpenGui() {
        classList = new HashMap<String, Achievement>();
    }

    @SubscribeEvent
    public void openGuiEvent(GuiOpenEvent event) {
        if (event.gui != null && classList.containsKey(event.gui.getClass().getName())) {
            Achievement achievement = classList.get(event.gui.getClass().getName());
            if (achievement != null)
                ConditionManager.getInstance().sendAchievementPacket(achievement);
        }
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {
        if (achievement != null && args != null && args.length == 1)
            classList.put(args[0], achievement);
    }

    @Override
    public void registerEvent() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String conditionID() {
        return "opengui";
    }

    @Override
    public boolean isClientSide() {
        return true;
    }
}
