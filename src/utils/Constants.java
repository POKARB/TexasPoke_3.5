package utils;
//常量类
public class Constants {

    public final static int PLAYER_NUM =2;//玩家数量
    public final static int GAP_VALUE = 10; 	// 分界值，大于或等于这个值为Big，小于这个值为Small
    public final static int MAX_TOTAL_MULTIPLE = 3;
    public final static int MAX_BET_MULTIPLE_EACH_HAND = 2;
    public final static int HIGH_BET_MULTIPLE = 10; // 下注时最大跟注的倍数，超过这个倍数时就弃牌
    public final static int MIDDLE_BET_MULTIPLE = 5;//下注中注倍数
    public final static int LOW_BET_MULTIPLE = 3;//下注小注倍数

    public final static int HIGH_RAISE_MULTIPLE = 5;//加注大注倍数
    public final static int MIDDLE_RAISE_MULTIPLE = 3;//加注中注倍数
    public final static int LOW_RAISE_MULTIPLE = 2;//加注小注倍数

    public final static int MAX_FOLD_MULTIPLE = 3; // 当剩余筹码占初始金币不到 1 / MAX_FOLD_MULTIPLE 时，会变得比较保守

    public final static int BLIND=100;
    public final static int JETTON=20000;

    public final static int LESS_CALL_PERCENTAGE = 35; // 牌型不是很好的时候，跟注的概率百分比
    public final static int LESS_GAP_VALUE = 11;//牌型不好时区分大小牌的分界值
    public final static int LESS_MIDDLE_BET_MULTIPLE = 4;//小注下注倍数
}
