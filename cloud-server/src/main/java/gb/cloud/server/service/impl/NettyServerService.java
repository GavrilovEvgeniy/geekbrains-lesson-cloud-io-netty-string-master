package gb.cloud.server.service.impl;

import gb.cloud.server.Main;
import gb.cloud.server.service.ServerService;
import gb.cloud.server.service.impl.handler.BigFilesWriteHandler;
import gb.cloud.server.service.impl.handler.CommandInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class NettyServerService implements ServerService {

    @Override
    public void startServer(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                             channel.pipeline().addLast(
                                     new StringDecoder(),
                                     new StringEncoder(),
                                     new CommandInboundHandler()

                             );
                             channel.pipeline().addLast(
                                              new ChunkedWriteHandler(),
                                              new BigFilesWriteHandler()
                             );
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();

            Main.log.info("Server started on port " + port);
            System.out.println("Server started on port " + port);

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            Main.log.error("Server is dead");
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            Main.log.info("Server finished successfully");
        }
    }

}
