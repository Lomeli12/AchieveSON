package net.lomeli.achieveson.achievement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.util.*;

import net.minecraft.stats.Achievement;

import net.minecraftforge.common.AchievementPage;

import net.lomeli.achieveson.Logger;

public class AchievementJSON {
    public static HashMap<String, AchievementJSON> jsonList = new HashMap<String, AchievementJSON>();
    private File jsonFile;
    private String pageTitle;
    public List<SubAchievement> subAchievementList;

    public AchievementJSON(File file) {
        jsonFile = file;
        subAchievementList = new ArrayList<SubAchievement>();
    }

    public void loadAchievements() {
        if (parseJSON()) {
            jsonList.put(pageTitle.replace(" ", "-"), this);
            List<Achievement> achievements = new ArrayList<Achievement>();
            for (SubAchievement subs : subAchievementList) {
                if (subs != null) {
                    subs.loadAchievement();
                    achievements.add(subs.getAchievement());
                }
            }
            Achievement[] array = new Achievement[achievements.size()];
            for (int i = 0; i < array.length; i++)
                array[i] = achievements.get(i);
            Logger.logInfo("Creating page for " + pageTitle + " with " + array.length + " achievements.");
            AchievementPage.registerAchievementPage(new AchievementPage(pageTitle, array));
        }
    }

    private boolean parseJSON() {
        if (jsonFile != null && jsonFile.exists()) {
            JsonParser parser = new JsonParser();
            try {
                JsonReader reader = reader = new JsonReader(new FileReader(jsonFile));
                if (reader != null) {
                    JsonElement element = parser.parse(reader);
                    JsonObject jsonObject = element.getAsJsonObject();
                    pageTitle = jsonObject.get("pageTitle").getAsString();
                    String pageid = pageTitle.replace(" ", "-");
                    if (jsonObject.has("achievements") && jsonObject.get("achievements").isJsonObject()) {
                        JsonObject achievements = jsonObject.getAsJsonObject("achievements");
                        List<String> names = new ArrayList<String>();
                        for (Map.Entry<String, JsonElement> entry : achievements.entrySet()) {
                            if (entry.getKey().isEmpty() || names.contains(entry.getKey())) {
                                Logger.logWarning("Achievement with same id already registered, ignoring...");
                                continue;
                            }
                            String id = entry.getKey();
                            names.add(id);
                            JsonObject subAchievement = entry.getValue().getAsJsonObject();
                            if (subAchievement != null) {
                                String type = subAchievement.get("conditionType").getAsString();
                                String param = subAchievement.get("params").getAsString();
                                String item = subAchievement.get("itemIcon").getAsString();
                                int x = subAchievement.get("xPos").getAsInt();
                                int y = subAchievement.get("yPos").getAsInt();
                                String[] params = param.split(" ");
                                SubAchievement sub;
                                if (subAchievement.has("parentAchievement"))
                                    sub = new SubAchievement(pageid, id, x, y, type, item, params, subAchievement.get("parentAchievement").getAsString());
                                else
                                    sub = new SubAchievement(pageid, id, x, y, type, item, params);
                                if (sub != null)
                                    subAchievementList.add(sub);
                            }
                        }
                    }

                    return true;
                }
            } catch (Exception e) {
                Logger.logError("Something went wrong while parsing " + jsonFile.getName() + ". Please check and fix any errors, then restart Minecraft.");
                e.printStackTrace();
            }
        }
        Logger.logWarning("Null JSON file, ignoring...");
        return false;
    }
}
