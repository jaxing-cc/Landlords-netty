package org.jaxing.common.game;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.jaxing.common.factory.DataFactory;
import org.jaxing.common.utils.CommonUtil;


public abstract class BaseCommand implements Command{
    protected DataFactory dataFactory = DataFactory.getInstance();
    protected Logger logger = Logger.getLogger(BaseCommand.class);
    //指令名称
    @Getter
    protected String name;

    //处理失败标志
    @Getter
    @Setter
    protected String errorFlag;

    //处理成功标志
    @Getter
    @Setter
    protected String successFlag;

    @Getter
    @Setter
    protected String info;

    @Getter
    @Setter
    protected boolean hidden;

    public BaseCommand(String name, String info) {
        this.name = name;
        this.info = info;
        errorFlag = CommonUtil.errorFlag;
        successFlag = CommonUtil.successFlag;
    }

    @Override
    public String toString() {
        return name + " ==> " + this.info;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        return obj.equals(this.name);
    }

    @Override
    public Object clientToDo(ChannelHandlerContext ctx, String s) {
        if (s.equals(errorFlag)){
            System.out.println("处理失败");
        }
        return null;
    }
}
