package net.lomeli.achieveson.api;

import net.minecraft.stats.Achievement;

public interface IConditionManager {
    /**
     * Register new which achievements will listen for this condition
     * @param clazz
     */
    public void registerAchievements(Class<? extends ConditionHandler> clazz);

    public ConditionHandler getConditionHandler(String id);

    /**
     * Only really used for Client-Side conditions. Look at {@link net.lomeli.achieveson.conditions.ConditionOpenGui}
     * for an example
     * @param achievement
     */
    public void sendAchievementPacket(Achievement achievement);
}
