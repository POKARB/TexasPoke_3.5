package utils;
//单牌类，用于表示一张扑克

public class Poker {
    private int color;      //花色
    /* SPADES黑桃=0， HEARTS=1红桃，CLUBS=2梅花，DIAMONDS=3方片*/
    private int value;      //2~14点数

    public Poker(int color, int value) {
        this.setColor(color);
        this.setValue(value);
    }

    public int getColor() {
        return this.color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    public int getValue() {
        return this.value;
    }
    public void setValue(int value) {
        assert value >= 2 && value <= 14;//当点数值处于2-14之间时，设置点数
        this.value = value;
    }
}
