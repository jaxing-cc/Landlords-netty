package org.jaxing.common.entity.player;

public enum StatusType {
    FREE("空闲"),
    NOT_READY("等待中"),
    READY("已就绪"),
    UNDERWAY("进行中");
    public String name;
    StatusType(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
