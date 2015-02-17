package net.lomeli.achieveson.conditions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.MathHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.lib.ParsingUtil;

public class ConditionPlayer extends ConditionHandler {
    private List<AchieveCoords> coordsList;
    private List<ArmorSet> armorList;
    private List<XPAchievement> xpAchievements;

    public ConditionPlayer() {
        coordsList = new ArrayList<AchieveCoords>();
        armorList = new ArrayList<ArmorSet>();
        xpAchievements = new ArrayList<XPAchievement>();
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.player != null) {
            EntityPlayer player = event.player;
            Achievement achievement = null;
            // Position
            if (coordsList != null && !coordsList.isEmpty()) {
                AchieveCoords achieveCoords = null;
                for (AchieveCoords entry : coordsList) {
                    if (entry != null && entry.atLocation(event.player)) {
                        achieveCoords = entry;
                        break;
                    }
                }
                if (achieveCoords != null)
                    achievement = achieveCoords.getAchievement();
            }

            // Armor
            if (armorList != null && !armorList.isEmpty()) {
                ArmorSet set = null;
                for (ArmorSet armor : armorList) {
                    if (armor != null && armor.isWearingSet(player)) {
                        set = armor;
                        break;
                    }
                }
                if (set != null)
                    achievement = set.getAchievement();
            }

            if (xpAchievements != null && !xpAchievements.isEmpty()) {
                for (XPAchievement xpAch : xpAchievements) {
                    if (xpAch != null && xpAch.isAtLevel(player)) {
                        achievement = xpAch.getAchievement();
                        break;
                    }
                }
            }

            if (achievement != null && FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
                EntityPlayerMP playerMP = (EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(player.dimension).func_152378_a(player.getUniqueID());
                if (playerMP != null) {
                    if (!playerMP.func_147099_x().hasAchievementUnlocked(achievement) && playerMP.func_147099_x().canUnlockAchievement(achievement))
                        playerMP.addStat(achievement, 1);
                }
            }
        }
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {
        if (achievement != null && args != null && args.length >= 2) {
            String type = args[0];
            if (type.equalsIgnoreCase("pos")) {
                if (args.length == 4) {
                    int x = ParsingUtil.parseInt(args[1]);
                    int y = ParsingUtil.parseInt(args[2]);
                    int z = ParsingUtil.parseInt(args[3]);
                    coordsList.add(new AchieveCoords(x, y, z, achievement));
                }
            }
            if (type.equalsIgnoreCase("wear") || type.equalsIgnoreCase("armor")) {
                ItemStack[] gear = new ItemStack[4];
                boolean flag = true;
                for (int i = 0; i < gear.length; i++) {
                    if (i + 1 < args.length)
                        gear[i] = ParsingUtil.getStackFromString(args[i + 1]);
                }
                if (args.length == 6)
                    flag = Boolean.parseBoolean(args[5]);
                armorList.add(new ArmorSet(achievement, flag, gear));
            }
            if (type.equalsIgnoreCase("xplevel")) {
                int level = ParsingUtil.parseInt(args[1]);
                xpAchievements.add(new XPAchievement(achievement, level));
            }
        }
    }

    @Override
    public void registerEvent() {
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public String conditionID() {
        return "player";
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    private static class XPAchievement {
        private int level;
        private Achievement achievement;

        public XPAchievement(Achievement achievement, int level) {
            this.achievement = achievement;
            this.level = level;
        }

        public boolean isAtLevel(EntityPlayer player) {
            return player != null ? player.experienceLevel >= level : false;
        }

        public Achievement getAchievement() {
            return achievement;
        }
    }

    private static class AchieveCoords {
        private int x, y, z;
        private Achievement achievement;

        public AchieveCoords(int x, int y, int z, Achievement achievement) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.achievement = achievement;
        }

        public boolean atLocation(Entity entity) {
            return entity != null ? (MathHelper.floor_double(entity.posX) == this.x && MathHelper.floor_double(entity.boundingBox.minY) == this.y && MathHelper.floor_double(entity.posZ) == this.z) : false;
        }

        public Achievement getAchievement() {
            return this.achievement;
        }
    }

    private static class ArmorSet {
        private ItemStack[] set;
        private Achievement achievement;
        private boolean exact;

        public ArmorSet(Achievement achievement, boolean exact, ItemStack... items) {
            set = new ItemStack[4];
            for (int i = 0; i < set.length; i++)
                if (i < items.length) set[i] = items[i];
            this.achievement = achievement;
            this.exact = exact;
        }

        public boolean isWearingSet(EntityLivingBase entity) {
            int i = 0;
            for (int k = 0; k < set.length; k++) {
                ItemStack setItem = set[k];
                ItemStack entityItem = entity.getEquipmentInSlot(k + 1);
                if (setItem == null || setItem.getItem() == null) {
                    if (exact && (entityItem == null || entityItem.getItem() == null)) i++;
                    else if (!exact) i++;
                } else if (entityItem != null && entityItem.getItem() != null) {
                    if (entityItem.getItem() == setItem.getItem())
                        i++;
                }
            }
            return i >= 4;
        }

        public Achievement getAchievement() {
            return achievement;
        }
    }
}
