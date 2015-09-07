package net.lomeli.achieveson.achievement;

public class AchievementInfo {
    private String id;
    private String conditionType;
    private String params;
    private String itemIcon;
    private int xPos;
    private int yPos;
    private String parentAchievement;

    public AchievementInfo(String id, String conditionType, String params, String itemIcon, int x, int y, String parent) {
        this.id = id;
        this.conditionType = conditionType;
        this.params = params;
        this.itemIcon = itemIcon;
        this.xPos = x;
        this.yPos = y;
        this.parentAchievement = parent;
    }

    public AchievementInfo(String id, String conditionType, String params, String itemIcon, int x, int y) {
        this(id, conditionType, params, itemIcon, x, y, null);
    }

    public String getID() {
        return id;
    }

    public String getConditionType() {
        return conditionType;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public String getParams() {
        return params;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public String getParentAchievement() {
        return parentAchievement;
    }
}
