package org.jaxing.common.game.cmd;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.jaxing.common.entity.room.GameInfo;
import org.jaxing.common.game.BaseCommand;
import org.jaxing.common.utils.CommonUtil;

public class GameInfoCommand extends BaseCommand {
    public static final String name = "gameInfo";
    public static final String info = "用来获取服务器端游戏信息";
    public GameInfoCommand(){
        super(name,info);
        hidden = true;
    }

    @Override
    public boolean serverOtherVal() {
        return false;
    }

    @Override
    public Object serverToDo(ChannelHandlerContext ctx, String s) {
        return null;
    }

    @Override
    public boolean clientOtherVal() {
        return false;
    }

    @Override
    public Object clientToDo(ChannelHandlerContext ctx, String s) {
        String data = CommonUtil.getData(s);
        if (CommonUtil.gameOverFlag.equals(data)){
            return CommonUtil.gameOverFlag;
        }else{
            return JSON.parseObject(data, GameInfo.class);
        }
    }
}
