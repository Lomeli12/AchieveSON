package net.lomeli.achieveson.network;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import net.lomeli.achieveson.lib.ParsingUtil;

public class MessageUnlockAchievement implements IMessage, IMessageHandler<MessageUnlockAchievement, IMessage> {
    private long mostSigUUID, leastSigUUID;
    private int dimID;
    private Achievement achievement;

    public MessageUnlockAchievement() {
    }

    public MessageUnlockAchievement(Achievement achievement, EntityPlayer player) {
        this.mostSigUUID = player.getUniqueID().getMostSignificantBits();
        this.leastSigUUID = player.getUniqueID().getLeastSignificantBits();
        this.dimID = player.dimension;
        this.achievement = achievement;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.mostSigUUID = buf.readLong();
        this.leastSigUUID = buf.readLong();
        this.dimID = buf.readInt();
        this.achievement = ParsingUtil.getAchievement(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(mostSigUUID);
        buf.writeLong(leastSigUUID);
        buf.writeInt(dimID);
        ByteBufUtils.writeUTF8String(buf, achievement.statId);
    }

    @Override
    public IMessage onMessage(MessageUnlockAchievement message, MessageContext ctx) {
        Achievement achieve = message.achievement;
        if (achieve == null) return null;
        UUID originUUID = new UUID(message.mostSigUUID, message.leastSigUUID);
        EntityPlayerMP playerMP = (EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.dimID).func_152378_a(originUUID);
        if (playerMP != null) {
            if (!playerMP.func_147099_x().hasAchievementUnlocked(achieve) && playerMP.func_147099_x().canUnlockAchievement(achieve))
                playerMP.addStat(achieve, 1);
        }
        return null;
    }
}
