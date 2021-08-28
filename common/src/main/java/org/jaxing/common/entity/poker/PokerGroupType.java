package org.jaxing.common.entity.poker;

import java.util.HashMap;

public enum PokerGroupType {
    PASS("过",(byte)0),
    ONE("单张",(byte)1),
    TWO("对子",(byte)2),
    THREE("三带零",(byte)3),
    THREE_AND_ONE("三带一",(byte)4),
    THREE_AND_TWO("三带二",(byte)5),
    FOUR("炸弹",(byte)6),
    MANY_ONE("顺子",(byte)7),
    MANY_TWO("对子的顺子",(byte)8),
    AIRPLANE("飞机带零",(byte)9),
    AIRPLANE_AND_ONE("飞机带单",(byte)10),
    AIRPLANE_AND_TWO("飞机带对",(byte)11),
    JOKER("王炸",(byte)12);

    public static HashMap<Byte,PokerGroupType> map = new HashMap<>();
    static {
        map.put((byte) 0, PokerGroupType.PASS);
        map.put((byte) 1, PokerGroupType.ONE);
        map.put((byte) 2, PokerGroupType.TWO);
        map.put((byte) 3, PokerGroupType.THREE);
        map.put((byte) 4, PokerGroupType.THREE_AND_ONE);
        map.put((byte) 5, PokerGroupType.THREE_AND_TWO);
        map.put((byte) 6, PokerGroupType.FOUR);
        map.put((byte) 7, PokerGroupType.MANY_ONE);
        map.put((byte) 8, PokerGroupType.MANY_TWO);
        map.put((byte) 9, PokerGroupType.AIRPLANE);
        map.put((byte) 10,PokerGroupType.AIRPLANE_AND_ONE);
        map.put((byte) 11,PokerGroupType.AIRPLANE_AND_TWO);
        map.put((byte) 12,PokerGroupType.JOKER);
    }
    PokerGroupType(String name,byte code){
       this.name = name;
       this.code = code;
    }
    private String name;
    private byte code;

    public byte getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name;
    }
}
