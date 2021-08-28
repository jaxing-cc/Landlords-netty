package org.jaxing.tcpserver.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;
import org.jaxing.common.factory.DataFactory;
import org.jaxing.common.game.Game;
import org.jaxing.common.game.LandlordsGame;

public class ServerMessageHandler extends SimpleChannelInboundHandler<String> {
    private DataFactory dataFactory = DataFactory.getInstance();
    private Game<String> messageHandler;
    private Logger logger = Logger.getLogger(ServerMessageHandler.class);
    public ServerMessageHandler(){
        messageHandler = new LandlordsGame();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        dataFactory.putUser(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        messageHandler.serverExec(s,ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        dataFactory.removeUser(ctx.channel());
        logger.debug("有用户离开,对用户进行数据清除处理");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
