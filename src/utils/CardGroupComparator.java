package utils;

import java.util.Comparator;

public class CardGroupComparator implements Comparator {
    /**implements Comparator<CardGroup>为什么会导致失败？**/
    @Override
    public int compare(Object arg0, Object arg1) {
        CardGroup group1 = (CardGroup)arg0;
        CardGroup group2 = (CardGroup)arg1;
        if (group2.getPower() > group1.getPower())
            return 1;
        else if (group2.getPower() < group1.getPower())
            return -1;
        return 0;
    }
}
