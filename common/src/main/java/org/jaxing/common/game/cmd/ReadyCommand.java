package org.jaxing.common.game.cmd;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.jaxing.common.entity.player.StatusType;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.entity.room.Room;
import org.jaxing.common.game.BaseCommand;
import org.jaxing.common.utils.CommonUtil;

public class ReadyCommand extends BaseCommand {
    public static final String name = "ready";
    public static final String info = "准备开始游戏";

    public ReadyCommand(){
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
        User user = dataFactory.getUserGroup().get(id);
        StatusType type = user.getType();
        if (type == StatusType.NOT_READY){
            user.setType(StatusType.READY);
        }else{
            user.setType(StatusType.NOT_READY);
        }
        Room room = dataFactory.getRoomGroup().get(user.getRoomId());
        ctx.writeAndFlush(UserInfoCommand.name + CommonUtil.tag + JSON.toJSONString(user) + CommonUtil.end);
        try {
            if (room.ready()){
                //调用开启方法
                room.start(id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean clientOtherVal() {
        return false;
    }
}
