package utils;

//玩家投注状态记录类
public class BetState {
    private String playername1;        //玩家ID
    private String playername2;        //对手ID
    private int jetton1=20000;             //玩家手中筹码
    private int jetton2=20000;             //对手手中筹码
    private int bet1;                //玩家累计投注额
    private int bet2;                //对手累计投注额
    private String action1;          //玩家最近一次action
    private String action2;          //对手最近一次action

    public BetState(String playerID1, int jetton1,  int bet1,
                    String action1,String playerID2, int jetton2,  int bet2,
                    String action2) {
        this.playername1 = playerID1;
        this.jetton1 = jetton1;
        this.bet1 = bet1;
        this.action1 = action1;
        this.playername2=playerID2;
        this.jetton2=jetton2;
        this.bet2=bet2;
        this.action2=action2;
    }

    //设置玩家手中筹码，累计投注额和行为，对手玩家累计额和行为
    public void setBet(int jetton1,int jetton2,int bet1,  int bet2) {
        this.jetton1 = jetton1;
        this.jetton2 = jetton2;
        this.bet1 = bet1;
        //  this.action1 = action1;
        this.bet2=bet2;
        // this.action2=action2;
    }

    //暂时保存玩家剩余筹码
    public void setJetton(int jetton) {
        this.jetton1=jetton;
    }

    //暂时保存玩家累计投注额
    public void setBet(int bet) {
        this.bet1=bet;
    }

    //暂时保存玩家行为
    public void setAction(String action) {
        this.action1=action;
    }

    //暂时保存对手剩余筹码量
    public void setRivalJetton(int jetton) {
        this.jetton2=jetton;
    }

    //暂时保存对手累计筹码量
    public void setRivalBet(int jet) {
        this.bet2=jet;
    }

    //暂时保存对手行为
    public void setRivalAction(String action) {
        this.action2=action;
    }

    //获取玩家ID
    public String getPlayerName() {
        return this.playername1;
    }

    //获取对手ID
    public String getRivalName() {
        return this.playername2;
    }

    //获取玩家剩余筹码量
    public int getJetton() {
        return this.jetton1;
    }

    //获取对手剩余筹码量
    public int getRivalJetton() {
        return this.jetton2;
    }

    //获取玩家总下注量
    public int getBet() {
        return this.bet1;
    }

    //获取对手总下注量
    public int getRivalBet() {
        return this.bet2;
    }

    //获取玩家行为
    public String getAction() {
        return this.action1;
    }

    //获取对手行为
    public String getRavalAction() {
        return this.action2;
    }
}
