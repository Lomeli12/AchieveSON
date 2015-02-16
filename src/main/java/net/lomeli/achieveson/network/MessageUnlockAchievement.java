package net.lomeli.achieveson.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import net.lomeli.achieveson.lib.ParsingUtil;

public class MessageUnlockAchievement implements IMessage, IMessageHandler<MessageUnlockAchievement, IMessage> {
    private Achievement achievement;

    public MessageUnlockAchievement() {
    }

    public MessageUnlockAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.achievement = ParsingUtil.getAchievement(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, achievement.statId);
    }

    @Override
    public IMessage onMessage(MessageUnlockAchievement message, MessageContext ctx) {
        Achievement achieve = message.achievement;
        if (achieve == null)
            return null;
        EntityPlayerMP playerMP = ctx.getServerHandler().playerEntity;
        if (playerMP != null) {
            if (!playerMP.func_147099_x().hasAchievementUnlocked(achieve) && playerMP.func_147099_x().canUnlockAchievement(achieve))
                playerMP.addStat(achieve, 1);
        }
        return null;
    }
}
