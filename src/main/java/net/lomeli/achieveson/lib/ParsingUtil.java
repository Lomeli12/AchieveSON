package net.lomeli.achieveson.lib;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;

import cpw.mods.fml.common.registry.GameRegistry;

public class ParsingUtil {

    public static ItemStack getStackFromString(String s) {
        String[] array_1 = s.split(" ");
        if (array_1 != null && (array_1.length == 1 || array_1.length == 2)) {
            String itemString = array_1[0];
            String[] array_2 = itemString.split(":");
            if (array_2.length == 2) {
                String modID = array_2[0];
                String itemName = array_2[1];
                int meta = 0;
                Item item = GameRegistry.findItem(modID, itemName);
                if (array_1.length == 2 && array_1[1].startsWith("meta="))
                    meta = parseInt(array_1[1].substring(5));
                return new ItemStack(item, 1, meta);
            }
        }
        return null;
    }
    
    public static int getCountFromString(String s) {
        if (s != null && s.startsWith("count="))
            return parseInt(s.substring(6));
        return 1;
    }

    public static Achievement getAchievement(String s) {
        for (Object obj : AchievementList.achievementList) {
            if (obj != null && obj instanceof Achievement) {
                if (((Achievement) obj).statId.equals(s)) return (Achievement) obj;
            }
        }
        return null;
    }

    public static boolean doStacksMatch(ItemStack targetStack, ItemStack stack) {
        return targetStack != null && stack != null && targetStack.getItem() != null && stack.getItem() != null && ((targetStack.getItem() == stack.getItem()) && (targetStack.getItemDamage() == Short.MAX_VALUE || targetStack.getItemDamage() == stack.getItemDamage()));
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
