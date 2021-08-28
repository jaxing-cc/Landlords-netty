package org.jaxing.common.entity.poker;

import lombok.Getter;
import org.jaxing.common.factory.PokerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 玩家持有的牌组
 */
@Getter
public class PokerGroup {
    //牌组大小
    private byte size;
    //牌组链表
    private PokerGroupItem head;
    //链表尾部
    private PokerGroupItem tail;


    public PokerGroup(){
        size = 0;
        head = null;
        tail = null;
    }
    /**
     * 放入一张牌
     * @param item
     */
    public void put(PokerGroupItem item){
        if (size == 0){
            head = item;
            tail = item;
        }else{
            //头插法
            item.setNext(head);
            item.setLast(null);
            head.setLast(item);
            head = item;
        }
        size++;
    }

    public void put(PokerGroup pokerGroup){
        byte size = pokerGroup.getSize();
        if (size == 0){
            return;
        }
        PokerGroupItem head = pokerGroup.getHead();
        this.size += size;
        tail.setNext(head);
        head.setLast(tail);
        tail = pokerGroup.getTail();
    }
    /**
     * 根据pComparablePokerGroup取出牌
     * 返回 0 => 玩家出牌成功，手中已经没有牌了，游戏结束
     * 返回 1 => 出牌成功
     * 返回 -1 => 出牌失败
     * @param pokerGroup 要出的牌组
     * @return 弹出的牌组 返回null表示value存在没有的牌组
     */
    public byte[] pop(ComparablePokerGroup pokerGroup){
        int size = pokerGroup.getSize();
        if (this.size < size){
            return null;
        }
        //检查是否可以出牌
        int []array = new int[16];
        int[] targetArray = pokerGroup.getCountArray();
        forEach(o->{
            Poker poker = PokerFactory.get(o.getPokerId());
            byte value = poker.getValue();
            PokerType type = poker.getType();
            if (type == PokerType.JOKER){
                array[14]++;
            }else if (type == PokerType.SUPER_JOKER){
                array[15]++;
            }else {
                array[value]++;
            }
        });
        for (int i = 0; i < array.length; i++) {
            if (array[i] < targetArray[i]){
                return null;
            }else{
                array[i] = targetArray[i];
            }
        }
        byte []answer = new byte[size];
        List<PokerGroupItem> list = new ArrayList<>();
        forEach(o->{
            byte val = o.val();
            if (val == 0){
                if (o.get().getType() == PokerType.JOKER){
                    val = 14;
                }else{
                    val = 15;
                }
            }
            if (array[val]!= 0){
                array[val]--;
                list.add(o);
            }
        });
        for (int i = 0; i < size; i++) {
            PokerGroupItem pokerGroupItem = list.get(i);
            answer[i] = pokerGroupItem.getPokerId();
            remove(pokerGroupItem);
        }
        return answer;
    }

    public byte[] getPokersIndex(){
        byte [] answer = new byte[size];
        PokerGroupItem p = head;
        for (int i = 0; i < size; i++) {
            answer[i] = p.getPokerId();
            p = p.getNext();
        }
        return answer;
    }
    public boolean contain(byte[] value){
        return false;
    }
    private void remove(PokerGroupItem item){
        PokerGroupItem next = item.getNext();
        PokerGroupItem last = item.getLast();
        if (size == 1){
            head = null;
            tail = null;
        }else if (next == null){
            //最后一个
            last.setNext(null);
            tail = last;
        }else if (last == null){
            //第一个
            head = next;
            next.setLast(null);
        }else{
            last.setNext(next);
            next.setLast(last);
        }
        size--;
    }

    /**
     * 链表归并排序
     * 时间复杂度 log(n)
     */
    public void sort(){
        head = sort(head,size);
        tail = head;
        while (tail.getNext() != null){
            tail = tail.getNext();
        }
    }

    public PokerGroupItem sort(PokerGroupItem head,int size){
        if (size == 1){
            return head;
        }
        int mid = size / 2;
        PokerGroupItem l = head;
        PokerGroupItem r = PokerFactory.cut(head,mid);
        l = sort(l,mid);
        r = sort(r,size - mid);
        return PokerFactory.merge(l,r);
    }
    @Override
    public String toString() {
        PokerGroupItem point = head;
        StringBuilder sb =new  StringBuilder();
        while (point != null){
            Poker poker = point.get();
            sb.append('|').append(poker.getType().tag).append(poker.getValue());
            point = point.getNext();
        }
        sb.append('|').append("--剩余:").append(size);
        return sb.toString();
    }
    public void forEach(LoopFunctionInterface loopFunctionInterface){
        PokerGroupItem point = head;
        while (point != null) {
            loopFunctionInterface.handler(point);
            point = point.getNext();
        }
    }
    public interface LoopFunctionInterface{
        void handler(PokerGroupItem item);
    }
}
