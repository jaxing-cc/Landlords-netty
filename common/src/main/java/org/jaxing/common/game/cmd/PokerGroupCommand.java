package org.jaxing.common.game.cmd;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.jaxing.common.entity.player.StatusType;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.entity.poker.Poker;
import org.jaxing.common.entity.poker.PokerGroupType;
import org.jaxing.common.entity.poker.PokerType;
import org.jaxing.common.entity.room.GameInfo;
import org.jaxing.common.entity.room.Room;
import org.jaxing.common.factory.PokerFactory;
import org.jaxing.common.game.BaseCommand;
import org.jaxing.common.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

public class PokerGroupCommand extends BaseCommand {
    public static final String name = "pokerGroup";
    public static final String info = "用于接受服务器发来的牌组并打印";
    private static final char top1='┌';
    private static final String top2="──┐";
    private static final char wall = '|';
    private static final char bottom1 ='└';
    private static final String bottom2 ="──┘";

    public PokerGroupCommand(){
        super(name,info);
        hidden = true;
    }
    @Override
    public boolean serverOtherVal() {
        return true;
    }

    @Override
    public Object serverToDo(ChannelHandlerContext ctx, String s) {
        ChannelId id = ctx.channel().id();
        User user = dataFactory.getUser(id);
        Long roomId = user.getRoomId();
        Room room = dataFactory.getRoomGroup().get(roomId);
        if (room == null){
            logger.debug("没有找到房间实例");
            ctx.writeAndFlush(MessageCommand.buildMsg("没有找到房间实例",false));
            return null;
        }
        GameInfo gameInfo = room.getGameInfo();
        if (gameInfo == null){
            logger.debug("没有找到游戏对局实例");
            ctx.writeAndFlush(MessageCommand.buildMsg("没有找到游戏对局实例",false));
            return  null;
        }
        String[] split = s.split(CommonUtil.tag);
        int flag = gameInfo.play(ctx,split[1].toCharArray(),split[2]);
        if (flag != 0){
            User master = gameInfo.getMaster();
            List<User> farmers = gameInfo.getFarmers();
            List<User> users = gameInfo.getUsers();
            int multiple = gameInfo.getMultiple();
            StringBuilder msg = new StringBuilder();
            //游戏结束,分发消息
            if (flag == 1){
                master.coinAdd(multiple * 2);
                farmers.forEach(item -> item.coinSubtract(multiple));
                msg.append("游戏结束,地主[ ").append(master.getName()).append(" ]胜利 !! ")
                        .append("地主 + ").append(multiple * 2).append(" , ")
                        .append("农民 - ").append(multiple);
            }else{
                master.coinSubtract(multiple * 2);
                farmers.forEach(item -> item.coinAdd(multiple));
                msg.append("游戏结束,农民[ ")
                        .append(farmers.get(0).getName()).append(',')
                        .append(farmers.get(1).getName()).append(" ]胜利 !! ")
                        .append("地主 - ").append(multiple * 2).append(" , ")
                        .append("农民 + ").append(multiple);
            }
            logger.debug("当前对局结束,对局信息:" + room.getGameInfo() + "地主" + master + ", 农民" + farmers);
            room.gameEnd(msg.toString());
        }
        return null;
    }

    @Override
    public boolean clientOtherVal() {
        return true;
    }

    @Override
    public Object clientToDo(ChannelHandlerContext ctx, String s) {
        String[] split = s.split(CommonUtil.tag);
        //解析扑克id数组
        char[] chars = split[1].toCharArray();
        List<Byte> list = new ArrayList<>();
        int cur = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != ';'){
                int v = chars[i] - '0';
                cur = cur * 10 + v;
            }else{
                list.add((byte) cur);
                cur = 0;
            }
        }
        int size = list.size();
        StringBuilder printStr = new StringBuilder();
        printStr.append(top1);
        for (int i = 0; i < size; i++) {
            printStr.append(top2);
        }
        printStr.append('\n').append(wall);
        for (int i = 0; i < size; i++) {
            Poker poker = PokerFactory.get(list.get(i));
            byte value = poker.getValue();
            if (value == 10){
                printStr.append(value).append(wall);
            }else if (value == 11){
                printStr.append('J').append(' ').append(wall);
            }else if (value == 12){
                printStr.append('Q').append(' ').append(wall);
            }else if (value == 13){
                printStr.append('K').append(' ').append(wall);
            }else if (value == 1){
                printStr.append('A').append(' ').append(wall);
            }else if (value == 0){
                printStr.append(poker.getType().tag).append(' ').append(wall);
            }else{
                printStr.append(value).append(' ').append(wall);
            }
        }
        printStr.append('\n').append(wall);
        for (int i = 0; i < size; i++) {
            Poker poker = PokerFactory.get(list.get(i));
            if (poker.getValue() != 0){
                printStr.append(poker.getType().tag).append(' ').append(wall);
            }else{
                printStr.append("  ").append(wall);
            }
        }
        printStr.append('\n').append(bottom1);
        for (int i = 0; i < size; i++) {
            printStr.append(bottom2);
        }
        printStr.append('\n');
        System.out.println(printStr);
        //返回是否阻塞
        return split[2];
    }

    public static String buildClientMsg(byte[] arr,boolean allow){
        StringBuffer res = new StringBuffer(name).append(CommonUtil.tag);
        for (byte b : arr) {
            res.append(b).append(';');
        }
        res.append(CommonUtil.tag).append(allow?CommonUtil.allow:CommonUtil.refuse);
        return res.append(CommonUtil.end).toString();
    }

    public static String buildServerMsg(String msg, PokerGroupType type){
        StringBuffer res = new StringBuffer(name).append(CommonUtil.tag);
        res.append(msg);
        res.append(CommonUtil.tag).append(type.getCode());
        return res.append(CommonUtil.end).toString();
    }
}
