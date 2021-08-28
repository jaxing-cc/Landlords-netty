package org.jaxing.common.game;

import io.netty.channel.ChannelHandlerContext;
import org.jaxing.common.game.cmd.*;
import org.jaxing.common.utils.CommonUtil;
import java.util.HashMap;
import java.util.Set;

public class LandlordsGame implements Game<String> {

    private static HashMap<String,Command> cmdGroup =new HashMap<>();

    static {
        cmdGroup.put(InitCommand.name,new InitCommand());
        cmdGroup.put(CreateCommand.name,new CreateCommand());
        cmdGroup.put(ListCommand.name,new ListCommand());
        cmdGroup.put(UserInfoCommand.name,new UserInfoCommand());
        cmdGroup.put(LeaveCommand.name,new LeaveCommand());
        cmdGroup.put(JoinCommand.name,new JoinCommand());
        cmdGroup.put(RoomCommand.name,new RoomCommand());
        cmdGroup.put(ReadyCommand.name,new ReadyCommand());
        cmdGroup.put(MessageCommand.name,new MessageCommand());
        cmdGroup.put(GameInfoCommand.name,new GameInfoCommand());
        cmdGroup.put(PokerGroupCommand.name,new PokerGroupCommand());
    }


    @Override
    public Object serverExec(String msg, ChannelHandlerContext ctx) {
        Command command = contain(msg);
        if (command == null){
            ctx.writeAndFlush(CommonUtil.errorFlag + CommonUtil.end);
        }else{
            return command.serverToDo(ctx,msg);
        }
        return null;
    }

    @Override
    public Object clientExec(String msg, ChannelHandlerContext ctx) {
        Command command = contain(msg);
        if (command == null){
            if (msg.equals(CommonUtil.errorFlag)){
                System.out.println("无效的指令");
            }
        }else{
            return command.clientToDo(ctx,msg);
        }
        return null;
    }
    public Command contain(String cmd){
        if (cmd == null){
            return null;
        }
        Set<String> strings = cmdGroup.keySet();
        if (strings.contains(cmd)){
            return cmdGroup.get(cmd);
        }
        for (String string : strings) {
            if (cmd.startsWith(string + CommonUtil.tag)){
                return cmdGroup.get(string);
            }
        }
        return null;
    }

    public void printCmd(){
        for (Command value : cmdGroup.values()) {
            if (!value.isHidden()){
                System.out.println(value);
            }
        }
    }

    public Command getCommandByName(String name){
        return cmdGroup.get(name);
    }

}
