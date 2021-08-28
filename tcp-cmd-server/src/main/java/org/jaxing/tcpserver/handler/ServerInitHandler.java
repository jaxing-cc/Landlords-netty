package org.jaxing.tcpserver.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ServerInitHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8))
                .addLast(new LineBasedFrameDecoder(1024))
                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                .addLast(new ServerMessageHandler());
    }
}
