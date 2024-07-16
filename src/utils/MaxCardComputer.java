package utils;

import java.util.ArrayList;
import java.util.Collections;

//用于从当前已知牌中挑选出组成最大牌型的五张牌

public class MaxCardComputer {
    private ArrayList<Poker> pokers;        //自己可用的所有牌
    private CardGroup maxGroup;             //组成最大牌型的五张牌
    //无最大牌型时计算最大牌型
    public MaxCardComputer(ArrayList<Poker> holdPokers,ArrayList<Poker> publicPokers)
    {//传入手牌数列和公共牌数列
        pokers = new ArrayList<Poker>();
        pokers.addAll(holdPokers);
        if (publicPokers != null)//当公共牌序列不为空时：将公共牌也加入自己的可用牌序列
            pokers.addAll(publicPokers);
        this.computeMaxCardGroup();//计算组成最大牌型的五张
    }
    //有最大牌型时计算新的最大牌型
    public MaxCardComputer(MaxCardComputer oldComputer, Poker poker)
    {
        pokers = new ArrayList<Poker>();
        pokers.addAll(oldComputer.getPokers());

        this.computeMaxCardGroup_New(poker);
        this.pokers.add(poker);
    }

    public ArrayList<Poker> getPokers()
    {
        return this.pokers;
    }

    public CardGroup getMaxCardGroup() {
        return this.maxGroup;
    }

    //计算最大的五张牌，无之前的最大牌型时
    @SuppressWarnings("unchecked")
    private void computeMaxCardGroup()
    {
        if (pokers.size() == 5)
        {//当所得牌型只有五张时，最大牌型就是手中的扑克序列
            CardGroup group = new CardGroup(pokers);
            this.maxGroup = group;
        }
        else if (pokers.size() == 6)
        {
            ArrayList<CardGroup> groups = new ArrayList<CardGroup>();
            //建立一个空的cardgroup数列
            for (int i = 0; i < 6; i++)
            {
                ArrayList<Poker> pokerList = new ArrayList<Poker>(pokers);
                //
                pokerList.remove(i);
                groups.add(new CardGroup(pokerList));
            }
            Collections.sort(groups, new CardGroupComparator());
            this.maxGroup = groups.get(0);
        }
        /**为什么这么找？**/
        else if (pokers.size() == 7)
        {
            ArrayList<CardGroup> groups = new ArrayList<CardGroup>();
            for (int i = 0; i < 7; i++)
            {
                for (int j = 0; j < 7; j++)
                {
                    if (i == j) continue;
                    ArrayList<Poker> pokerList = new ArrayList<Poker>(pokers);
                    Poker pi = pokerList.get(i);
                    Poker pj = pokerList.get(j);
                    pokerList.remove(pi);
                    pokerList.remove(pj);

                    groups.add(new CardGroup(pokerList));
                }
            }
            Collections.sort(groups, new CardGroupComparator());
            this.maxGroup = groups.get(0);
        }
    }

    private void computeMaxCardGroup_New(Poker poker) {
        if (this.pokers.size() == 5) {
            ArrayList<CardGroup> groups = new ArrayList<CardGroup>();
            for (int i = 0; i < 5; i++) {
                ArrayList<Poker> pokerList = new ArrayList<Poker>(
                        pokers);
                pokerList.remove(i);
                pokerList.add(poker);
                groups.add(new CardGroup(pokerList));
            }
            Collections.sort(groups, new CardGroupComparator());
            this.maxGroup = groups.get(0);
        }
        else if (this.pokers.size() == 6) {
            ArrayList<CardGroup> groups = new ArrayList<CardGroup>();
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if (i == j) continue;
                    ArrayList<Poker> pokerList = new ArrayList<Poker>(
                            pokers);
                    Poker pi = pokerList.get(i);
                    Poker pj = pokerList.get(j);
                    pokerList.remove(pi);
                    pokerList.remove(pj);
                    pokerList.add(poker);
                    groups.add(new CardGroup(pokerList));
                }
            }
            Collections.sort(groups, new CardGroupComparator());
            this.maxGroup = groups.get(0);
        }
    }
}
