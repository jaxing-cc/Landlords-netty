package org.jaxing.common.game.cmd;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.jaxing.common.entity.player.StatusType;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.game.BaseCommand;
import org.jaxing.common.utils.CommonUtil;

public class CreateCommand extends BaseCommand {
    public static final String name = "create";
    public static final String info = "创建一个房间";

    public CreateCommand(){
        super(name,info);
        hidden =false;
    }

    @Override
    public boolean serverOtherVal() {
        return false;
    }

    @Override
    public Object serverToDo(ChannelHandlerContext ctx, String s) {
        Channel channel = ctx.channel();
        Long roomId = dataFactory.createRoom(channel.id());
        User user = dataFactory.getUser(ctx.channel().id());
        user.setType(StatusType.NOT_READY);
        ctx.writeAndFlush(name + CommonUtil.tag + roomId + CommonUtil.end);
        ctx.writeAndFlush(UserInfoCommand.name + CommonUtil.tag + JSON.toJSONString(user) + CommonUtil.end);
        return null;
    }

    @Override
    public boolean clientOtherVal() {
        return true;
    }

    @Override
    public Object clientToDo(ChannelHandlerContext ctx, String s) {
        System.out.println("房间号: " + CommonUtil.getData(s));
        return null;
    }
}
