package org.jaxing.common.factory;

import org.jaxing.common.entity.poker.*;

import java.util.*;

/**
 * 扑克牌工厂
 */
public class PokerFactory {
    public static Poker[] POKER_MAP;
    public static byte MAX_POKER_SIZE = 54;
    static {
        POKER_MAP = new Poker[MAX_POKER_SIZE];
        byte index = 0;
        byte val = 1;
        while (index < MAX_POKER_SIZE - 2){
            POKER_MAP[index] = new Poker(index,val, PokerType.SPADE);index++;
            POKER_MAP[index] = new Poker(index,val, PokerType.HEARTS);index++;
            POKER_MAP[index] = new Poker(index,val, PokerType.CLUBS);index++;
            POKER_MAP[index] = new Poker(index,val, PokerType.DIAMONDS);index++;
            val++;
        }
        POKER_MAP[index] = new Poker(index, (byte) 0, PokerType.SUPER_JOKER);index++;
        POKER_MAP[index] = new Poker(index, (byte) 0, PokerType.JOKER);
    }
    public static Poker get(byte id){
        return POKER_MAP[id];
    }

    /***
     * 返回一个大小为4的牌组
     * 0:底牌 3张
     * 1:玩家1 17张
     * 2:玩家2 17张
     * 3:玩家3 17张
     * 时间复杂度 n
     * @return
     */
    public static PokerGroup[] wash(){
        byte[] all = new byte[MAX_POKER_SIZE];
        for (byte i = 0; i < MAX_POKER_SIZE; i++) {
            all[i] = i;
        }
        byte item;
        for (byte i = 0; i < MAX_POKER_SIZE; i++) {
            byte target = (byte) (Math.random() * 54);
            if (i != target){
                item = all[i];
                all[i] = all[target];
                all[target] = item;
            }
        }
        PokerGroup[] answer = new PokerGroup[4];
        for (byte i = 0; i < 4; i++) {
            answer[i] = new PokerGroup();
        }
        for (byte i = 0; i < MAX_POKER_SIZE; i++) {
            if (i < 3){
                answer[0].put(new PokerGroupItem(all[i]));
            }else if (i < 20){
                answer[1].put(new PokerGroupItem(all[i]));
            }else if (i < 37){
                answer[2].put(new PokerGroupItem(all[i]));
            }else{
                answer[3].put(new PokerGroupItem(all[i]));
            }
        }
        return answer;
    }
    //归并两个有序链表
    public static PokerGroupItem merge(PokerGroupItem l1,PokerGroupItem l2){
        PokerGroupItem answerHead = null;
        PokerGroupItem answerPoint = null;
        while (l1!=null && l2!= null){
            PokerGroupItem next;
            if (l1.val() > l2.val()){
                next = l1;
                l1 = cut(l1,1);
            }else{
                next = l2;
                l2 = cut(l2,1);
            }
            if (answerHead == null){
                answerHead = next;
                answerPoint = next;
            }else{
                answerPoint.setNext(next);
                next.setLast(answerPoint);
                answerPoint = answerPoint.getNext();
            }
        }
        if (l1 != null){
            if (answerHead == null){
                answerHead = l1;
            }else{
                answerPoint.setNext(l1);
                l1.setLast(answerPoint);
            }
        }
        if (l2 != null){
            if (answerHead == null){
                answerHead = l2;
            }else{
                answerPoint.setNext(l2);
                l2.setLast(answerPoint);
            }
        }
        return answerHead;
    }

    //切掉一个链表前n个节点，返回后边半段
    public static PokerGroupItem cut(PokerGroupItem linkedlist,int n){
        if (n == 0){
            return null;
        }
        for (int i = 0; i < n; i++) {
            linkedlist = linkedlist.getNext();
            if (linkedlist == null){
                return null;
            }
        }
        PokerGroupItem last = linkedlist.getLast();
        linkedlist.setLast(null);
        if (last!=null){
            last.setNext(null);
        }
        return linkedlist;
    }

