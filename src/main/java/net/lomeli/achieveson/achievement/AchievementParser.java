package net.lomeli.achieveson.achievement;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import cpw.mods.fml.common.registry.GameRegistry;

import net.lomeli.achieveson.Logger;
import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.conditions.ConditionManager;

public class AchievementParser {
    private String pageID, id, type, parentID;
    private ItemStack item;
    private String[] params;
    private int xPos, yPos;
    private Achievement achievement;

    public AchievementParser(String pageID, String id, int x, int y, String type, String item, String[] params) {
        this.pageID = pageID;
        this.id = id;
        this.xPos = x;
        this.yPos = y;
        this.type = type;
        String[] itemInfo = item.split(":");
        if (itemInfo != null && (itemInfo.length == 2 || itemInfo.length == 3)) {
            ItemStack stack = new ItemStack(GameRegistry.findItem(itemInfo[0], itemInfo[1]));
            if (itemInfo.length == 3)
                stack.setItemDamage(Integer.parseInt(itemInfo[2]));
            this.item = stack;
        }
        this.params = params;
    }

    public AchievementParser(String pageID, String id, int x, int y, String type, String item, String[] params, String parentID) {
        this(pageID, id, x, y, type, item, params);
        this.parentID = parentID;
    }

    public String getId() {
        return this.id;
    }

    public void loadAchievement(AchievementJSONFile page) {
        if (item != null) {
            Achievement parentAchievement = null;
            if (parentID != null && !parentID.isEmpty()) {
                if (page != null && !page.achievementParserList.isEmpty()) {
                    for (AchievementParser sub : page.achievementParserList) {
                        if (sub != null && sub.getId().equals(parentID)) {
                            parentAchievement = sub.getAchievement();
                            break;
                        }
                    }
                    if (parentAchievement == null)
                        Logger.logWarning("Parent for " + id + " achievement given, but not found! This can happen if no achievements have this id exist or the parent is registered after the child.");
                }
            }
            Logger.logInfo("Creating achievement " + id + " for page " + pageID);
            achievement = new Achievement("achievement." + pageID + "." + id, pageID + "." + id, xPos, yPos, item, parentAchievement).registerStat();
        }
        if (achievement != null && type != null && !type.isEmpty()) {
            Logger.logInfo("Assigning achievement " + id + " to condition handler.");
            ConditionHandler condition = ConditionManager.getInstance().getConditionHandler(type);
            if (condition != null)
                condition.registerAchievementCondition(achievement, params);
            else
                Logger.logWarning("Condition handler with that id does not exist!");
        }
    }

    public Achievement getAchievement() {
        return achievement;
    }
}
