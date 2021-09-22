package org.jaxing.common.entity.room;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.jaxing.common.entity.player.StatusType;
import org.jaxing.common.entity.player.User;
import org.jaxing.common.entity.poker.ComparablePokerGroup;
import org.jaxing.common.entity.poker.PokerGroup;
import org.jaxing.common.entity.poker.PokerGroupType;
import org.jaxing.common.factory.PokerFactory;
import org.jaxing.common.game.cmd.GameInfoCommand;
import org.jaxing.common.game.cmd.MessageCommand;
import org.jaxing.common.game.cmd.PokerGroupCommand;
import org.jaxing.common.utils.CommonUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class GameInfo {
    private static Logger logger = Logger.getLogger(GameInfo.class);
    private static final String defaultName = "游戏没有开始";
    //游戏是否开始
    private boolean state;
    //房间号
    private Long roomId;
    //当前操作的用户名
    private String currentUsername;
    //当前玩家id
    @JSONField(serialize = false)
    private ChannelId currentUid;
    //地主id
    @JSONField(serialize = false)
    private ChannelId masterId;
    //回合数
    private int count;
    //倍数
    public int multiple;
    //第一个出牌的
    private int firstUserIndex;
    //玩家map
    @JSONField(serialize = false)
    private HashMap<ChannelId, User> players;
    //用户牌组
    @JSONField(serialize = false)
    private HashMap<ChannelId, PokerGroup> pokerGroups;
    //地主牌 三张
    @JSONField(serialize = false)
    private PokerGroup specialPokers;

    //上次的牌
    @JSONField(serialize = false)
    private ComparablePokerGroup lastPokers;

    private List<User> users;
    public GameInfo(){
        state = false;
        currentUsername = defaultName;
        count = 0;
        multiple = 3;
        users = new ArrayList<>();
        pokerGroups = new HashMap<>();
    }
    public void start(HashMap<ChannelId, User> playerTable,ChannelId lastReadyUserId){
        players = playerTable;
        //生成第一个人的index
        firstUserIndex = getFirstIndex();
        PokerGroup[] wash = PokerFactory.wash();
        state = true;
        int i = 1;
        Collection<User> values = players.values();
        for (User item : values) {
            ChannelId id = item.getChannel().id();
            item.setType(StatusType.UNDERWAY) ;
            users.add(item);
            if (i < wash.length){
                //给玩家发牌
                pokerGroups.put(id,wash[i++]);
            }
        }
        //名牌
        specialPokers = wash[0];
        //找出地主
        User first = getCurrentUser();
        masterId = first.getChannel().id();
        logger.debug("游戏开始 , 地主的玩家编号:" + firstUserIndex +" 玩家信息: "+ users);
        //群发消息
        String msg1 = "地主是玩家[" + currentUsername + "],底牌是:";
        String msg2 = "你的牌是:";
        String s = PokerGroupCommand.buildClientMsg(specialPokers.getPokersIndex(),false);
        users.forEach(item->{
            Channel channel = item.getChannel();
            ChannelId id = channel.id();
            channel.writeAndFlush(MessageCommand.buildMsg(msg1,false));
            channel.writeAndFlush(s);
            channel.writeAndFlush(MessageCommand.buildMsg(msg2,false));
            PokerGroup pokerGroup = pokerGroups.get(id);
            if (id == currentUid){
                pokerGroup.put(specialPokers);
            }
            pokerGroup.sort();
            channel.writeAndFlush(PokerGroupCommand.buildClientMsg(pokerGroup.getPokersIndex(),false));
            if (id != lastReadyUserId){
                channel.writeAndFlush(MessageCommand.buildMsg("请输入任意键开始游戏!...",id == first.getChannel().id()));
            }else{
                channel.writeAndFlush(MessageCommand.buildMsg("游戏开始! 如果当前不是你的回合，请耐心等待!",id == first.getChannel().id()));
            }
        });
        //下发游戏信息
        updateState();
    }
    private void updateState(){
        players.values().forEach(item -> {
            item.getChannel().writeAndFlush(GameInfoCommand.name + CommonUtil.tag + JSON.toJSONString(this) + CommonUtil.end);
        });
    }
    //该方法实现伪随机
    private int getFirstIndex(){
        long time = System.currentTimeMillis();
        return (int) ((time & (time >> 32)) % players.size());
    }
    //获取当前用户index
    private int getCurrentIndex(){
        return (firstUserIndex + count) % players.size();
    }
    //获取会自动更新当前玩家信息的字段
    private User getCurrentUser(){
        User first = users.get(getCurrentIndex());
        currentUsername = first.getName();
        currentUid = first.getChannel().id();
        return first;
    }
    //获取下一个玩家。回合数会自动++
    private User getNextUser(){
        this.count++;
        return getCurrentUser();
    }
    public StringBuilder getOrder(){
        int count = users.size();
        int first = firstUserIndex;
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < count; i++) {
            User user = users.get((first + i) % count);
            answer.append(user.getName());
            if (i != count -1){
                answer.append(" ==> ");
            }
        }
        return answer;
    }
    //返回地主
    @JSONField(serialize = false)
    public User getMaster(){
        return players.get(masterId);
    }
    //返回农民
    @JSONField(serialize = false)
    public List<User> getFarmers(){
        List<User> answer = new ArrayList<>();
        users.forEach(user->{
            if (user.getChannel().id() != masterId){
                answer.add(user);
            }
        });
        return answer;
    }
    /**
     * 2 => 农民胜利
     * 1 => 地主胜利
     * 0 => 没有结束
     * @return
     */
    public int play(ChannelHandlerContext ctx, char[] charArray, String type) {
        int code = 0;
        for (int i = 0; i < type.length(); i++) {
            code = code * 10 + (type.charAt(i) - '0');
        }
        User currentUser = getCurrentUser();
        ChannelId id = currentUser.getChannel().id();
        PokerGroup pokerGroup = pokerGroups.get(id);
        //牌型
        PokerGroupType pokerGroupType = PokerGroupType.map.get((byte) code);
        logger.debug(currentUser + " , 已经出牌,出牌的类型是:" + pokerGroupType);
        if (pokerGroupType == PokerGroupType.PASS){
            if (lastPokers.getId() == id){
                currentUser.getChannel().writeAndFlush(MessageCommand.buildMsg("您的牌没有人能要过，本轮你可以随便出...;",true));
                return 0;
            }
            //指针后移
            User nextUser = getNextUser();
            //给玩家分发信息...
            notifyPlayer(null,pokerGroup.getSize(),currentUser,nextUser);
            return 0;
        }
        ComparablePokerGroup instance = ComparablePokerGroup.getInstance(charArray);
        instance.setType(pokerGroupType);
        instance.setId(ctx.channel().id());
        //游戏刚开始,第一次出牌
        if (lastPokers == null || lastPokers.getId() == id){
            if (lastPokers !=null && lastPokers.getId() == id){
                logger.debug(currentUser + " 本轮可以随意出牌，因为他的牌没有人要的过");
            }
            //从手牌中出牌
            byte[] pop = pokerGroup.pop(instance);
            logger.debug("第一次出牌...");
            if (pop == null){
                logger.debug(currentUser + " 出牌失败，手牌中没有足够的牌");
                currentUser.getChannel().writeAndFlush(MessageCommand.buildMsg("您的出牌有误,请检查出牌是否合法;",true));
                return 0;
            }else{
                if (pokerGroupType == PokerGroupType.FOUR || pokerGroupType == PokerGroupType.JOKER){
                    logger.debug(currentUser + " 出了炸弹，倍数 * 2");
                    multiple *= 2;
                }
                if (pokerGroup.getSize() == 0){
                    boolean flag = currentUser.getChannel().id() == masterId;
                    logger.debug(currentUser + " 出玩了所有的牌，游戏结束," + (flag?"地主获胜":"农民获胜"));
                    return flag?1: 2;
                }else{
                    User nextUser = getNextUser();
                    lastPokers = instance;
                    //给玩家分发信息...
                    notifyPlayer(pop,pokerGroup.getSize(),currentUser,nextUser);
                    return 0;
                }
            }
        }else{
            if (lastPokers.compareTo(instance)){
                logger.debug(currentUser + " 的 '" + new String(charArray) + "' 大过上次的出牌,可以出牌");
                byte[] pop = pokerGroup.pop(instance);
                if (pop == null){
                    logger.debug(currentUser + " 出牌失败，手牌中没有足够的牌");
                    currentUser.getChannel().writeAndFlush(MessageCommand.buildMsg("您的出牌有误,请检查出牌是否合法;",true));
                    return 0;
                }else{
                    if (pokerGroupType == PokerGroupType.FOUR || pokerGroupType == PokerGroupType.JOKER){
                        logger.debug(currentUser + " 出了炸弹，倍数 * 2");
                        multiple *= 2;
                    }
                    if (pokerGroup.getSize() == 0) {
                        boolean flag = currentUser.getChannel().id() == masterId;
                        logger.debug(currentUser + " 出玩了所有的牌，游戏结束," + (flag?"地主获胜":"农民获胜"));
                        return flag?1: 2;
                    }else{
                        User nextUser = getNextUser();
                        lastPokers = instance;
                        notifyPlayer(pop,pokerGroup.getSize(),currentUser,nextUser);
                    }
                }

            }else{
                currentUser.getChannel().writeAndFlush(MessageCommand.buildMsg("您的出牌有误,请检查出牌是否合法",true));
                return 0;
            }
        }
        return 0;
    }
    //出的牌组
    private void notifyPlayer(byte[] publishPokers,int remain,User currentUser,User nextUser){
        String publishUsername = currentUser.getName();
        String nextName = nextUser.getName();
        String msg1 = (currentUser.getChannel().id() ==masterId ?"地主":"农民") + "[ "+publishUsername+" ] 出牌完毕 , 他还有"+remain+"张牌;";
        String msg2 = "下一位出牌的玩家是[ " + nextName + " ]";
        users.forEach(user -> {
            Channel channel = user.getChannel();
            channel.writeAndFlush(MessageCommand.buildMsg(msg1,false));
            if (publishPokers == null){
                channel.writeAndFlush(MessageCommand.buildMsg("该玩家跳过了本回合;",false));
            }else{
                channel.writeAndFlush(PokerGroupCommand.buildClientMsg(publishPokers,false));
            }
            if (channel.id() == nextUser.getChannel().id()){
                channel.writeAndFlush(MessageCommand.buildMsg("下边是你的牌:",false));
                channel.writeAndFlush(PokerGroupCommand.buildClientMsg(pokerGroups.get(channel.id()).getPokersIndex(),false));
            }
            channel.writeAndFlush(MessageCommand.buildMsg(msg2,nextUser.getChannel().id() == channel.id()));
        });
    }
}

