package net.lomeli.achieveson.achievement;

import com.google.gson.Gson;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.StringTranslate;

import net.minecraftforge.common.AchievementPage;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

import net.lomeli.achieveson.AchieveSON;
import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.conditions.ConditionManager;
import net.lomeli.achieveson.lib.Logger;
import net.lomeli.achieveson.lib.ParsingUtil;

public class AchievementHandler {

    public static void loadAchievements() {
        Gson gson = new Gson();
        File[] files = AchieveSON.achievementFolder.listFiles();
        for (File file : files) {
            if (FilenameUtils.getExtension(file.getAbsolutePath()).equalsIgnoreCase("json")) {
                try {
                    Logger.logInfo("Parsing " + file.getName() + " for achievements...");
                    FileReader reader = new FileReader(file);
                    AchievementFile achievementFile = gson.fromJson(reader, AchievementFile.class);
                    if (achievementFile != null)
                        createPage(file, achievementFile);
                    Logger.logInfo("-------------------------------------");
                } catch (Exception e) {
                    // Probably the user's fault, so I don't fucking care.
                    e.printStackTrace();
                }
            }
        }
    }

    public static void createPage(File file, AchievementFile achievementFile) {
        String langZip = achievementFile.getLangZip();
        if (langZip != null && !langZip.isEmpty()) {
            File langFile = new File(file.getParentFile(), langZip + (langZip.endsWith(".zip") ? "" : ".zip"));
            if (langFile.exists()) {
                Logger.logInfo("Attempting to inject language zip " + langZip);
                injectLanguage(langFile, FMLCommonHandler.instance().getEffectiveSide());
            } else
                Logger.logWarning("Could not find language zip!");
        }
        String pageId = achievementFile.getPageTitle().replace(" ", "-");
        List<Achievement> achievements = new ArrayList<Achievement>();
        for (AchievementInfo info : achievementFile.getAchievementInfo()) {
            Achievement achieve = createAchievement(achievements, info, pageId);
            if (achieve != null)
                achievements.add(achieve);
        }
        Achievement[] array = new Achievement[achievements.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = achievements.get(i);
        Logger.logInfo("Created page for " + achievementFile.getPageTitle());
        AchievementPage.registerAchievementPage(new AchievementPage(achievementFile.getPageTitle(), array));
    }

    public static Achievement createAchievement(List<Achievement> list, AchievementInfo info, String pageID) {
        Achievement achievement = null;
        Achievement parentAchievement = null;
        if (info.getParentAchievement() != null && !info.getParentAchievement().isEmpty()) {
            for (Achievement achieve : list) {
                if (achieve.statId.equals("achievement." + pageID + "." + info.getParentAchievement())) {
                    Logger.logInfo("Adding " + achieve.statId + " as parent for " + info.getName());
                    parentAchievement = achieve;
                    break;
                }
            }
        }
        int xPos = info.getxPos();
        int yPos = info.getyPos();
        ItemStack item = ParsingUtil.getStackFromString(info.getItemIcon());
        Logger.logInfo("Creating achievement " + info.getName() + " for page " + pageID);
        achievement = new Achievement("achievement." + pageID + "." + info.getName(), pageID + "." + info.getName(), xPos, yPos, item != null ? item : new ItemStack(Blocks.stone), parentAchievement).registerStat();
        if (achievement != null && info.getConditionType() != null && !info.getConditionType().isEmpty()) {
            Logger.logInfo("Assigning achievement " + info.getName() + " to condition handler " + info.getConditionType() + ".");
            ConditionHandler condition = ConditionManager.getInstance().getConditionHandler(info.getConditionType());
            if (condition != null)
                condition.registerAchievementCondition(achievement, info.getParams().split(" "));
            else
                Logger.logWarning("Condition handler with that id does not exist!");
        }
        return achievement;
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
}
