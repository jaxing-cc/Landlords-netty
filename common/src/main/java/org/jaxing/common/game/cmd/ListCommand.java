package org.jaxing.common.game.cmd;

import io.netty.channel.ChannelHandlerContext;
import org.jaxing.common.entity.room.Room;
import org.jaxing.common.game.BaseCommand;
import org.jaxing.common.utils.CommonUtil;
import java.util.Collection;
public class ListCommand extends BaseCommand {
    public static final String name = "list";
    public static final String info = "展示房间列表";
    public ListCommand(){
        super(name,info);
        hidden = false;
    }

    @Override
    public boolean serverOtherVal() {
        return false;
    }

    @Override
    public Object serverToDo(ChannelHandlerContext ctx, String s) {
        Collection<Room> values = dataFactory.getRoomGroup().values();
        StringBuilder answer = new StringBuilder(ListCommand.name).append(CommonUtil.tag);
        for (Room value : values) {
            answer.append(value).append(";");
        }
        answer.append(CommonUtil.end);
        ctx.writeAndFlush(answer);
        return null;
    }

    @Override
    public boolean clientOtherVal() {
        return false;
    }

    @Override
    public Object clientToDo(ChannelHandlerContext ctx, String s) {
        String data = CommonUtil.getData(s);
        if ("".equals(data)){
            System.out.println("还没有任何房间被创建...");
            return null;
        }
        String[] info = data.split(";");
        for (String s1 : info) {
            System.out.println(s1);
        }
        return null;
    }
}
