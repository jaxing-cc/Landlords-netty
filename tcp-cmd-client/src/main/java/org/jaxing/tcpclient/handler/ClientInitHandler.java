package org.jaxing.tcpclient.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.jaxing.common.game.LandlordsGame;
import org.jaxing.tcpclient.TcpClientStarter;

public class ClientInitHandler extends ChannelInitializer<SocketChannel> {
    private LandlordsGame game;
    private TcpClientStarter starter;
    public ClientInitHandler(LandlordsGame game,TcpClientStarter starter) {
        this.game = game;
        this.starter = starter;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8))
                .addLast(new LineBasedFrameDecoder(1024))
                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                .addLast(new ClientMessageHandler(game,starter));
    }
}
