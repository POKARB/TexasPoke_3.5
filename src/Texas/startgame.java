package Texas;

//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Scanner;
import java.lang.String;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

//import utils.Color;
import utils.Poker;
import strategy.TwoAi;
import utils.BetState;
import utils.SuperAI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class startgame {
    private TwoAi ai;
    //Scanner reader=new Scanner(System.in);
    BetState state;
    int ravalbet;
    String data="";
    String result="";
    //boolean check=true;//检查输入信息是否更新
    public static int earings = 0;  //记录总共挣得的筹码
    public static boolean raiseflag;  //记录对手是否raise
    public startgame() {
        state=new BetState("0000",20000,0,null,"1111",20000,0,null);
        ravalbet=0;
        ai=new TwoAi("0000");
    };
    public void game() throws IOException {
        //int nu;//记录对手（sb）preflop阶段call的次数
        int gap = 10;
        JFrame receiver = new JFrame("interface1");
        //JFrame launcher=new JFrame("interface2");
        receiver.setSize(400, 400);
        receiver.setLocation(200, 100);
        receiver.setLayout(null);
		/*launcher.setSize(400, 400);
		launcher.setLocation(200, 500);
		launcher.setLayout(null);*/

        JPanel pInput = new JPanel();
        pInput.setBounds(gap, gap, 350, 120);
        pInput.setLayout(new GridLayout(2,3,gap,gap));
		/*JPanel pOutput = new JPanel();
		pOutput.setBounds(gap, gap, 350, 120);
		pOutput.setLayout(new GridLayout(2,3,gap,gap));*/



        JLabel location = new JLabel("获取信息:");
        JTextField locationText = new JTextField();
        //JLabel location_=new JLabel("发送信息：");
        //JTextField location_Text=new JTextField();

        JButton b = new JButton("生成");

        pInput.add(location);
        pInput.add(locationText);
        //pOutput.add(location_);
        //pOutput.add(location_Text);


        //receive文本域
        JTextArea ta = new JTextArea();
        ta.setLineWrap(true);
        b.setBounds(150, 120 + 30, 80, 30);
        ta.setBounds(gap, 150 + 60, 375, 120);
        ta.setLineWrap(true);// 激活自动换行功能
        //launcher文本域
        /*
        JTextArea wo = new JTextArea();
        wo.setLineWrap(true);
        //b.setBounds(150, 120 + 30, 80, 30);
        wo.setBounds(gap, 150 + 60, 375, 120);
        wo.setLineWrap(true);// 激活自动换行功能
		*/

        receiver.add(pInput);
        receiver.add(b);
        receiver.add(ta);
        //receiver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //launcher.add(pOutput);
        //launcher.add(b);
        //launcher.add(wo);
        //launcher.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        receiver.setVisible(true);
        //launcher.setVisible(true);
        //byte[] bytes;

        String address=Monitor(receiver,locationText,ta,b);//interface1输入ip地址


        //连接服务端，连接成功后，进入玩家，开始游戏
        try (SocketChannel socketChannel = SocketChannel.open()) {
            //连接服务端socket
            // socketChannel.configureBlocking(false);
            SocketAddress socketAddress = new InetSocketAddress(address, 10001);
            socketChannel.connect(socketAddress);


            System.out.println("已建立连接;");


            ByteBuffer readbuffer = ByteBuffer.allocate(1024);
            ByteBuffer writebuffer = ByteBuffer.allocate(1024);
            //创建容量为1024字节的缓冲区
            //这里最好使用selector处理   这里只是为了写的简单

            //接收服务器传来的信息
            data=ReaderSocket(socketChannel,readbuffer);

            //wo.setText("");
            //wo.append(data);
            System.out.println(data);

            System.out.println("向服务端输入name：");
            //String name=Monitor(receiver,locationText,ta,b);//interface1输入name
            //向服务器传出name
            String name="PolarB";
            System.out.println(name);
            WriterSocket(name,writebuffer,socketChannel);

            for(int i=0;;i++) {
                String action = "";
                String rivalaction="";


                while(!(data.contains("preflop")||data.contains("flop")||data.contains("turn")
                        ||data.contains("river")||data.contains("earn")||data.contains("oppo")) ){//检查数据输入是否正确，不正确再重新输入
                    //接收服务器传来的信息
                    data=ReaderSocket(socketChannel,readbuffer);
                    //data=Monitor(receiver,locationText,ta,b);
                }
                System.out.println(data);
                if(data.contains("earnChips")||data.contains("oppo")) {//新的一轮
                    if(data.contains("earnChips")) {
                        String value = data.substring(10);
                        earings+=Integer.parseInt(value);
                        System.out.println("目前已经挣得：");
                        System.out.println(earings);
                    }
                    raiseflag = false;
                    //接收服务器传来的信息
                    String id = String.valueOf(i);
                    System.out.println(data);
                    state=new BetState(id,20000,0,null,"1111",20000,0,null);//重置玩家
                    ravalbet=0;
                    ai=new TwoAi(id);


                }
                String[]inputData=data.split("\\|");
                data="";
                state.setAction(null);//将玩家行为设置为null，之后用于判断是否是第一行为






                if (inputData[0].contains("preflop")) {//preflop阶段
                    raiseflag = false;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ai.setStage("preflop");
                    int PFinitJetton=state.getJetton();
                    int PFinitRivalJetton=state.getRivalJetton();
                    this.setHoldInfo(inputData[2]);
                    this.setBlindInfo(inputData[1]);
                    while(true) {
                        //当我是小盲注时，preflop阶段，我方表态
                        if(ai.getIdentity().equals("SB")) {
                            if(state.getAction()==null) {//首先表态
                                //玩家表态
                                System.out.println("我方表态");
                                action=ai.thinkAfterHold(state);
                                if(action.equals("fold"))  {
                                    state.setAction(action);//记录玩家行为
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);


                                    System.out.println(action);
                                    break;
                                }
                                else if(action.equals("call")||action.equals("check")) {//小盲注的第一行为为check，转换为call
                                    // if(action.equals("check")) System.out.equals("策略出错，请检查");
                                    action="call";
                                    //小盲注第一行为为call不进入游戏的下一阶段
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(state.getRivalBet());//记录玩家下注额
                                    state.setJetton(PFinitJetton-state.getBet());//记录玩家剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    System.out.println(action);
                                }
                                else if(action.contains("raise")) {
                                    if(state.getAction()==null) {//我为大盲注且第一行为为raise时，下注为盲注的两倍
                                        action = "raise 200";
                                        state.setBet(2*state.getRivalBet());//记录玩家下注总额
                                        state.setJetton(PFinitJetton-state.getBet());//设置剩余筹码量
                                        state.setAction("raise");//记录玩家行为
                                        ai.setBetStates(state);//更新状态
                                        System.out.println(action);
                                    }
                                    else {
                                        String value = action.substring(6);
                                        state.setAction("raise");//记录玩家行为
                                        state.setBet(Integer.parseInt(value));//记录玩家下注额
                                        state.setJetton(PFinitJetton - state.getBet());//记录玩家剩余筹码量
                                        ai.setBetStates(state);//更新状态
                                        System.out.println(action);
                                    }
                                }
                                else if(action.equals("allin")) {
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(20000);//记录玩家下注额
                                    state.setJetton(0);//记录玩家剩余筹码量

                                    System.out.println(action);
                                }

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                            }

                            //轮到对手表态
                            System.out.println("轮到对手表态:");
                            //接收服务器传来的信息
                            data=ReaderSocket(socketChannel,readbuffer);
                            rivalaction=data;//赋值rivalaction
                            System.out.println(rivalaction);
                            if(rivalaction.equals("fold")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态8
                                break;//对手玩家行为为fold或，结束此轮牌局
                            }

                            if((data.contains("preflop")||data.contains("flop")||
                                    data.contains("turn")||data.contains("river")||
                                    data.contains("earn")||data.contains("oppo")))break;

                            else if(rivalaction.equals("call")) {//对手玩家行为为call，得出对手玩家的下注量，结束此轮牌局
                                state.setRivalBet(state.getBet());//设置下注总额
                                state.setRivalJetton(PFinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;
                            }
                            else if(rivalaction.equals("check")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;
                            }
                            else if(rivalaction.contains("raise")) {
                                String[] Action=rivalaction.split(" ");
                                state.setRivalAction(Action[0]);//记录对手行为
                               // raiseflag = true;
                                state.setRivalBet(Integer.parseInt(Action[1],10));//记录对手下注总额
                                state.setRivalJetton(PFinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态
                            }
                            else if(rivalaction.equals("allin")) {
                                state.setRivalBet(20000);
                                state.setRivalJetton(0);//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                            }

                            //轮到玩家表态
                            System.out.println("轮到玩家表态:");
                            action = ai.thinkAfterHold(state);
                            if(action=="fold")  {
                                state.setAction(action);//记录玩家行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if(action.equals("call")||action.equals("check")) {//只有对方为raise时才会轮到我方表态，这时我不能为check
                                //if(action.equals("check")) System.out.equals("策略出错，请检查");
                                action="call";
                                state.setAction(action);//记录玩家行为
                                state.setBet(state.getRivalBet());//记录玩家下注总额
                                state.setJetton(PFinitJetton-state.getBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if(action.contains("raise")) {
                                if(state.getRavalAction()==null) {//我为大盲注且第一行为为raise时，下注为盲注的两倍
                                    action = "raise 200";
                                    state.setBet(2*state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(PFinitJetton-state.getBet());//设置剩余筹码量
                                    state.setAction("raise");//记录玩家行为
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                }
                                else {
                                    String value = action.substring(6);
                                    state.setAction("raise");//记录玩家行为
                                    state.setBet(Integer.parseInt(value));//记录玩家下注额
                                    state.setJetton(PFinitJetton - state.getBet());//记录玩家剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action, writebuffer, socketChannel);

                                    System.out.println(action);
                                }
                            }
                            else if(action.equals("allin")) {
                                if(state.getRavalAction().equals("allin")) {
                                    // System.out.println("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(20000);//记录玩家下注总额
                                    state.setJetton(0);//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                state.setBet(state.getJetton());
                                state.setJetton(PFinitJetton-state.getBet());//设置剩余筹码量
                                state.setRivalAction(action);//记录对手行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                            }
                        }

                        //当玩家是大盲注时,preflop阶段
                        if(ai.getIdentity().equals("BB")) {

                            //对手表态
                            System.out.println("轮到对手表态:");
                            //接收服务器传来的信息
                            data=ReaderSocket(socketChannel,readbuffer);
                            rivalaction=data;//更新rivalaction数据
                            System.out.println(rivalaction);
                            if(rivalaction.equals("fold")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;//对手玩家行为为fold或，结束此轮牌局
                            }

                            if((data.contains("preflop")||data.contains("flop")||
                                    data.contains("turn")||data.contains("river")||
                                    data.contains("earn")||data.contains("oppo")))break;

                            else if(rivalaction.equals("call")) {//对手玩家行为为call，得出对手玩家的下注量，结束此轮牌局
                                state.setRivalBet(state.getBet());
                                state.setRivalJetton(PFinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                // if(state.getRavalAction()!=null) break;//如果小盲注的call行为不是第一行为，结束本轮，进入flop阶段
                            }
                            else if(rivalaction.contains("raise")) {
                                String[] Action=rivalaction.split(" ");
                                state.setRivalAction(Action[0]);//记录对手行为
                              //  raiseflag = true;
                                state.setRivalBet(Integer.parseInt(Action[1],10));//记录对手下注总额
                                state.setRivalJetton(PFinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态
                            }
                            else if(rivalaction.equals("check")) {
                                System.out.println("对手行为非法");
                                break;
                            }
                            else if(rivalaction.equals("allin")) {
                                state.setRivalBet(20000);
                                state.setRivalJetton(0);//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                            }

                            //玩家表态
                            System.out.println("轮到我方表态：");
                            action = ai.thinkAfterHold(state);
                            ai.setBetStates(state);
                            if(action.equals("fold"))  {
                                state.setAction(action);//记录玩家行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if((action.equals("call")||action.equals("check"))) {//
                                if(action.equals("check")&&state.getRavalAction().equals("call")) {
                                    //玩家为大盲注，同时对方出call时，我方才能出check，进入下一阶段
                                    state.setAction(action);//记录玩家行为
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                //小盲注的第一行为为call，则玩家的行为不能为call,转化为check
                                else if(state.getRavalAction().equals("call")&&action=="call") {
                                    //System.out.equals("策略出错，请检查");
                                    action="check";
                                    state.setAction("check");//记录玩家行为
                                    state.setBet(2*state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(PFinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                else {
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(PFinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                            }
                            else if(action.contains("raise")) {
                                if(rivalaction.contains("raise")) {
                                    String value = action.substring(6);
                                    state.setAction("raise");//记录玩家行为
                                    state.setBet(Integer.parseInt(value));//记录玩家下注额
                                    state.setJetton(PFinitJetton - state.getBet());//记录玩家剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action, writebuffer, socketChannel);

                                    System.out.println(action);
                                }
                                else {
                                    action = "raise 200";
                                    state.setBet(2*state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(PFinitJetton-state.getBet());//设置剩余筹码量
                                    state.setAction("raise");//记录玩家行为
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                }
                            }
                            else if(action.equals("allin")) {
                                if(state.getRavalAction().equals("allin")) {
                                    // System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(20000);//记录玩家下注总额
                                    state.setJetton(0);//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                state.setBet(state.getJetton());
                                state.setJetton(PFinitJetton-state.getBet());//设置剩余筹码量
                                state.setRivalAction(action);//记录对手行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                            }
                        }
                    }
                }
                else if (inputData[0].contains("flop")){//flop阶段
                    ai.setStage(inputData[0]);
                    int FinitJetton=state.getJetton();
                    int FinitRivalJetton=state.getRivalJetton();
                    this.setFlopInfo(inputData[1]);
                    state.setBet(state.getJetton(),state.getRivalJetton(), 0, 0);
                    while(true) {

                        //当玩家是大盲注时
                        if(ai.getIdentity().equals("BB")) {
                            //玩家表态
                            System.out.println("轮到我方表态");
                            action=ai.thinkAfterFlop(state);   //
                            if(action=="fold") {
                                state.setAction(action);//记录玩家行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if(action.equals("call")) {
                                if(state.getAction()==null) {//flop阶段大盲注第一行为不能为call，转化为check
                                        //.out.equals("策略出错，请检查");
                                    action="check";
                                    state.setAction("check");//记录玩家行为
                                        //state.setBet(2*ai.getBlind());//记录玩家下注总额，大盲注的第一行为为raise时，下注金额为盲注的两倍
                                    state.setJetton(FinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                }
                                else {
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(FinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                            }
                            else if(action.equals("check")) {
                                if(state.getAction()==null) {//我是大盲注，同时是第一行为为check
                                    state.setAction(action);//记录玩家行为
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                }
                                else if(rivalaction.contains("raise")){//当对方为raise时，我check非法，改为call
                                    // System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(FinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                else if(state.getRavalAction().equals("call"))
                                {
                                    System.out.println("服务器不应发送call");
                                }
                                else
                                {
                                    System.out.println("对手行为非法");
                                }
                            }
                            else if(action.contains("raise")) {
                                if(rivalaction.contains("raise")) {
                                    String value = action.substring(6);
                                    state.setAction("raise");//记录玩家行为
                                    state.setBet(Integer.parseInt(value));//记录玩家下注额
                                    state.setJetton(FinitJetton - state.getBet());//记录玩家剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action, writebuffer, socketChannel);

                                    System.out.println(action);
                                }
                                else {
                                    action = "raise 200";
                                    state.setBet(2*state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(FinitJetton-state.getBet());//设置剩余筹码量
                                    state.setAction("raise");//记录玩家行为
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                }
                            }
                            else if(action.equals("allin")) {
                                if(state.getRavalAction().equals("allin")) {
                                    //System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(20000);//记录玩家下注总额
                                    state.setJetton(0);//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                state.setBet(state.getJetton());
                                state.setJetton(FinitJetton-state.getBet());//设置剩余筹码量
                                state.setRivalAction(action);//记录对手行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                            }

                            //对手表态
                            System.out.println("轮到对手表态:");

                            //接收服务器传来的信息
                            data=ReaderSocket(socketChannel,readbuffer);
                            rivalaction=data;//更新rivalaction数据
                            if(rivalaction.equals("fold")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;//对手玩家行为为fold或，结束此轮牌局
                            }

                            if((data.contains("preflop")||data.contains("flop")||
                                    data.contains("turn")||data.contains("river")||
                                    data.contains("earn")||data.contains("oppo")))break;

                            else if(rivalaction.equals("call")) {//对手玩家行为为call，得出对手玩家的下注量，结束此轮牌局
                                System.out.println("服务器不应发送call");
                                /*
                                state.setRivalBet(state.getBet());
                                state.setRivalJetton(FinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;//如果小盲注的call行为不是第一行为，结束本轮，进入flop阶段
                                 */
                            }
                            else if(rivalaction.contains("raise")) {
                                String[] Action=rivalaction.split(" ");
                                state.setRivalAction(Action[0]);//记录对手行为
                                //raiseflag = true;
                                state.setRivalBet(Integer.parseInt(Action[1],10));//记录对手下注总额
                                state.setRivalJetton(FinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态
                            }
                            else if(rivalaction.equals("check")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;
                            }
                            else if(rivalaction.equals("allin")) {
                                state.setRivalBet(20000);
                                state.setRivalJetton(0);//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                            }
                        }

                        //当玩家是小盲注时
                        if(ai.getIdentity().equals("SB")) {
                            //对手表态;
                            System.out.println("轮到对手表态:");
                            //接收服务器传来的信息
                            data=ReaderSocket(socketChannel,readbuffer);
                            rivalaction=data;//更新rivalaction数据
                            if(rivalaction.equals("fold")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;//对手玩家行为为fold或，结束此轮牌局
                            }

                            if((data.contains("preflop")||data.contains("flop")||
                                    data.contains("turn")||data.contains("river")||
                                    data.contains("earn")||data.contains("oppo")))break;

                            else if(rivalaction.equals("call")) {//对手玩家行为为call，得出对手玩家的下注量，结束此轮牌局
                                System.out.println("服务器不应发送call");
                                state.setRivalBet(state.getBet());
                                state.setRivalJetton(FinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                if(state.getRavalAction()!=null) break;//如果小盲注的call行为不是第一行为，结束本轮，进入flop阶段
                            }
                            else if(rivalaction.contains("raise")) {
                                String[] Action=rivalaction.split(" ");
                                state.setRivalAction(Action[0]);//记录对手行为
                              //  raiseflag = true;
                                state.setRivalBet(Integer.parseInt(Action[1],10));//记录对手下注总额
                                state.setRivalJetton(FinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态
                            }
                            else if(rivalaction.equals("check")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                            }
                            else if(rivalaction.equals("allin")) {
                                state.setRivalBet(20000);
                                state.setRivalJetton(0);//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                            }

                            //玩家表态
                            System.out.println("轮到我方表态：");
                            action=ai.thinkAfterFlop(state);
                            if(action.equals("fold")) {
                                state.setAction(action);//记录玩家行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if(action.equals("call")) {
                                state.setAction(action);//记录玩家行为
                                state.setBet(state.getRivalBet());//记录玩家下注总额
                                state.setJetton(FinitJetton-state.getBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if(action.equals("check")) {
//                                if(state.getRavalAction().equals("call")) {
//                                    state.setAction(action);//记录玩家行为
//                                    ai.setBetStates(state);//更新状态
//
//                                    //向服务器传出信息
//                                    WriterSocket(action,writebuffer,socketChannel);
//
//                                    System.out.println(action);
//                                    break;
//                                }
//                                else {
//                                    // System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(FinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                            }
                            else if(action.contains("raise")) {
                                if(state.getRivalBet()==0)  //如果BB第一手raise,else中的代码会raise 0
                                {
                                    action = "raise 200";
                                    state.setBet(2*state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(FinitJetton-state.getBet());//设置剩余筹码量
                                }
                                else {
                                    String value = action.substring(6);
                                    state.setAction("raise");//记录玩家行为
                                    state.setBet(Integer.parseInt(value));//记录玩家下注额
                                    state.setJetton(FinitJetton-state.getBet());//记录玩家剩余筹码量
                                    ai.setBetStates(state);//更新状态
                                }
                                state.setAction(action);//记录玩家行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                            }
                            else if(action.equals("allin")) {
                                if(state.getRavalAction().equals("allin")) {
                                    //System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(20000);//记录玩家下注总额
                                    state.setJetton(0);//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                state.setBet(state.getJetton());
                                state.setJetton(FinitJetton-state.getBet());//设置剩余筹码量
                                state.setRivalAction(action);//记录对手行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                            }
                        }
                    }
                }  //flop差不多改好了
                else if (inputData[0].contains("turn"))  {  //逻辑copy于flop，但具体变量有变化
                    ai.setStage("turn");
                    int TinitJetton=state.getJetton();
                    int TinitRivalJetton=state.getRivalJetton();
                    this.setTurnInfo(inputData[1]);
                    state.setBet(state.getJetton(),state.getRivalJetton(), 0, 0);
                    while(true) {

                        //当玩家是大盲注时
                        if(ai.getIdentity().equals("BB")) {
                            //玩家表态
                            System.out.println("轮到我方表态");
                            action=ai.thinkAfterTurn(state);
                            if(action=="fold") {
                                state.setAction(action);//记录玩家行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if(action.equals("call")) {
                                if(state.getAction()==null) {//flop阶段大盲注第一行为不能为call，转化为check
                                    //.out.equals("策略出错，请检查");
                                    action="check";
                                    state.setAction("check");//记录玩家行为
                                    //state.setBet(2*ai.getBlind());//记录玩家下注总额，大盲注的第一行为为raise时，下注金额为盲注的两倍
                                    state.setJetton(TinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                }
                                else {
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(TinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                            }
                            else if(action.equals("check")) {
                                if(state.getAction()==null) {//我是大盲注，同时是第一行为为check
                                    state.setAction(action);//记录玩家行为
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                }
                                else if(rivalaction.contains("raise")){//当对方为raise时，我check非法，改为call
                                    // System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(TinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                else if(state.getRavalAction().equals("call"))
                                {
                                    System.out.println("服务器不应发送call");
                                }
                                else
                                {
                                    System.out.println("对手行为非法");
                                }
                            }
                            else if(action.contains("raise")) {
                                if(rivalaction.contains("raise")) {
                                    String value = action.substring(6);
                                    state.setAction("raise");//记录玩家行为
                                    state.setBet(Integer.parseInt(value));//记录玩家下注额
                                    state.setJetton(TinitJetton - state.getBet());//记录玩家剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action, writebuffer, socketChannel);

                                    System.out.println(action);
                                }
                                else {
                                    action = "raise 200";
                                    state.setBet(2*state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(TinitJetton-state.getBet());//设置剩余筹码量
                                    state.setAction("raise");//记录玩家行为
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                }
                            }
                            else if(action.equals("allin")) {
                                if(state.getRavalAction().equals("allin")) {
                                    System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(20000);//记录玩家下注总额
                                    state.setJetton(0);//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                state.setBet(state.getJetton());
                                state.setJetton(TinitJetton-state.getBet());//设置剩余筹码量
                                state.setRivalAction(action);//记录对手行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                            }

                            //对手表态
                            System.out.println("轮到对手表态:");

                            //接收服务器传来的信息
                            data=ReaderSocket(socketChannel,readbuffer);
                            rivalaction=data;//更新rivalaction数据
                            if(rivalaction.equals("fold")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;//对手玩家行为为fold或，结束此轮牌局
                            }

                            if((data.contains("preflop")||data.contains("flop")||
                                    data.contains("turn")||data.contains("river")||
                                    data.contains("earn")||data.contains("oppo")))break;

                            else if(rivalaction.equals("call")) {
                                System.out.println("服务器不应发送call");
                            }
                            else if(rivalaction.contains("raise")) {
                                String[] Action=rivalaction.split(" ");
                                state.setRivalAction(Action[0]);//记录对手行为
                             //   raiseflag = true;
                                state.setRivalBet(Integer.parseInt(Action[1],10));//记录对手下注总额
                                state.setRivalJetton(TinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态
                            }
                            else if(rivalaction.equals("check")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;
                            }
                            else if(rivalaction.equals("allin")) {
                                state.setRivalBet(20000);
                                state.setRivalJetton(0);//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                            }
                        }

                        //当玩家是小盲注时
                        if(ai.getIdentity().equals("SB")) {
                            //对手表态;
                            System.out.println("轮到对手表态:");
                            //接收服务器传来的信息
                            data=ReaderSocket(socketChannel,readbuffer);
                            rivalaction=data;//更新rivalaction数据
                            if(rivalaction.equals("fold")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;//对手玩家行为为fold或，结束此轮牌局
                            }

                            if((data.contains("preflop")||data.contains("flop")||
                                    data.contains("turn")||data.contains("river")||
                                    data.contains("earn")||data.contains("oppo")))break;

                            else if(rivalaction.equals("call")) {//对手玩家行为为call，得出对手玩家的下注量，结束此轮牌局
                                System.out.println("服务器不应发送call");
                                state.setRivalBet(state.getBet());
                                state.setRivalJetton(TinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                if(state.getRavalAction()!=null) break;//如果小盲注的call行为不是第一行为，结束本轮，进入flop阶段
                            }
                            else if(rivalaction.contains("raise")) {
                                String[] Action=rivalaction.split(" ");
                                state.setRivalAction(Action[0]);//记录对手行为
                              //  raiseflag = true;
                                state.setRivalBet(Integer.parseInt(Action[1],10));//记录对手下注总额
                                state.setRivalJetton(TinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态
                            }
                            else if(rivalaction.equals("check")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                            }
                            else if(rivalaction.equals("allin")) {
                                state.setRivalBet(20000);
                                state.setRivalJetton(0);//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                            }

                            //玩家表态
                            System.out.println("轮到我方表态：");
                            action=ai.thinkAfterTurn(state);
                            if(action.equals("fold")) {
                                state.setAction(action);//记录玩家行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if(action.equals("call")) {
                                state.setAction(action);//记录玩家行为
                                state.setBet(state.getRivalBet());//记录玩家下注总额
                                state.setJetton(TinitJetton-state.getBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if(action.equals("check")) {
//                                if(state.getRavalAction().equals("call")) {
//                                    state.setAction(action);//记录玩家行为
//                                    ai.setBetStates(state);//更新状态
//
//                                    //向服务器传出信息
//                                    WriterSocket(action,writebuffer,socketChannel);
//
//                                    System.out.println(action);
//                                    break;
//                                }
//                                else {
                                    //System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(TinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                            }
                            else if(action.contains("raise")) {
                                if(state.getRivalBet()==0)  //如果BB第一手raise,else中的代码会raise 0
                                {
                                    action = "raise 200";
                                    state.setBet(2*state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(state.getJetton()-state.getBet());//设置剩余筹码量
                                }
                                else {
                                    String value = action.substring(6);
                                    state.setAction("raise");//记录玩家行为
                                    state.setBet(Integer.parseInt(value));//记录玩家下注额
                                    state.setJetton(TinitJetton-state.getBet());//记录玩家剩余筹码量
                                    ai.setBetStates(state);//更新状态
                                }
                                state.setAction(action);//记录玩家行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                            }
                            else if(action.equals("allin")) {
                                if(state.getRavalAction().equals("allin")) {
                                    // System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(20000);//记录玩家下注总额
                                    state.setJetton(0);//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                state.setBet(state.getJetton());
                                state.setJetton(TinitJetton-state.getBet());//设置剩余筹码量
                                state.setRivalAction(action);//记录对手行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                            }
                        }
                    }
                }   //turn差不多改好了
                else if (inputData[0].contains("river")) {
                    raiseflag = false;
                    ai.setStage("river");
                    int RinitJetton=state.getJetton();
                    int RinitRivalJetton=state.getRivalJetton();
                    this.setRiverInfo(inputData[1]);
                    state.setBet(state.getJetton(),state.getRivalJetton(), 0, 0);
                    while(true) {

                        //当玩家是大盲注时
                        if(ai.getIdentity().equals("BB")) {
                            //玩家表态
                            System.out.println("轮到我方表态");
                            action=ai.thinkAfterRiver(state);
                            if(action=="fold") {
                                state.setAction(action);//记录玩家行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if(action.equals("call")) {
                                if(state.getAction()==null) {//flop阶段大盲注第一行为不能为call，转化为check
                                    //.out.equals("策略出错，请检查");
                                    action="check";
                                    state.setAction("check");//记录玩家行为
                                    //state.setBet(2*ai.getBlind());//记录玩家下注总额，大盲注的第一行为为raise时，下注金额为盲注的两倍
                                    state.setJetton(RinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                }
                                else {
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(RinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                            }
                            else if(action.equals("check")) {
                                if(state.getAction()==null) {//我是大盲注，同时是第一行为为check
                                    state.setAction(action);//记录玩家行为
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                }
                                else if(rivalaction.contains("raise")){//当对方为raise时，我check非法，改为call
                                    // System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(RinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                else if(state.getRavalAction().equals("call"))
                                {
                                    System.out.println("服务器不应发送call");
                                }
                                else
                                {
                                    System.out.println("对手行为非法");
                                }
                            }
                            else if(action.contains("raise")) {
                                if(rivalaction.contains("raise")) {
                                    String value = action.substring(6);
                                    state.setAction("raise");//记录玩家行为
                                    state.setBet(Integer.parseInt(value));//记录玩家下注额
                                    state.setJetton(RinitJetton - state.getBet());//记录玩家剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action, writebuffer, socketChannel);

                                    System.out.println(action);
                                }
                                else {
                                    action = "raise 200";
                                    state.setBet(2*state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(RinitJetton-state.getBet());//设置剩余筹码量
                                    state.setAction("raise");//记录玩家行为
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                }
                            }
                            else if(action.equals("allin")) {
                                if(state.getRavalAction().equals("allin")) {
                                    // System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(20000);//记录玩家下注总额
                                    state.setJetton(0);//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                state.setBet(state.getJetton());
                                state.setJetton(RinitJetton-state.getBet());//设置剩余筹码量
                                state.setRivalAction(action);//记录对手行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                            }

                            //对手表态
                            System.out.println("轮到对手表态:");

                            //接收服务器传来的信息
                            data=ReaderSocket(socketChannel,readbuffer);
                            rivalaction=data;//更新rivalaction数据
                            if(rivalaction.equals("fold")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;//对手玩家行为为fold或，结束此轮牌局
                            }

                            if((data.contains("preflop")||data.contains("flop")||
                                    data.contains("turn")||data.contains("river")||
                                    data.contains("earn")||data.contains("oppo")))break;

                            else if(rivalaction.equals("call")) {//对手玩家行为为call，得出对手玩家的下注量，结束此轮牌局
                                System.out.println("服务器不应发送call");
                                /*
                                state.setRivalBet(state.getBet());
                                state.setRivalJetton(FinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;//如果小盲注的call行为不是第一行为，结束本轮，进入flop阶段
                                 */
                            }
                            else if(rivalaction.contains("raise")) {
                                String[] Action=rivalaction.split(" ");
                                state.setRivalAction(Action[0]);//记录对手行为
                            //    raiseflag = true;
                                state.setRivalBet(Integer.parseInt(Action[1],10));//记录对手下注总额
                                state.setRivalJetton(RinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态
                            }
                            else if(rivalaction.equals("check")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;
                            }
                            else if(rivalaction.equals("allin")) {
                                state.setRivalBet(20000);
                                state.setRivalJetton(0);//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                            }
                        }

                        //当玩家是小盲注时
                        if(ai.getIdentity().equals("SB")) {
                            //对手表态;
                            System.out.println("轮到对手表态:");
                            //接收服务器传来的信息
                            data=ReaderSocket(socketChannel,readbuffer);
                            rivalaction=data;//更新rivalaction数据
                            if(rivalaction.equals("fold")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                break;//对手玩家行为为fold或，结束此轮牌局
                            }

                            if((data.contains("preflop")||data.contains("flop")||
                                    data.contains("turn")||data.contains("river")||
                                    data.contains("earn")||data.contains("oppo")))break;

                            else if(rivalaction.equals("call")) {//对手玩家行为为call，得出对手玩家的下注量，结束此轮牌局
                                state.setRivalBet(state.getBet());
                                state.setRivalJetton(RinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                                if(state.getRavalAction()!=null) break;//如果小盲注的call行为不是第一行为，结束本轮，进入flop阶段
                            }
                            else if(rivalaction.contains("raise")) {
                                String[] Action=rivalaction.split(" ");
                                state.setRivalAction(Action[0]);//记录对手行为
                               // raiseflag = true;
                                state.setRivalBet(Integer.parseInt(Action[1],10));//记录对手下注总额
                                state.setRivalJetton(RinitRivalJetton-state.getRivalBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态
                            }
                            else if(rivalaction.equals("check")) {
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                            }
                            else if(rivalaction.equals("allin")) {
                                state.setRivalBet(20000);
                                state.setRivalJetton(0);//设置剩余筹码量
                                state.setRivalAction(rivalaction);//记录对手行为
                                ai.setBetStates(state);//更新状态
                            }

                            //玩家表态
                            System.out.println("轮到我方表态：");
                            action=ai.thinkAfterRiver(state);
                            if(action.equals("fold")) {
                                state.setAction(action);//记录玩家行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if(action.equals("call")) {
                                state.setAction(action);//记录玩家行为
                                state.setBet(state.getRivalBet());//记录玩家下注总额
                                state.setJetton(RinitJetton-state.getBet());//设置剩余筹码量
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                                break;
                            }
                            else if(action.equals("check")) {
//                                if(state.getRavalAction().equals("call")) {
//                                    state.setAction(action);//记录玩家行为
//                                    ai.setBetStates(state);//更新状态
//
//                                    //向服务器传出信息
//                                    WriterSocket(action,writebuffer,socketChannel);
//
//                                    System.out.println(action);
//                                    break;
//                                }
//                                else {
//                                    // System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(RinitJetton-state.getBet());//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                            }
                            else if(action.contains("raise")) {
                                if(state.getRivalBet()==0)  //如果BB第一手raise,else中的代码会raise 0
                                {
                                    action = "raise 200";
                                    state.setBet(2*state.getRivalBet());//记录玩家下注总额
                                    state.setJetton(state.getJetton()-state.getBet());//设置剩余筹码量
                                }
                                else {
                                    String value = action.substring(6);
                                    state.setAction("raise");//记录玩家行为
                                    state.setBet(Integer.parseInt(value));//记录玩家下注额
                                    state.setJetton(RinitJetton-state.getBet());//记录玩家剩余筹码量
                                    ai.setBetStates(state);//更新状态
                                }
                                state.setAction(action);//记录玩家行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                                System.out.println(action);
                            }
                            else if(action.equals("allin")) {
                                if(state.getRavalAction().equals("allin")) {
                                    // System.out.equals("策略出错，请检查");
                                    action="call";
                                    state.setAction(action);//记录玩家行为
                                    state.setBet(20000);//记录玩家下注总额
                                    state.setJetton(0);//设置剩余筹码量
                                    ai.setBetStates(state);//更新状态

                                    //向服务器传出信息
                                    WriterSocket(action,writebuffer,socketChannel);

                                    System.out.println(action);
                                    break;
                                }
                                state.setBet(state.getJetton());
                                state.setJetton(RinitJetton-state.getBet());//设置剩余筹码量
                                state.setRivalAction(action);//记录对手行为
                                ai.setBetStates(state);//更新状态

                                //向服务器传出信息
                                WriterSocket(action,writebuffer,socketChannel);

                            }
                        }
                    }
                }
            }
        }
//        catch (IOException e) {
//            // 处理异常
//            System.err.println("IO 异常发生：" + e.getMessage());
//
//            // 提供用户友好的错误提示
//            JOptionPane.showMessageDialog(null, "发生 IO 异常，请稍后重试", "错误", JOptionPane.ERROR_MESSAGE);
//        }

    }

    /**
     * 每个玩家分发2张底牌
     * @param msg
     */
    private void setHoldInfo(String msg) {
        //  String lines[] = msg.split("\\|");
        Poker poker[] = new Poker[2];
        String items[] =msg.split("<|,|><|,|>");
        poker[0] = this.convertToPoker(items[1], items[2]);
        poker[1] = this.convertToPoker(items[3], items[4]);
        this.ai.addHoldPokers(poker[0], poker[1]);
    }
    /**
     * 将字符串表示的牌转换成Poker
     * @param color
     * @param val
     * @return
     */
    private Poker convertToPoker(String color, String val) {
        Poker poker = null;
        if (color.equals("0"))
            poker = new Poker(0, convertToValue(val));
        else if (color.equals("1"))
            poker = new Poker(1, convertToValue(val));
        else if (color.equals("2"))
            poker = new Poker(2, convertToValue(val));
        else if (color.equals("3"))
            poker = new Poker(3, convertToValue(val));
        return poker;
    }
    /**
     * 将输入的牌值转换成数值
     * @param value
     * @return
     */
    private int convertToValue(String value)
    {
        int value1;
        value1=Integer.parseInt(value);
        value1+=2;
        return value1;
    }
    /**
     * 翻开三张共牌
     * @param msg
     */
    private void setFlopInfo(String msg)
    {
        //String lines[] = msg.split("\\|");
        Poker poker[] = new Poker[3];
        String items[] =msg.split("<|,|><,|>");
        poker[0] = convertToPoker(items[1], items[2]);
        poker[1] = convertToPoker(items[4], items[5]);
        poker[2] = convertToPoker(items[7], items[8]);
        this.ai.addFlopPokers(poker[0], poker[1], poker[2]);
    }
    /**
     * 翻开一张转牌
     * @param msg
     */
    private void setTurnInfo(String msg) {
        //String lines[] = msg.split("\\|");
        String items[] = msg.split("<|,|>");
        Poker poker=new Poker(0,0);
        poker=this.convertToPoker(items[1], items[2]);
        this.ai.addTurnPoker(poker);
    }
    /**
     * 翻开一张河牌
     * @param msg
     */
    private void setRiverInfo(String msg) {
        //String lines[] = msg.split("\\|");
        Poker poker = null;
        String items[] = msg.split("<|,|>");
        poker = this.convertToPoker(items[1], items[2]);
        this.ai.addRiverPoker(poker);
    }
    /**
     * 设置盲注信息
     * @param msg
     */
    private void setBlindInfo(String msg) {//msg=inputData.get(1)
        // String lines[] = msg.split("\\|");
        // int blind = 0;
        if(msg.equals("SMALLBLIND")) {
            ai.setIdentity("SB");
            ai.postBlind();
            state.setBet(19950,19900,50,100);}//小盲注，盲注额为50
        if(msg.equals("BIGBLIND")) {
            ai.setIdentity("BB");
            ai.postBlind();
            state.setBet(19900,19950,100,50);}//大盲注，盲注额为100
        ai.setBetStates(state);
        //this.state("0000",)
        //ai.setBlind(blind);
    }


    //界面鼠标监听
    public String Monitor(JFrame receiver,JTextField locationText,JTextArea ta,JButton b) {
        boolean check=true;

        while(check) {
            //鼠标监听
            b.addActionListener(new ActionListener(){
                boolean checkedpass = true;
                public void actionPerformed(ActionEvent e){
                    checkedpass = true;
                    checkEmpty(locationText,"获取信息");

                    String location = locationText.getText();

                    if(checkedpass){
                        String model = "%s";
                        result= String.format(model, location);
                        ta.setText("");
                        ta.append(result);
                    }

                }

                //检验是否为空
                private void checkEmpty(JTextField tf, String msg){
                    if(!checkedpass)
                        return;
                    String value = tf.getText();
                    if(value.length()==0){
                        JOptionPane.showMessageDialog(receiver, msg + " 不能为空");
                        tf.grabFocus();
                        checkedpass = false;
                    }
                }


            });
            if(!data.equals(result)) {
                data=result;
                check=false;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return data;
    }



    public String ReaderSocket(SocketChannel socketChannel,ByteBuffer readbuffer) throws IOException {
        int readLenth=-1;
        readLenth= socketChannel.read(readbuffer);
        //读取模式
        readbuffer.flip();
        byte[] bytes = new byte[readLenth];

        readbuffer.get(bytes);
        String g=new String(bytes, "UTF-8");
        readbuffer.clear();
        return g;
    }
    public void WriterSocket(String Information,ByteBuffer writebuffer,SocketChannel socketChannel) throws IOException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //socketChannel.socket().setSoTimeout(400);
        String s=Information;
        writebuffer.put(s.getBytes());
        //写入模式
        writebuffer.flip();
        socketChannel.write(writebuffer);
        writebuffer.clear();
    }

}