package org.jaxing.common.entity.poker;

public enum  PokerType {
    SPADE("黑桃",'♠'),
    HEARTS("红心",'♥'),
    CLUBS("梅花",'♣'),
    DIAMONDS("方片",'♦'),
    SUPER_JOKER("大王",'S'),
    JOKER("小王",'s');
    public String name;
    public char tag;
    PokerType(String name,char tag){
        this.name = name;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return name;
    }
}
