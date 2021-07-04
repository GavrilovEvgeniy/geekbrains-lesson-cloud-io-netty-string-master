package gb.cloud.server.service.impl.handler;

import gb.cloud.server.factory.Factory;
import gb.cloud.server.service.CommandDictionaryService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.sql.SQLException;

public class CommandInboundHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String command) throws IOException, SQLException, ClassNotFoundException {
        CommandDictionaryService dictionaryService = Factory.getCommandDirectoryService();
        String commandResult = dictionaryService.processCommand(command);

        ctx.writeAndFlush(commandResult);
    }

}
