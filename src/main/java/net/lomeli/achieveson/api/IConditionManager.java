package net.lomeli.achieveson.api;

public interface IConditionManager {
    public void registerAchievements(Class<? extends ConditionHandler> clazz);

    public ConditionHandler getConditionHandler(String id);
}
