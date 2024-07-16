package utils;

import java.util.*;
import utils.Constants;
import utils.BetState;
import utils.ProbabilityComputer;
//TwoAI继承的父类抽象类，设置AI算法应具有的属性和方法，

import Texas.startgame;


public class SuperAI
{
    private ArrayList<Poker>holdPokers;//底牌数列
    private ArrayList<Poker>publicPokers;//公共牌数列
    private String playerName;//玩家名
    private int blind;//盲注金额
    private int initjetton;//初始筹码数
    private int actualjetton;//实际剩余筹码数
    private int totaljet;//下注总筹码
    private int playernum=2;//玩家数
    private float winprob;//当前赢牌概率
    private boolean isTheLastOne;//最后一个表态玩家
    private boolean isAllin;//判断对手是否Allin
    private String identity;//玩家身份（大盲注BB或小盲注SB）
    private int statenum;//当前局数
    private boolean befolded;//是否弃牌
    private boolean beraised;//本环节是否已加注
    protected String stage;//当前牌局所处阶段
    private ArrayList<InitState> initStates;//记录双方初始状态
    private BetState betStates;//双方下注状态记录
    private ArrayList<String> activePlayers;

    public SuperAI(String name)
    {//构造函数
        this.holdPokers = new ArrayList<Poker>();
        this.publicPokers = new ArrayList<Poker>();
        this.activePlayers = new ArrayList<String>();
        this.playerName = name;
        this.playernum = 2;
        this.blind=Constants.BLIND;
        this.initjetton=Constants.JETTON;
        this.actualjetton=Constants.JETTON;
        this.befolded = false;
        this.isAllin = false;
        this.winprob = 0;
        this.initStates=null;
        //  this.betStates = new ArrayList<BetState>();
        this.holdPokers.clear();
        this.publicPokers.clear();
        this.activePlayers.clear();
        this.statenum=0;
        this.identity=null;
    }

    //设置玩家大小盲注信息
    public void setIdentity(String s)
    {
        this.identity=s;
    }

    //返回玩家大小盲注信息
    public String getIdentity() {
        return this.identity;
    }

    public void setInitInfo( ArrayList<InitState> states)
    {

        for (InitState state: this.initStates)
        {
            this.activePlayers.add(state.getPlayerNAME());
        }

    }

    public void setPlayerID(String playerID)
    {
        this.playerName = playerID;
    }
    public void setHasRaised(boolean flag)
    {
        this.beraised=flag;
    }
    public int getInitJetton()
    {
        return this.initjetton;
    }
    public boolean getFolded()
    {
        return this.befolded;
    }
    //返回玩家名
    public String getPlayerID()
    {
        return this.playerName;
    }

    //获取剩余玩家手中筹码量
    public int getTotalJetton()
    {
        return this.actualjetton;
    }
    public int gettotaljet() {
        this.totaljet=this.getInitJetton()-this.getTotalJetton();
        return this.totaljet;
    }
    public int getPlayerNum() {
        return this.playernum;
    }
    public int getActiverPlayerNum()
    {
        return this.activePlayers.size();
    }
    public boolean IsTheLastOne()
    {return this.isTheLastOne; }
    public boolean hasAllIn() { return this.isAllin; }
    public float getWinPlayerProb()
    {  return this.winprob; }
    public int getHandNum() {
        return this.statenum;
    }
    public ArrayList<Poker> getHoldPokers() {
        return this.holdPokers;
    }

    public ArrayList<Poker> getPublicPokers() {
        return this.publicPokers;
    }

    //获取盲注信息
    public int getBlind() {
        return this.blind;
    }

    //获取是否加注信息
    public boolean getHasRaised() {
        return this.beraised;
    }

    //设置双方下注状态记录
    public void setBetStates(BetState states) {
        this.betStates = states;
	       /* if (!this.isAllin && this.publicPokers.size() == 0) {
	            for (BetState state: states) {
	                if (state.getAction().equals("all_in")) {
	                    this.isAllin = true;
	                    break;
	                }
	            }
	        }*/
    }

    //设置初始状态
    public void setInitStates(ArrayList<InitState> states) {
        this.initStates = states;
    }
    public void postBlind() {
        if(this.identity=="BB")
        {this.actualjetton-=Constants.BLIND;
        }

        else if(this.identity=="SB")
        {this.actualjetton-=0.5*Constants.BLIND;
        }
    }

