package org.jaxing.common.game.cmd;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.entity.room.Room;
import org.jaxing.common.game.BaseCommand;
import org.jaxing.common.utils.CommonUtil;

import java.util.Collection;

public class RoomCommand extends BaseCommand {

    public static final String name = "room";
    public static final String info = "查看当前的房间状态";

    public RoomCommand(){
        super(name,info);
        hidden = false;
    }
    @Override
    public boolean serverOtherVal() {
        return false;
    }

    @Override
    public Object serverToDo(ChannelHandlerContext ctx, String s) {
        ChannelId id = ctx.channel().id();
        StringBuilder sb = new StringBuilder();
        Long roomId = dataFactory.getUser(id).getRoomId();
        Room room = dataFactory.getRoomGroup().get(roomId);
        Collection<User> values = room.getPlayerTable().values();
        sb.append(room).append(";");
        for (User value : values) {
            sb.append(value).append(";");
        }
        ctx.writeAndFlush(RoomCommand.name + CommonUtil.tag + sb + CommonUtil.end);
        return null;
    }

    @Override
    public boolean clientOtherVal() {
        return true;
    }

    @Override
    public Object clientToDo(ChannelHandlerContext ctx, String s) {
        String data = CommonUtil.getData(s);
        System.out.println(data.replace(';', '\n'));
        return null;
    }
}
