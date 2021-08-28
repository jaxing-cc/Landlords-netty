package org.jaxing.common.game.cmd;

import io.netty.channel.ChannelHandlerContext;
import org.jaxing.common.game.BaseCommand;

public class HelpCommand extends BaseCommand {
    public static final String name = "help";
    public static final String info = "查看所有命令的使用";

    public HelpCommand(){
        super(name,info);
        hidden = false;
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
        return null;
    }
}
