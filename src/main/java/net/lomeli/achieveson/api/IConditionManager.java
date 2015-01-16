package net.lomeli.achieveson.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;

public interface IConditionManager {
    public void registerAchievements(Class<? extends ConditionHandler> clazz);

    public ConditionHandler getConditionHandler(String id);

    public void sendAchievementPacket(Achievement achievement, EntityPlayer player);
}
