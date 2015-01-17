package net.lomeli.achieveson.conditions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.lomeli.achieveson.api.ConditionHandler;
import net.lomeli.achieveson.lib.NBTUtil;
import net.lomeli.achieveson.lib.ParsingUtil;

public class ConditionKillEntity extends ConditionHandler {
    private List<KillTarget> targets;

    public ConditionKillEntity() {
        targets = new ArrayList<KillTarget>();
    }

    @SubscribeEvent
    public void entityDeathEvent(LivingDeathEvent event) {
        if (event.entityLiving != null && !event.entityLiving.worldObj.isRemote && event.source != null) {
            if (event.source.getSourceOfDamage() != null && damageFromPlayer(event.source) && !targets.isEmpty()) {
                KillTarget info = matchInfo(event.entityLiving.getClass());
                EntityPlayerMP player = getPlayerFromSource(event.source);
                if (info != null && player != null && !player.func_147099_x().hasAchievementUnlocked(info.getAchievement()) && player.func_147099_x().canUnlockAchievement(info.getAchievement())) {
                    int count = 1 + NBTUtil.getInt(player, info.getAchievement().statId);
                    NBTUtil.setInt(player, info.getAchievement().statId, count);
                    if (count >= info.getCount()) {
                        player.addStat(info.getAchievement(), 1);
                        NBTUtil.removeTag(player, info.getAchievement().statId);
                    }
                }
            }
        }
    }

    public EntityPlayerMP getPlayerFromSource(DamageSource source) {
        return damageFromPlayer(source) ? (EntityPlayerMP) source.getEntity() : null;
    }

    public KillTarget matchInfo(Class<?> clazz) {
        if (clazz != null && targets != null && !targets.isEmpty()) {
            for (KillTarget targetInfo : targets)
                if (targetInfo != null && targetInfo.match(clazz)) return targetInfo;
        }
        return null;
    }

    public boolean damageFromPlayer(DamageSource source) {
        return source.getEntity() != null && source.getEntity() instanceof EntityPlayer;
    }

    @Override
    public void registerAchievementCondition(Achievement achievement, String... args) {
        if (achievement != null && args != null && (args.length == 1 || args.length == 2)) {
            int count = 1;
            if (args.length == 2 && args[1].startsWith("count="))
                count = ParsingUtil.parseInt(args[1].substring(6));
            targets.add(new KillTarget(args[0], achievement, count));
        }
    }

    @Override
    public void registerEvent() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String conditionID() {
        return "killentity";
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    private static class KillTarget {
        private String className;
        private int count;
        private Achievement achievement;

        public KillTarget(String className, Achievement achievement, int count) {
            this.className = className;
            this.achievement = achievement;
            this.count = count;
        }

        public boolean match(String s) {
            return this.className.equals(s);
        }

        public boolean match(Class<?> s) {
            return match(s.getName());
        }

        public Achievement getAchievement() {
            return this.achievement;
        }

        public int getCount() {
            return count;
        }
    }
}
