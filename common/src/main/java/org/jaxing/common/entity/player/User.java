package org.jaxing.common.entity.player;

import com.alibaba.fastjson.annotation.JSONField;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 连接后用户实体类
 */
@Getter
@Setter
public class User implements Serializable {
    private String name;
    @JSONField(serialize = false)
    private Channel channel;
    private Long roomId;
    private StatusType type;
    private int coin;
    public User(){
        type = StatusType.FREE;
        coin = 0;
        roomId = null;
        coin = 100;
    }

    public void coinAdd(int val){
        this.coin += val;
    }

    public void coinSubtract(int val){
        this.coin -= val;
    }
    @Override
    public String toString() {
        return name + " ["+channel.remoteAddress()+"]("+type+") ==> coin:" + coin;
    }
}
