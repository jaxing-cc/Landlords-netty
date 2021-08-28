package org.jaxing.common.game;

import io.netty.channel.ChannelHandlerContext;

//指令接口
public interface Command {

    boolean serverOtherVal();

    Object serverToDo(ChannelHandlerContext ctx,String s);

    boolean clientOtherVal();

    Object clientToDo(ChannelHandlerContext ctx,String s);

    boolean isHidden();
}