    public static PokerGroupType checkUserInputPoker(String s){
        String errorMsg = "不符合规则的牌组";
        if ("pass".equals(s)){
            return PokerGroupType.PASS;
        }
        char[] chars = s.toCharArray();
        int len = chars.length;
        if (len == 0){
            throw new RuntimeException("输入的牌组不能为空");
        }
        for (char aChar : chars) {
            if ( (aChar < '0' || aChar >'9') &&
                    aChar != 'a' && aChar != 'A' &&
                    aChar != 'j' && aChar != 'J' &&
                    aChar != 'q' && aChar != 'Q' &&
                    aChar != 'k' && aChar != 'K' &&
                    aChar != 's' && aChar != 'S'){
                throw new RuntimeException("输入的牌组存在不能识别的元素");
            }
        }
        int []countArray = getPokerArray(chars);
        int temp = 0;
        switch (len){
            case 1:return PokerGroupType.ONE;
            case 2:
                if (chars[0] != 'S' && chars[0] != 's' && chars[0] == chars[1]){
                    return PokerGroupType.TWO;
                }else if ((chars[0] == 'S' && chars[1] == 's') || (chars[0] == 's' && chars[1] == 'S')){
                    return PokerGroupType.JOKER;
                }else{
                    throw new RuntimeException(errorMsg);
                }
            case 3:
                if (chars[0]!= 's' && chars[0] != 'S' && chars[0] == chars[1] && chars[1] == chars[2]){
                    return  PokerGroupType.THREE;
                }else{
                    throw new RuntimeException(errorMsg);
                }
            case 4:
                if (chars[0]!= 's' && chars[0] != 'S' && chars[0] == chars[1] && chars[1] == chars[2] && chars[2] == chars[3]){
                    return PokerGroupType.FOUR;
                }else if ((temp =checkThreeAndX(countArray))!= 0){
                    return temp == 1?PokerGroupType.THREE_AND_ONE:PokerGroupType.THREE_AND_TWO;
                }else{
                    throw new RuntimeException(errorMsg);
                }
            default:
                if ((temp =checkThreeAndX(countArray))!= 0){
                    return temp == 1?PokerGroupType.THREE_AND_ONE:PokerGroupType.THREE_AND_TWO;
                }
                PokerGroupType type = checkOtherType(countArray);
                if (type == null){
                    throw new RuntimeException(errorMsg);
                }else{
                    return type;
                }
        }
    }
    //获取计数数组
    public static int[] getPokerArray(char[] chars){
        int []countArray = new int[16];
        for (char aChar : chars) {
            if (aChar>='0' && aChar<='9'){
                if (aChar == '0'){
                    countArray[10]++;
                }else{
                    countArray[aChar -'0']++;
                }
            }else if (aChar=='s'){
                countArray[14]++;
            }else if (aChar == 'S'){
                countArray[15]++;
            }else if (aChar == 'K' || aChar =='k'){
                countArray[13]++;
            }else if (aChar == 'Q' || aChar =='q'){
                countArray[12]++;
            }else if (aChar == 'J' || aChar =='j'){
                countArray[11]++;
            }else if (aChar == 'A' || aChar =='a'){
                countArray[1]++;
            }
        }
        return countArray;
    }
    //检查类型
    private static PokerGroupType checkOtherType(int[] countArray) {
        Set<Integer> set = new HashSet<>();
        for (int i : countArray) {
            if (i > 3){
                return null;
            }
            if (i != 0){
                set.add(i);
            }
        }
        if (set.contains(3)){
            int size = getContinuousNumberSize(3, countArray);
            int twoSize = 0;
            int oneSize = 0;
            for (int i = 1; i <= 13; i++) {
                if (countArray[i] == 2){
                    twoSize++;
                }
                if (countArray[i] == 1){
                    oneSize++;
                }
            }
            if (size >= 2){
                if (oneSize == 0 && twoSize == 0){
                    return PokerGroupType.AIRPLANE;
                }else if (oneSize == 0 && twoSize == size){
                    return PokerGroupType.AIRPLANE_AND_TWO;
                }else if (oneSize == size && twoSize == 0){
                    return PokerGroupType.AIRPLANE_AND_ONE;
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }else if (set.contains(2)){
            return getContinuousNumberSize(2, countArray)>=3?set.size()>1?null:PokerGroupType.MANY_TWO:null;
        }else if (set.contains(1)){
            return getContinuousNumberSize(1, countArray)>=5?set.size()>1?null:PokerGroupType.MANY_ONE:null;
        }
        return null;
    }
    //返回数组中值为 val 的连续长度
    public static int getContinuousNumberSize(int val,int []countArray){
        int count = 0;
        int i = 2;
        while (i <= 13 && countArray[i] != val){
            i++;
        }
        if (i == 2){
            return -1;
        }
        while (i <= 13 && countArray[i] == val){
            count++;
            i++;
        }
        if (i == 14){
            count += countArray[1]==val?1:0;
        }
        return count;
    }
    //返回0标识无法识别
    //返回1标识三带一
    //返回2标识三带对
    private static int checkThreeAndX(int []countArray){
        int three = 0;
        int mainNum = 0;
        int two = 0;
        int one = 0;
        for (int i = 1; i < countArray.length; i++) {
            if (countArray[i]!=0){
                switch (countArray[i]){
                    case 0:break;
                    case 1:one++;break;
                    case 2:two++;break;
                    case 3:three++;mainNum = i;break;
                    default:return 0;
                }
            }
        }
        if (three != 1 || (two + one)!=1 ){
            return 0;
        }
        return mainNum <= 13? one == 0?2:1 :0;
    }
}

