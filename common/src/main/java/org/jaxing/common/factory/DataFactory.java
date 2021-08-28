package org.jaxing.common.factory;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.jaxing.common.entity.player.StatusType;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.entity.room.GameInfo;
import org.jaxing.common.entity.room.Room;
import org.jaxing.common.game.cmd.GameInfoCommand;
import org.jaxing.common.game.cmd.MessageCommand;
import org.jaxing.common.game.cmd.UserInfoCommand;
import org.jaxing.common.utils.CommonUtil;
import sun.management.snmp.util.MibLogger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 用户工厂
 */
public class DataFactory {
    private volatile static DataFactory self;
    private AtomicLong size;
    @Getter
    private ChannelGroup channelGroup;
    @Getter
    private ConcurrentHashMap<ChannelId, User> userGroup;
    @Getter
    private ConcurrentHashMap<Long, Room> roomGroup;
    private static Logger logger = Logger.getLogger(DataFactory.class);

    public DataFactory(){
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        userGroup = new ConcurrentHashMap<>();
        roomGroup = new ConcurrentHashMap<>();
        size = new AtomicLong(0);
    }
    //单例
    public static DataFactory getInstance() {
        if (self == null){
            synchronized (DataFactory.class){
                if (self == null){
                    self = new DataFactory();
                }
            }
        }
        return self;
    }
    //用户连接
    public void putUser(Channel channel){
        if (!channelGroup.contains(channel)) {
            size.incrementAndGet();
            User user = new User();
            user.setChannel(channel);
            userGroup.put(channel.id(),user);
            channelGroup.add(channel);
        }
    }
    //用户移除
    public void removeUser(Channel channel){
        size.decrementAndGet();
        ChannelId id = channel.id();
        User user = userGroup.get(id);
        Long roomId = user.getRoomId();
        String msg = "玩家[ "+user.getName()+" ]断开连接....";
        logger.debug(msg);
        if (roomId !=null){
            //玩家在房间中
            Room room = roomGroup.get(roomId);
            room.delete(channel.id());
            GameInfo gameInfo = room.getGameInfo();
            if (gameInfo != null){
                logger.debug(user.getName() + " 还有游戏实例，正在结束游戏....");
                //玩家在游戏中
                room.gameEnd(msg+ ", 游戏结束，返回房间 , 如果界面没有弹出输入框 , 请输入任意键+enter");
            }
            if (room.size() == 0){
                roomGroup.remove(roomId);
            }
        }
        userGroup.remove(channel.id());
    }
    //获取
    public User getUser(ChannelId id){
        return userGroup.get(id);
    }
    //用户数量
    public long userCount(){
        return size.get();
    }
    //打印状态
    public void printStatus(){
        System.out.println("连接数:" + channelGroup.size() + " -- 用户数" + userGroup.size());
        for (Channel channel : channelGroup) {
            System.out.println(channel.id() + "---->" + userGroup.get(channel.id()));
        }
    }
    //创建房间
    public Long createRoom(ChannelId id){
        User user = getUser(id);
        Room room = new Room(user);
        user.setRoomId(room.getId());
        roomGroup.put(room.getId(),room);
        return room.getId();
    }
}
