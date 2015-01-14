package net.lomeli.achieveson.api;

import net.minecraft.stats.Achievement;

public abstract class ConditionHandler {
    public static IConditionManager conditionManager;

    public abstract void registerAchievementCondition(Achievement achievement, String... args);

    /** Typically, condition handlers will have FML/Forge events. This method is called to register those events */
    public abstract void registerEvent();

    public abstract String conditionID();

    public static final void registerHandler(Class<? extends ConditionHandler> clazz) {
        if (conditionManager == null) {
            try {
                Class cl = Class.forName("net.lomeli.achieveson.conditions.ConditionManager");
                conditionManager = (IConditionManager) cl.getField("instance").get(clazz);
            } catch (Exception e) {
                return;
            }
        }
        conditionManager.registerAchievements(clazz);
    }
}
