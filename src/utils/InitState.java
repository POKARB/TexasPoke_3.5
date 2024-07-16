package utils;

public class InitState {
    private String name;        //玩家名
    private int jetton;             //玩家剩余筹码

    public InitState(String name, int jetton) {
        this.name = name;
        this.jetton = jetton;
    }

    public String getPlayerNAME() {
        return this.name;
    }

    public int getJetton() {
        return this.jetton;
    }
}

