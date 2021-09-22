package org.jaxing.tcpclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.entity.poker.PokerGroupType;
import org.jaxing.common.entity.room.GameInfo;
import org.jaxing.common.factory.PokerFactory;
import org.jaxing.common.game.LandlordsGame;
import org.jaxing.tcpclient.cmd.CmdExecutor;
import org.jaxing.tcpclient.handler.ClientInitHandler;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class TcpClientStarter {
    private int port = 10666;
    private String host = "localhost";
    private Scanner scanner;
    private CmdExecutor executor;
    private SimpleDateFormat format;
    private static volatile boolean nextStep;//用与控制展示输入栏在接收到消息后展示，游戏开始后不用就了
    private Channel channel;
    @Getter
    @Setter
    private User user;
    //游戏开始后使用
    public static volatile boolean block;
    @Getter
    @Setter
    private GameInfo gameInfo;

    public TcpClientStarter(){
        scanner = new Scanner(System.in);
        format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        nextStep = true;
    }
    public TcpClientStarter(String host,int port){
        this.port = port;
        this.host = host;
        scanner = new Scanner(System.in);
        format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        nextStep = true;
    }
    private void out(){
        System.out.print("["+format.format(new Date())+" ("+user.getType()+")]>: ");
    }
    public void start(){
        EventLoopGroup work = new NioEventLoopGroup(1);
        LandlordsGame game = new LandlordsGame();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(work)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitHandler(game,this));
            ChannelFuture future = bootstrap.connect(host,port).sync();
            this.channel = future.channel();
            executor = new CmdExecutor(future.channel(),this);
            executor.setGame(game);
            work();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            work.shutdownGracefully();
        }
    }
    public void work(){
        System.out.print("起个名字吧:");
        String name = scanner.nextLine();
        nextStep = false;
        executor.init(name);
        while (true){
            if (isStart()){
                block();
                if (gameInfo == null){
                    continue;
                }
                System.out.print(gameInfo.getOrder().append(" 当前是您的回合>:"));
                String cmd = null;
                PokerGroupType type = null;
                if (gameInfo == null){
                    continue;
                }
                try {
                    type = PokerFactory.checkUserInputPoker(cmd = scanner.nextLine());
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
                if (type == null){
                    continue;
                }else{
                    block = false;
                }
                executor.play(cmd,type);
            }else{
                waitFinish();
                if (isStart()){
                    continue;
                }
                out();
                String cmd = scanner.nextLine();
                if (isStart()){
                    System.out.println("游戏开始! 如果当前不是你的回合，请耐心等待!");
                    continue;
                }
                nextStep = false;
                executor.exec(cmd);
            }
        }
    }

    //游戏开始使用的阻塞方法
    public void block(){
        while (!block){
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void waitFinish(){
        while (!nextStep){
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void next(){
        nextStep = true;
    }
    public static void main(String[] args) {
        TcpClientStarter starter = null;
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("config.properties"));
            String port = (String) properties.get("port");
            String host =(String) properties.get("host");
            starter = new TcpClientStarter(host,Integer.parseInt(port));
        }catch (Exception e){
            starter = new TcpClientStarter();
        }
        starter.start();
    }
    public boolean isStart(){
        return gameInfo != null;
    }
}
