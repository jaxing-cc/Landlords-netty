package org.jaxing.common.game.cmd;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.jaxing.common.entity.player.StatusType;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.entity.room.Room;
import org.jaxing.common.game.BaseCommand;
import org.jaxing.common.utils.CommonUtil;

public class JoinCommand extends BaseCommand {
    public static final String name = "join";
    public static final String info = "加入一个房间,比如加入房间号为6的房间: join@6";
    public JoinCommand(){
        super(name,info);
        hidden = false;
    }
    @Override
    public boolean serverOtherVal() {
        return false;
    }

    @Override
    public Object serverToDo(ChannelHandlerContext ctx, String s) {
        String data = CommonUtil.getData(s);
        ChannelId uid = ctx.channel().id();
        User user = dataFactory.getUser(uid);
        Room room = null;
        try {
            room = dataFactory.getRoomGroup().get(Long.parseLong(data));
        }catch (Exception ignored){}
        if (room != null && room.add(dataFactory.getUser(uid))){
            user.setType(StatusType.NOT_READY);
            user.setRoomId(room.getId());
            ctx.writeAndFlush(UserInfoCommand.name + CommonUtil.tag + JSON.toJSONString(user) + CommonUtil.end);
        }else{
            ctx.writeAndFlush(errorFlag + CommonUtil.end);
        }
        return null;
    }

    @Override
    public boolean clientOtherVal() {
        return false;
    }

    @Override
    public Object clientToDo(ChannelHandlerContext ctx, String s) {
        if (errorFlag.equals(s)){
            System.out.println("房间不存在或者已经满员!");
        }
        return null;
    }
}
