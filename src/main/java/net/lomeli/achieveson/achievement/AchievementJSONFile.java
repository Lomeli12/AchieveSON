package net.lomeli.achieveson.achievement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.stats.Achievement;
import net.minecraft.util.StringTranslate;

import net.minecraftforge.common.AchievementPage;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

import net.lomeli.achieveson.lib.Logger;

public class AchievementJSONFile {
    public static HashMap<String, AchievementJSONFile> jsonList = new HashMap<String, AchievementJSONFile>();
    public List<AchievementParser> achievementParserList;
    private File jsonFile;
    private String pageTitle, langZip;

    public AchievementJSONFile(File file) {
        jsonFile = file;
        achievementParserList = new ArrayList<AchievementParser>();
    }

    public static void injectLanguage(File source, Side side) {
        try {
            ZipFile zf = new ZipFile(source);
            for (ZipEntry ze : Collections.list(zf.entries())) {
                Matcher matcher = Pattern.compile("(?:.+/|)([\\w_-]+).lang").matcher(ze.getName());
                if (matcher.matches()) {
                    String lang = matcher.group(1);
                    FMLLog.fine("Injecting found translation data for lang %s in zip file %s at %s into language system", lang, source.getName(), ze.getName());
                    LanguageRegistry.instance().injectLanguage(lang, StringTranslate.parseLangFile(zf.getInputStream(ze)));
                    // Ensure en_US is available to StringTranslate on the server
                    if ("en_US".equals(lang) && side == Side.SERVER)
                        StringTranslate.inject(zf.getInputStream(ze));
                }
            }
            zf.close();
        } catch (Exception e) {
            Logger.logError("Could not inject language file!");
            e.printStackTrace();
        }
    }

    public void loadAchievements() {
        if (parseJSON()) {
            if (langZip != null && !langZip.isEmpty()) {
                File langFile = new File(jsonFile.getParentFile(), langZip + (langZip.endsWith(".zip") ? "" : ".zip"));
                if (langFile.exists()) {
                    Logger.logInfo("Attempting to inject language zip " + langZip);
                    injectLanguage(langFile, FMLCommonHandler.instance().getEffectiveSide());
                } else
                    Logger.logWarning("Could not find language zip!");
            }
            jsonList.put(pageTitle.replace(" ", "-"), this);
            List<Achievement> achievements = new ArrayList<Achievement>();
            for (AchievementParser subs : achievementParserList) {
                if (subs != null) {
                    subs.loadAchievement(this);
                    if (subs.getAchievement() != null) {
                        achievements.add(subs.getAchievement());
                        jsonList.replace(pageTitle.replace(" ", "-"), this);
                    } else
                        Logger.logError("Failed to load " + subs.getId());
                }
            }
            Achievement[] array = new Achievement[achievements.size()];
            for (int i = 0; i < array.length; i++)
                array[i] = achievements.get(i);
            Logger.logInfo("Creating page for " + pageTitle + " with " + array.length + " achievements.");
            if (pageTitle != null)
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
                    if (jsonObject.has("langZip"))
                        langZip = jsonObject.get("langZip").getAsString();
                    else
                        Logger.logWarning("Achievement page does not have localization zip! You can still use it, but everything will show up with unlocalized names! Use tag \"langZip\" to set localization zip file.");
                    String pageid = pageTitle.replace(" ", "-");
                    if (jsonObject.has("achievements") && jsonObject.get("achievements").isJsonObject()) {
                        JsonObject achievements = jsonObject.getAsJsonObject("achievements");
                        List<String> names = new ArrayList<String>();
                        for (Map.Entry<String, JsonElement> entry : achievements.entrySet()) {
                            if (entry.getKey().isEmpty() || names.contains(entry.getKey())) {
                                Logger.logWarning("Achievement with same id already registered, ignoring...");
                                continue;
                            }
                            if (entry.getValue().isJsonObject()) {
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
                                    AchievementParser sub;
                                    if (subAchievement.has("parentAchievement"))
                                        sub = new AchievementParser(pageid, id, x, y, type, item, params, subAchievement.get("parentAchievement").getAsString());
                                    else
                                        sub = new AchievementParser(pageid, id, x, y, type, item, params);
                                    if (sub != null)
                                        achievementParserList.add(sub);
                                }
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
