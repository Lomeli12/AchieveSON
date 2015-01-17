package net.lomeli.achieveson.lib;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;

import cpw.mods.fml.common.registry.GameRegistry;

public class ParsingUtil {
    public static Item getItemFromString(String s) {
        if (s != null && !s.isEmpty()) {
            String[] array = s.split(":");
            if (array != null && array.length >= 2)
                return GameRegistry.findItem(array[0], array[1]);
        }
        return null;
    }

    public static ItemStack getStackFromString(String s) {
        String[] array_1 = s.split(" ");
        if (array_1 != null && (array_1.length == 1 || array_1.length == 2)) {
            String itemString = array_1[0];
            String[] array_2 = itemString.split(":");
            if (array_1.length == 2)
                return getStackFromString(itemString, parseInt(array_1[1]));
            if (array_2 != null && (array_2.length == 2 || array_2.length == 3))
                return array_2.length == 3 ? getStackFromString(itemString, parseInt(array_2[2])) : getStackFromString(itemString, 0);
        }
        return null;
    }

    public static ItemStack getStackFromString(String s, int i) {
        Item item = getItemFromString(s);
        return item != null ? new ItemStack(item, 1, i) : null;
    }

    public static Achievement getAchievement(String s) {
        for (Object obj : AchievementList.achievementList) {
            if (obj != null && obj instanceof Achievement) {
                if (((Achievement) obj).statId.equals(s)) return (Achievement) obj;
            }
        }
        return null;
    }

    public static boolean doStacksMatch(ItemStack a, ItemStack b) {
        return a != null && b != null && a.getItem() != null && b.getItem() != null && ((a.getItem() == b.getItem()) && (a.getItemDamage() == b.getItemDamage()));
    }

    public static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
