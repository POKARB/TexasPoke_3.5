package utils;

import java.util.Comparator;

//将扑克从大到小排序
public class PokerComparator implements Comparator {

    @Override
    public int compare(Object arg0, Object arg1) {
        Poker poker1 = (Poker)arg0;
        Poker poker2 = (Poker)arg1;

        return poker2.getValue() - poker1.getValue();
    }

}