    /**
     * 添加两张底牌
     */
    public void addHoldPokers(Poker p1, Poker p2) {
        this.holdPokers.add(p1);
        this.holdPokers.add(p2);
        this.beraised = false;
    }


    /* 添加三张公共牌
     */
    public void addFlopPokers(Poker p1, Poker p2, Poker p3) {
        this.publicPokers.add(p1);
        this.publicPokers.add(p2);
        this.publicPokers.add(p3);
        this.winprob = ProbabilityComputer.computeProbability(
                this.holdPokers, this.publicPokers);
        this.beraised = false;
    }


    /**
     * 添加一张转牌
     */
    public void addTurnPoker(Poker p) {
        this.publicPokers.add(p);
        this.winprob = ProbabilityComputer.computeProbability(
                this.holdPokers, this.publicPokers);
        this.beraised = false;
    }

    /**
     * 添加一张河牌
     */
    public void addRiverPoker(Poker p) {
        this.publicPokers.add(p);

        this.winprob = ProbabilityComputer.computeProbability(
                this.holdPokers, this.publicPokers);

        this.beraised = false;
    }


    public String getResponse(int diff, int jetton) {//diff差注额 jetton 决定传入的筹码量
        if(!(this.getPublicPokers().size() == 0)) {
            float prob = this.getWinPlayerProb();
            System.out.println("获胜的概率为：");
            System.out.println(prob);
        }

        if (jetton == 0 && diff > 0) {
            return "fold";
        }
        else if (jetton == 0 && diff == 0) {
            return "check";
        }
        else if (jetton == diff) {
            this.actualjetton -= jetton;
            return "call";
        }
        else if (jetton > diff) {
            if(jetton > 4 * diff)
            {
                if((this.actualjetton-=8*getMaxBet(this.betStates)-getSelfBet(this.betStates))<0)  //如果剩余筹码不够就allin
                {
                    this.actualjetton = 0;
                    return "allin";
                }
                this.actualjetton-=8*getMaxBet(this.betStates)-getSelfBet(this.betStates);
                return "raise"+" "+8*getMaxBet(this.betStates);
            }
            if(jetton > 2 * diff )
            {
                if((this.actualjetton-=4*getMaxBet(this.betStates)-getSelfBet(this.betStates))<0)  //如果剩余筹码不够就allin
                {
                    this.actualjetton = 0;
                    return "allin";
                }
                this.actualjetton-=4*getMaxBet(this.betStates)-getSelfBet(this.betStates);
                return "raise"+" "+4*getMaxBet(this.betStates);
            }

            if((actualjetton-=2*getMaxBet(this.betStates)-getSelfBet(this.betStates))<0)  //如果剩余筹码不够就allin
            {
                this.actualjetton = 0;
                return "allin";
            }
            this.actualjetton-=2*getMaxBet(this.betStates)-getSelfBet(this.betStates);
            return "raise"+" "+2*getMaxBet(this.betStates);
        }
        else if (jetton >= this.getTotalJetton()) {
            this.actualjetton = 0;
            return "allin";
        }
        else {
            return "fold";
        }
    }
    public int getMaxBet(BetState betstates) {
        int maxBet =(betstates.getBet()>betstates.getRivalBet())?betstates.getBet():betstates.getRivalBet();
        return maxBet;
    }
    public int getSelfBet(BetState betstates) {
        return betstates.getBet();
    }
    public String callByDiff(int diff, int maxMultiple) {
        if (this.getPublicPokers().size() == 0) {//公共牌池为空时
            ArrayList<Poker> hp = this.getHoldPokers();//扑克序列=手牌序列

            if (diff > 4 * maxMultiple * 50)  //如果对手和自己下注相差大于4*maxmultiple*盲注就fold
                return this.getResponse(diff, 0);


            if (this.isHoldBigPair(hp)) {  //如果手牌中有大对子，则call(一般该段程序不执行)
                if (hp.get(0).getValue() >= Constants.GAP_VALUE)
                    return this.getResponse(diff, diff);
                else {
                    if (diff >= this.getTotalJetton())
                        return this.getResponse(diff, 0);
                    return this.getResponse(diff, diff);
                }
            }
            return this.getResponse(diff, diff);
        }
        else if (this.getPublicPokers().size() == 3) {
            float prob = this.getWinPlayerProb();

            if(startgame.earings > 5000){  //根据已经挣得的筹码改变策略
                prob-=0.1;
                if(prob<0){
                    prob = 0;
                }
            }
            else if (startgame.earings > 10000){
                prob-=0.2;
                if(prob<0){
                    prob = 0;
                }
            }
            else if (startgame.earings > 15000){
                prob-=0.3;
                if(prob<0){
                    prob = 0;
                }
            }
            if(startgame.earings < -5000){
                prob+=0.2;
                if(prob>1){
                    prob = 1;
                }
            }


            if (prob < 0.10f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.50f) {
                if (diff <= maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else {
                    return this.getResponse(diff,0);  //fold
                }
            }
            else if (prob < 0.75f) {
                if (diff < 2 * maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else{
                    return this.getResponse(diff, 0);  //fold
                }
            }
            else if (prob < 0.95f) {
                if (diff < 4 * maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else{
                    return this.getResponse(diff, 0);  //fold
                }
            }
            else if (prob < 0.98f) {
                if (diff < 6 * maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else {
                    return this.getResponse(diff, 0);  //fold
                }
            }
            else if (prob < 0.99f) {
                    return this.getResponse(diff, diff);  //call
            }
            return this.getResponse(diff, diff);
        }
        else if (this.getPublicPokers().size() == 4) {
            float prob = this.getWinPlayerProb();

            if(startgame.earings > 5000){  //根据已经挣得的筹码改变策略
                prob-=0.1;
                if(prob<0){
                    prob = 0;
                }
            }
            else if (startgame.earings > 10000){
                prob-=0.2;
                if(prob<0){
                    prob = 0;
                }
            }
            else if (startgame.earings > 15000){
                prob-=0.3;
                if(prob<0){
                    prob = 0;
                }
            }
            if(startgame.earings < -5000){
                prob+=0.2;
                if(prob>1){
                    prob = 1;
                }
            }

            if (prob < 0.20f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.50f) {
                if (diff <= maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else {
                    return this.getResponse(diff, 0);  //fold
                }
            }
            else if (prob < 0.75f) {
                if (diff < 2 * maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else{
                    return this.getResponse(diff, 0);  //fold
                }
            }
            else if (prob < 0.95f) {
                if (diff < 4 * maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else{
                    return this.getResponse(diff, 0);  //fold
                }
            }
            else if (prob < 0.97f) {
                if (diff < 6 * maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else {
                    return this.getResponse(diff, 0);  //fold
                }
            }
            else if (prob < 0.985f) {
                return this.getResponse(diff, diff);  //call
            }
            return this.getResponse(diff, diff);
        }
        else if (this.getPublicPokers().size() == 5) {
            float prob = this.winprob;

            if (prob < 0.25f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.50f) {
                if (diff <= maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else {
                    return this.getResponse(diff, 0);  //fold
                }
            }
            else if (prob < 0.80f) {
                if (diff < 2 * maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else{
                    return this.getResponse(diff, 0);  //fold
                }
            }
            else if (prob < 0.95f) {
                if (diff < 4 * maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else{
                    return this.getResponse(diff, 0);  //fold
                }
            }
            else if (prob < 0.97f) {
                if (diff < 6 * maxMultiple * this.getBlind())
                    return this.getResponse(diff, diff);  //call
                else {
                    return this.getResponse(diff, 0);  //fold
                }
            }
            else if (prob < 0.985f) {
                return this.getResponse(diff, diff);  //call
            }
            return this.getResponse(diff, diff);
        }
        System.out.println("公共牌池溢出");
        return this.getResponse(diff, diff);
    }
    private boolean isHoldBigPair(ArrayList<Poker> hp) {
        // 避免出错
        if (hp == null || hp.size() < 2)
            return false;
            // 手牌是大对：AA, KK, QQ, JJ, 1010等
        else if (hp.get(0).getValue() == hp.get(1).getValue()
                && hp.get(0).getValue() >= Constants.GAP_VALUE)
            return true;
        return false;
    }
    public String raiseByDiff(int diff, int multiple, int maxMultiple) {
        if (this.getPublicPokers().size() == 0) {  //preflop阶段只有手牌是一对时才会raise
            ArrayList<Poker> hp = this.getHoldPokers();
            if (this.isHoldBigPair(hp)) {
                if (hp.get(0).getValue() >= 12)
                    return this.getResponse(diff, 20000);
                else {
                    return this.getResponse(diff, 10000);
                }
            }
        }
        else if (this.getPublicPokers().size() == 3) {
            float prob = this.getWinPlayerProb();

            if(startgame.earings > 8000){  //根据已经挣得的筹码改变策略
                prob-=0.45;
                if(prob<0){
                    prob = 0;
                }
            }
            if(startgame.earings < -5000){
                prob+=0.2;
                if(prob>1){
                    prob = 1;
                }
            }

            if (prob < 0.10f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.50f) {
                if (diff > maxMultiple * this.getBlind() / 2)
                return this.getResponse(diff, 0);

                if (diff >= multiple * this.getBlind() / 2) {
                    return this.getResponse(diff, diff);
                }
                    return this.getResponse(diff, multiple * this.getBlind());
            }
            else if (prob < 0.75f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);

                if (diff >= multiple * this.getBlind()) {
                    return this.getResponse(diff, diff);
                }
                return this.getResponse(diff, 2 * multiple * this.getBlind());
            }
            else if (prob < 0.90f) {
                if (diff >= multiple * this.getBlind()) {
                    return this.getResponse(diff, diff);
                }
                return this.getResponse(diff, 3 * multiple * this.getBlind());
            }
            else if (prob < 0.97f) {
                if (diff >=  2 * multiple * this.getBlind()) {
                    return this.getResponse(diff, diff);
                }
                return this.getResponse(diff, 4 * multiple * this.getBlind());
            }
            else {
                return this.getResponse(diff, 20000);
            }
        }
        else if (this.getPublicPokers().size() == 4) {
            float prob = this.getWinPlayerProb();

            if(startgame.earings > 8000){  //根据已经挣得的筹码改变策略
                prob-=0.45;
                if(prob<0){
                    prob = 0;
                }
            }
            if(startgame.earings < -5000){
                prob+=0.2;
                if(prob>1){
                    prob = 1;
                }
            }

            if (prob < 0.20f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.50f) {
                if (diff > maxMultiple * this.getBlind() / 2)
                    return this.getResponse(diff, 0);

                if (diff >= multiple * this.getBlind() / 2) {
                    return this.getResponse(diff, diff);
                }
                return this.getResponse(diff, multiple * this.getBlind());
            }
            else if (prob < 0.80f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);

                if (diff >= multiple * this.getBlind()) {
                    return this.getResponse(diff, diff);
                }
                return this.getResponse(diff, 2 * multiple * this.getBlind());
            }
            else if (prob < 0.96f) {
                if (diff >= multiple * this.getBlind()) {
                    return this.getResponse(diff, diff);
                }
                return this.getResponse(diff, 3 * multiple * this.getBlind());
            }
            else if (prob < 0.985f) {
                if (diff >=  2 * multiple * this.getBlind()) {
                    return this.getResponse(diff, diff);
                }
                return this.getResponse(diff, 4 * multiple * this.getBlind());
            }
            else {
                return this.getResponse(diff, 20000);
            }
        }
        else if (this.getPublicPokers().size() == 5) {
            float prob = this.getWinPlayerProb();

            if(startgame.earings > 8000){  //根据已经挣得的筹码改变策略
                prob-=0.45;
                if(prob<0){
                    prob = 0;
                }
            }
            if(startgame.earings < -5000){
                prob+=0.2;
                if(prob>1){
                    prob = 1;
                }
            }

            if (prob < 0.25f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.65f) {
                if (diff > maxMultiple * this.getBlind() / 2)
                    return this.getResponse(diff, 0);

                if (diff >= multiple * this.getBlind() / 2) {
                    return this.getResponse(diff, diff);
                }
                return this.getResponse(diff, multiple * this.getBlind());
            }
            else if (prob < 0.85f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);

                if (diff >= multiple * this.getBlind()) {
                    return this.getResponse(diff, diff);
                }
                return this.getResponse(diff, 2 * multiple * this.getBlind());
            }
            else if (prob < 0.95f) {
                if (diff >= multiple * this.getBlind()) {
                    return this.getResponse(diff, diff);
                }
                return this.getResponse(diff, 3 * multiple * this.getBlind());
            }
            else if (prob < 0.985f) {
                if (diff >=  2 * multiple * this.getBlind()) {
                    return this.getResponse(diff, diff);
                }
                return this.getResponse(diff, 4 * multiple * this.getBlind());
            }
            else {
                return this.getResponse(diff, 20000);
            }
        }
        return this.getResponse(diff, multiple * this.getBlind());
    }
}