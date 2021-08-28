package org.jaxing.test;
import org.jaxing.common.entity.poker.*;
import org.junit.Test;

import java.util.Scanner;


public class Main {
    @Test
    public void test(){
        PokerGroup pokerGroup = new PokerGroup();
        pokerGroup.put(new PokerGroupItem((byte) 0));
        pokerGroup.put(new PokerGroupItem((byte) 1));
        pokerGroup.put(new PokerGroupItem((byte) 52));
        pokerGroup.sort();
        ComparablePokerGroup instance = ComparablePokerGroup.getInstance("S".toCharArray());
        pokerGroup.pop(instance);
        System.out.println(pokerGroup);
    }

    public String toString(PokerGroupItem head) {
        PokerGroupItem point = head;
        StringBuilder sb =new  StringBuilder();
        while (point != null){
            Poker poker = point.get();
            sb.append('|').append(poker.getType().tag).append(poker.getValue());
            point = point.getNext();
        }
        sb.append('|');
        return sb.toString();
    }

    public PokerGroupItem build(int []arr){
        PokerGroup pokerGroup = new PokerGroup();
        for (int i : arr) {
            pokerGroup.put(new PokerGroupItem((byte) i));
        }
        return pokerGroup.getHead();
    }
    private boolean compareAlone(int val1,int val2){
        boolean a12 = val1 == 1 || val1 == 2;
        boolean b12 = val2 == 1 || val2 == 2;
        if (a12 && b12){
            return val1 < val2;
        }else if (a12 || b12){
            return b12 && (val1 != 14 && val1 != 15);
        }else{
            return val1 < val2;
        }
    }
}
