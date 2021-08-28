package org.jaxing.common.utils;

public class CommonUtil {
    //命令分界符
    public static final String tag = "@";
    //消息结尾符
    public static final String end = "\n";
    //处理失败标志
    public static final String errorFlag ="?";
    //处理成功标志
    public static final String successFlag="!";
    //允许用户输入
    public static final String allow = "1";
    //拒绝用户输入
    public static final String refuse = "0";
    //接收到该标志会把客户端GameInfo对象设置为null
    public static final String gameOverFlag = "game over !!!";

    public static String getData(String s){
        String[] split = s.split(tag);
        return split.length == 2? split[1]:"";
    }
}
