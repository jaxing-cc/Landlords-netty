package org.jaxing.common.game;


import io.netty.channel.ChannelHandlerContext;


public interface Game<T> {
    //服务器接到命令要如何处理
    Object serverExec(T msg, ChannelHandlerContext channel);
    //子类接到命令要如何处理
    Object clientExec(T msg, ChannelHandlerContext channel);

    Command contain(String cmd);
}
