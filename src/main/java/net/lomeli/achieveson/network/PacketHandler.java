package net.lomeli.achieveson.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

import net.lomeli.achieveson.AchieveSON;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(AchieveSON.MOD_ID.toLowerCase());
    public static void init() {
        INSTANCE.registerMessage(MessageUnlockAchievement.class, MessageUnlockAchievement.class, 0, Side.SERVER);
    }
}
