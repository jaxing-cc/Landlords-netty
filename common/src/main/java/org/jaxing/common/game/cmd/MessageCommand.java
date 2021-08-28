package org.jaxing.common.game.cmd;

import io.netty.channel.ChannelHandlerContext;
import org.jaxing.common.game.BaseCommand;
import org.jaxing.common.utils.CommonUtil;

public class MessageCommand extends BaseCommand {
    public static final String name = "message";
    public static final String info = "用于服务器向客户端发送消息 使用Message@[消息]@[0,1] 0表示客户端不能输入 1表示客户端可以输入";
    private static final char line = ';';
    public MessageCommand(){
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
        String[] split = s.split(CommonUtil.tag);
        System.out.println(split[1].replace(';','\n'));
        return split[2];
    }

    public static String buildMsg(String msg,boolean isAllow){
        return new StringBuffer(name).append(CommonUtil.tag).append(msg).append(CommonUtil.tag).append(isAllow?CommonUtil.allow:CommonUtil.refuse).append(CommonUtil.end).toString();
    }
}
