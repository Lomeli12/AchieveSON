package net.lomeli.achieveson;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import net.lomeli.achieveson.achievement.AchievementJSONFile;
import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.conditions.*;
import net.lomeli.achieveson.lib.Logger;
import net.lomeli.achieveson.network.PacketHandler;

@Mod(modid = AchieveSON.MOD_ID, name = AchieveSON.MOD_NAME, version = AchieveSON.VERSION)
public class AchieveSON {
    public static final String MOD_ID = "achieveson";
    public static final String MOD_NAME = "AchieveSON";
    public static final String VERSION = "1.0.0";

    public static File achievementFolder;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Logger.logInfo("Initializing basic packet handler...");
        PacketHandler.init();
        Logger.logInfo("Registering base condition handlers...");
        ConditionHandler.registerHandler(ConditionItemPickup.class);
        ConditionHandler.registerHandler(ConditionKillEntity.class);
        ConditionHandler.registerHandler(ConditionBlock.class);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            ConditionHandler.registerHandler(ConditionOpenGui.class);
        ConditionHandler.registerHandler(ConditionCraft.class);
        ConditionHandler.registerHandler(ConditionPlayer.class);

        achievementFolder = new File(event.getModConfigurationDirectory(), "achievements");
        if (!achievementFolder.exists()) {
            Logger.logWarning("Achievement folder does not exists! Creating...");
            achievementFolder.mkdir();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (achievementFolder.isDirectory()) {
            Logger.logInfo("Reading json files...");
            File[] files = achievementFolder.listFiles();
            for (File file : files) {
                if (FilenameUtils.getExtension(file.getAbsolutePath()).equalsIgnoreCase("json")) {
                    Logger.logInfo("Parsing " + file.getName() + " for achievements...");
                    new AchievementJSONFile(file).loadAchievements();
                    Logger.logInfo("-------------------------------------");
                }
            }
        }
    }
}
