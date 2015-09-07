package net.lomeli.achieveson.achievement;

public class AchievementFile {
    private String pageTitle;
    private String langZip;
    private AchievementInfo[] achievements;

    public AchievementFile(String pageTitle, String langZip, AchievementInfo... achievements) {
        this.pageTitle = pageTitle;
        this.langZip = langZip;
        this.achievements = achievements;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getLangZip() {
        return langZip;
    }

    public AchievementInfo[] getAchievementInfo() {
        return achievements;
    }
}
