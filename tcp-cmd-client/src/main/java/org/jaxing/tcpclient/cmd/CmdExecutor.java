package org.jaxing.tcpclient.cmd;

import io.netty.channel.Channel;
import org.jaxing.common.entity.player.StatusType;
import org.jaxing.common.entity.poker.PokerGroup;
import org.jaxing.common.entity.poker.PokerGroupType;
import org.jaxing.common.game.LandlordsGame;
import org.jaxing.common.game.cmd.*;
import org.jaxing.common.utils.CommonUtil;
import org.jaxing.tcpclient.TcpClientStarter;

/**
 * 解析命令
 */
public class CmdExecutor {
    private Channel channel;
    private LandlordsGame game;
    private TcpClientStarter tcpClientStarter;
    public CmdExecutor(Channel channel, TcpClientStarter tcpClientStarter) {
        this.channel = channel;
        this.tcpClientStarter = tcpClientStarter;
    }

    public void exec(String cmd){
        cmd = cmd.trim();
        if (cmd.length()==0){
            return;
        }
        if (cmd.startsWith(JoinCommand.name + CommonUtil.tag) ){
            join(cmd);
            return;
        }
        switch (cmd){
            case "exit":exit();break;
            case HelpCommand.name:help();break;
            case CreateCommand.name:create();break;
            case ListCommand.name:list();break;
            case LeaveCommand.name:leave();break;
            case RoomCommand.name:room();break;
            case ReadyCommand.name:ready();break;
            default:
                System.out.println("无效指令");
                TcpClientStarter.next();
                break;
        }
    }
    public void play(String data, PokerGroupType type){
        channel.writeAndFlush(PokerGroupCommand.buildServerMsg(data,type));
    }
    private void ready() {
        StatusType type = tcpClientStarter.getUser().getType();
        if (type == StatusType.NOT_READY || type == StatusType.READY){
            channel.writeAndFlush(ReadyCommand.name + CommonUtil.end);
        }else{
            System.out.println("当前无法使用该命令");
            TcpClientStarter.next();
        }
    }

    private void room() {
        StatusType type = tcpClientStarter.getUser().getType();
        if (type == StatusType.NOT_READY || type == StatusType.READY){
            channel.writeAndFlush(RoomCommand.name + CommonUtil.end);
        }else{
            System.out.println("当前无法使用该命令");
            TcpClientStarter.next();
        }
    }

    private void join(String cmd) {
        if (tcpClientStarter.getUser().getType()==StatusType.FREE){
            channel.writeAndFlush(cmd + CommonUtil.end);
        }else{
            System.out.println("只有空闲状态才能使用");
            TcpClientStarter.next();
        }
    }

    private void leave() {
        if (tcpClientStarter.getUser().getType()==StatusType.NOT_READY){
            channel.writeAndFlush(LeaveCommand.name + CommonUtil.end);
        }else{
            System.out.println("在房间'等待中'状态下才能使用");
            TcpClientStarter.next();
        }
    }

    private void list() {
        channel.writeAndFlush(ListCommand.name + CommonUtil.end);
    }

    private void create() {
        if (tcpClientStarter.getUser().getType()!= StatusType.FREE){
            System.out.println("您已经在房间里了");
            TcpClientStarter.next();
            return;
        }
        channel.writeAndFlush(CreateCommand.name + CommonUtil.end);
    }

    private void help(){
        System.out.println("基础命令:");
        game.printCmd();
        System.out.println("出牌命令(牌 => 要输入的键位 , 出多张牌顺序可以随意):");
        System.out.println("A => 1,a,A");
        System.out.println("2 => 2");
        System.out.println("3 => 3");
        System.out.println("4 => 4");
        System.out.println("5 => 5");
        System.out.println("6 => 6");
        System.out.println("7 => 7");
        System.out.println("8 => 8");
        System.out.println("9 => 9");
        System.out.println("10 => 0");
        System.out.println("J => j,J");
        System.out.println("Q => q,Q");
        System.out.println("K => k,K");
        System.out.println("小王 => s");
        System.out.println("大王 => S");
        TcpClientStarter.next();
    }
    public void exit(){
        System.out.println("bye");
        channel.close();
        System.exit(0);
    }

    public void init(String name) {
        channel.writeAndFlush(new StringBuilder(InitCommand.name).append(CommonUtil.tag).append(name).append(CommonUtil.end).toString());
    }

    public void setGame(LandlordsGame game) {
        this.game = game;
    }
}
