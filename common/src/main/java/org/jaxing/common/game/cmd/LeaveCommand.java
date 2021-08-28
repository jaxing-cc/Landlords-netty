package org.jaxing.common.game.cmd;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.jaxing.common.entity.player.StatusType;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.entity.room.Room;
import org.jaxing.common.game.BaseCommand;
import org.jaxing.common.utils.CommonUtil;

import java.util.concurrent.ConcurrentHashMap;

public class LeaveCommand extends BaseCommand {
    public static final String name = "leave";
    public static final String info = "离开当前房间";
    public LeaveCommand(){
        super(name,info);
        hidden = false;
    }
    @Override
    public boolean serverOtherVal() {
        return false;
    }

    @Override
    public Object serverToDo(ChannelHandlerContext ctx, String s) {
        try {
            ChannelId id = ctx.channel().id();
            User user = dataFactory.getUser(id);
            ConcurrentHashMap<Long, Room> roomGroup = dataFactory.getRoomGroup();
            Room room = roomGroup.get(user.getRoomId());
            room.delete(id);
            if (room.size() == 0){
                dataFactory.getRoomGroup().remove(room.getId());
            }
            user.setType(StatusType.FREE);
            user.setRoomId(null);
            ctx.writeAndFlush(UserInfoCommand.name + CommonUtil.tag + JSON.toJSONString(user) + CommonUtil.end);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean clientOtherVal() {
        return false;
    }

    @Override
    public Object clientToDo(ChannelHandlerContext ctx, String s) {
        return super.clientToDo(ctx, s);
    }
}
