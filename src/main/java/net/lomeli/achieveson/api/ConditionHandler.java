package net.lomeli.achieveson.api;

import net.minecraft.stats.Achievement;

public abstract class ConditionHandler {

    public static IConditionManager conditionManager;

    public abstract void registerAchievementCondition(Achievement achievement, String... args);

    /**
     * Typically, condition handlers will have FML/Forge events. This method is called to register those events
     */
    public abstract void registerEvent();

    /**
     * This is what is what needs to be put in "conditionType" in the json file to use this condition
     *
     * @return
     */
    public abstract String conditionID();

    /**
     * Set to true if you're using a client side event. Use {@link net.lomeli.achieveson.api.IConditionManager#sendAchievementPacket} to give the player their achievement.
     */
    public abstract boolean isClientSide();

    public static final void registerHandler(Class<? extends ConditionHandler> clazz) {
        conditionManager.registerAchievements(clazz);
    }
}
