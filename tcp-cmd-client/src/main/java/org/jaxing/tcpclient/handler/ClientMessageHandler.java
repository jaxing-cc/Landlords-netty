package org.jaxing.tcpclient.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.entity.room.GameInfo;
import org.jaxing.common.game.LandlordsGame;
import org.jaxing.common.utils.CommonUtil;
import org.jaxing.tcpclient.TcpClientStarter;

public class ClientMessageHandler extends SimpleChannelInboundHandler<String> {
    private TcpClientStarter starter;
    private LandlordsGame messageHandler;
    public ClientMessageHandler(LandlordsGame game, TcpClientStarter starter){
        messageHandler = game;
        this.starter = starter;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        Object o = messageHandler.clientExec(s, ctx);
        if (o instanceof User){
            //如果返回了用户数据，就修改
            starter.setUser((User) o);
            TcpClientStarter.next();
        }else if (o instanceof GameInfo){
            starter.setGameInfo((GameInfo) o);
        } else if (o instanceof Boolean){
            if ((boolean) o){
                TcpClientStarter.next();
            }
        } else if(o instanceof String){
            switch ((String) o){
                case CommonUtil.allow:
                    TcpClientStarter.block = true;
                    break;
                case CommonUtil.refuse:
                    TcpClientStarter.block = false;
                    break;
                case CommonUtil.gameOverFlag:
                    starter.setGameInfo(null);
                    TcpClientStarter.block = true;
                    break;
            }
//            System.out.println("***********" + s+ "**********" + TcpClientStarter.block);
        }else{
            TcpClientStarter.next();
        }
    }
}
