package net.lomeli.achieveson;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import net.lomeli.achieveson.achievement.AchievementJSON;
import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.conditions.ConditionItemPickup;
import net.lomeli.achieveson.conditions.ConditionManager;

@Mod(modid = AchieveSON.MOD_ID, name = AchieveSON.MOD_NAME, version = AchieveSON.VERSION)
public class AchieveSON {
    public static final String MOD_ID = "achieveson";
    public static final String MOD_NAME = "AchieveSON";
    public static final String VERSION = "1.0.0";

    public static File achievementFolder;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConditionHandler.registerHandler(ConditionItemPickup.class);

        achievementFolder = new File(event.getModConfigurationDirectory(), "achievements");
        if (!achievementFolder.exists()) {
            Logger.logWarning("Achievement folder does not exists!");
            achievementFolder.mkdir();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (achievementFolder.isDirectory()) {
            File[] files = achievementFolder.listFiles();
            for (File file : files) {
                if (FilenameUtils.getExtension(file.getAbsolutePath()).equalsIgnoreCase("json")) {
                    Logger.logInfo("Parsing " + file.getName() + " for achievements...");
                    new AchievementJSON(file).loadAchievements();
                }
            }
        }
    }
}
