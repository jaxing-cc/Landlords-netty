package org.jaxing.common.game.cmd;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.game.BaseCommand;
import org.jaxing.common.utils.CommonUtil;

public class InitCommand extends BaseCommand {
    public static final String name = "init";
    public static final String info = "非客户端使用的命令";
    public InitCommand(){
        super(name,info);
        hidden = true;
    }

    @Override
    public boolean serverOtherVal() {
        return true;
    }

    @Override
    public Object serverToDo(ChannelHandlerContext ctx, String s) {
        String[] split = s.split(CommonUtil.tag);
        User user = dataFactory.getUser(ctx.channel().id());
        user.setName(split[1]);
        ctx.writeAndFlush(UserInfoCommand.name + CommonUtil.tag + JSON.toJSONString(user) + CommonUtil.end);
        return null;
    }

    @Override
    public boolean clientOtherVal() {
        return false;
    }
}
