package net.lomeli.achieveson.api;

import net.minecraft.stats.Achievement;

public abstract class ConditionHandler {

    public abstract void registerAchievementCondition(Achievement achievement, String... args);

    /**
     * Typically, condition handlers will have FML/Forge events. This method is called to register those events
     */
    public abstract void registerEvent();

    public abstract String conditionID();

    /**
     * Set to true if you're using a client side event. Use {@link net.lomeli.achieveson.api.IConditionManager#sendAchievementPacket} to give the player their achievement.
     */
    public abstract boolean isClientSide();

    private static IConditionManager conditionManager;

    public static IConditionManager getConditionManager() {
        if (conditionManager == null) {
            try {
                Class cl = Class.forName("net.lomeli.achieveson.conditions.ConditionManager");
                conditionManager = (IConditionManager) cl.getMethod("getInstance").invoke(null);
            } catch (Exception e) {
            }
        }
        return conditionManager;
    }

    public static final void registerHandler(Class<? extends ConditionHandler> clazz) {
        getConditionManager().registerAchievements(clazz);
    }
}
