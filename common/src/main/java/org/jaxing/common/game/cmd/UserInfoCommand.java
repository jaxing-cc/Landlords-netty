package org.jaxing.common.game.cmd;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.game.BaseCommand;
import org.jaxing.common.utils.CommonUtil;

public class UserInfoCommand extends BaseCommand {
    public static final String name = "userInfo";
    public static final String info = "用来获取服务器端用户信息";
    public UserInfoCommand(){
        super(name,info);
        hidden = true;
    }

    @Override
    public boolean serverOtherVal() {
        return false;
    }

    @Override
    public Object serverToDo(ChannelHandlerContext ctx, String s) {
        return null;
    }

    @Override
    public boolean clientOtherVal() {
        return false;
    }

    @Override
    public Object clientToDo(ChannelHandlerContext ctx, String s) {
        String data = CommonUtil.getData(s);
        return JSON.parseObject(data, User.class);
    }
}
