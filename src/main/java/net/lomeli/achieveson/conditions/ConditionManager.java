package net.lomeli.achieveson.conditions;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;

import cpw.mods.fml.common.FMLCommonHandler;

import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.api.IConditionManager;
import net.lomeli.achieveson.lib.Logger;
import net.lomeli.achieveson.network.MessageUnlockAchievement;
import net.lomeli.achieveson.network.PacketHandler;

public class ConditionManager implements IConditionManager {
    public static ConditionManager instance;
    private HashMap<String, ConditionHandler> registeredConditions;

    public ConditionManager() {
        registeredConditions = new HashMap<String, ConditionHandler>();
    }

    public static ConditionManager getInstance() {
        if (instance == null)
            instance = new ConditionManager();
        return instance;
    }

    @Override
    public void registerAchievements(Class<? extends ConditionHandler> clazz) {
        try {
            Logger.logInfo("Registering " + clazz.getName() + " as condition handler...");
            ConditionHandler conditionHandler = clazz.newInstance();
            
            if (conditionHandler.isClientSide()) {
                if (FMLCommonHandler.instance().getEffectiveSide().isClient())
                    conditionHandler.registerEvent();
            } else
                conditionHandler.registerEvent();
            registeredConditions.put(conditionHandler.conditionID(), conditionHandler);
        } catch (Exception e) {
            Logger.logError("Something went wrong when registering " + clazz.getName());
            e.printStackTrace();
        }
    }

    @Override
    public ConditionHandler getConditionHandler(String id) {
        return registeredConditions.get(id);
    }

    @Override
    public void sendAchievementPacket(Achievement achievement, EntityPlayer player) {
        PacketHandler.INSTANCE.sendToServer(new MessageUnlockAchievement(achievement, player));
    }
}
