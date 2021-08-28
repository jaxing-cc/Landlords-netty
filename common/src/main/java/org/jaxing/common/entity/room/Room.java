package org.jaxing.common.entity.room;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.Getter;
import lombok.Setter;
import org.jaxing.common.entity.player.StatusType;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.game.cmd.GameInfoCommand;
import org.jaxing.common.game.cmd.MessageCommand;
import org.jaxing.common.game.cmd.UserInfoCommand;
import org.jaxing.common.utils.CommonUtil;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * 游戏房间实体类
 */
@Getter
@Setter
public class Room {
    public static AtomicLong aLong = new AtomicLong(0);
    private Long id;
    private byte playerSize;
    private HashMap<ChannelId,User> playerTable;
    private GameInfo gameInfo;
    public static Long createId(){
        return aLong.incrementAndGet();
    }
    public Room(User owner){
        this((byte) 3,owner);
    }
    public Room(byte playerSize,User user){
        this.playerSize = playerSize;
        playerTable = new HashMap<>();
        playerTable.put(user.getChannel().id(),user);
        id = createId();
    }
    public boolean add(User user){
        if (playerTable.size() < playerSize){
            playerTable.put(user.getChannel().id(),user);
            return true;
        }
        return false;
    }
    //游戏开始入口
    public void start(ChannelId id){
        gameInfo = new GameInfo();
        gameInfo.setRoomId(this.id);
        gameInfo.start(playerTable,id);
    }
    public int size(){
        return playerTable.size();
    }
    public boolean ready(){
        for (User value : playerTable.values()) {
            if (value.getType() != StatusType.READY){
                return false;
            }
        }
        return playerTable.size() == playerSize;
    }
    public void gameEnd(String msg){
        gameInfo = null;
        playerTable.values().forEach(item -> {
                item.getChannel().writeAndFlush(MessageCommand.buildMsg(";"+msg,false));
                item.setType(StatusType.NOT_READY);
                Channel channel = item.getChannel();
                channel.writeAndFlush(UserInfoCommand.name + CommonUtil.tag + JSON.toJSONString(item) + CommonUtil.end);
                channel.writeAndFlush(GameInfoCommand.name + CommonUtil.tag + CommonUtil.gameOverFlag + CommonUtil.end);
        });
    }
    @Override
    public String toString() {
        return  playerSize +"/" +size() +  " ==> 房间号:" + id;
    }

    public void delete(ChannelId id) {
        playerTable.remove(id);
    }
}
