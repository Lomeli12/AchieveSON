package net.lomeli.achieveson.lib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class NBTUtil {
    public static String PRESISTED = EntityPlayer.PERSISTED_NBT_TAG, ACHIEVETAGS = "achieveSONdata";

    public static NBTTagCompound getAchieveData(EntityPlayer player) {
        NBTTagCompound preTag = player.getEntityData().getCompoundTag(PRESISTED);
        return preTag.hasKey(ACHIEVETAGS) ? preTag.getCompoundTag(ACHIEVETAGS) : new NBTTagCompound();
    }

    public static void saveAchieveData(EntityPlayer player, NBTTagCompound tag) {
        NBTTagCompound preTag = player.getEntityData().getCompoundTag(PRESISTED);
        preTag.setTag(ACHIEVETAGS, tag);
        player.getEntityData().setTag(PRESISTED, preTag);
    }

    public static int getInt(EntityPlayer player, String statid) {
        NBTTagCompound tagCompound = getAchieveData(player);
        return tagCompound.getInteger(statid);
    }

    public static void setInt(EntityPlayer player, String statid, int count) {
        NBTTagCompound tagCompound = getAchieveData(player);
        tagCompound.setInteger(statid, count);
        saveAchieveData(player, tagCompound);
    }

    public static void removeTag(EntityPlayer player, String statid) {
        NBTTagCompound tagCompound = getAchieveData(player);
        tagCompound.removeTag(statid);
        saveAchieveData(player, tagCompound);
    }
}