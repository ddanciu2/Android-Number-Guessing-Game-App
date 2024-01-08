package com.example.dariandanciu_project4;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.Random;

public class Player2 extends Thread{
    private Handler UIHandler;

    public Handler player2Handler;

    ArrayList<Integer> arrayListP2 = new ArrayList<Integer>();

    String player2SecretNum;

    int P2numDigitsCP = 0;
    int P2numDigitsIP = 0;

    ArrayList<Integer> P2missedDigits = new ArrayList<Integer>();
    ArrayList<Integer> P2StratDigits = new ArrayList<Integer>();
    int p2MD;
    boolean p2FT;

    boolean missNumFlag = true;

    Player2(Handler UIHandlerPassed){
        UIHandler = UIHandlerPassed;
    }

    @Override
    public void run() {
        Looper.prepare();

        player2Handler = new Handler(Looper.myLooper()) {
            public void handleMessage(Message msg) {
                int what = msg.what ;
                Message msg2 = Message.obtain();
                switch (what) {
                    case 1: //Start of game, display player 2 secret #
                        msg2.what = 2;
                        msg2.setData(msg.getData());
                        UIHandler.sendMessage(msg2);
                        break;
                    case 2: //Send player 2 guess/response
                        msg2.what = 4;
                        msg2.setData(msg.getData());
                        UIHandler.sendMessage(msg2);
                        break;
                    case 3:
                        Looper.myLooper().quit();
                        break;
                }
            }
        };
        Looper.loop();
    }

    //Repopulate an array with numbers 0-9
    public void populateArray(){
        arrayListP2.clear();
        arrayListP2.add(0,0);
        arrayListP2.add(1,1);
        arrayListP2.add(2,2);
        arrayListP2.add(3,3);
        arrayListP2.add(4,4);
        arrayListP2.add(5,5);
        arrayListP2.add(6,6);
        arrayListP2.add(7,7);
        arrayListP2.add(8,8);
        arrayListP2.add(9,9);
    }

    //Strategy used to pick a guess. (Based on missing #s not from the player's secret number)
    public void strategy(){
        //Loop through #s that were missing and remove them from the arrayList/pool of #s to make a guess from
        boolean duplicate = false;
        for(int i = 0; i < P2StratDigits.size(); i++){
            if(P2StratDigits.get(i) == p2MD){
                duplicate = true;
            }
        }
        if(!duplicate && p2MD != -1){
            P2StratDigits.add(p2MD);
        }

        for(int i = 0; i < P2StratDigits.size(); i++){
            arrayListP2.remove(P2StratDigits.get(i));
        }
    }

    //Pick a 4 digit non-repeating # for the player 2's secret number
    public void pickSecretNum(){
        populateArray();

        int randomIndex = new Random().nextInt(10);
        int randomNum1 = arrayListP2.get(randomIndex);
        arrayListP2.remove(randomIndex);

        randomIndex = new Random().nextInt(9);
        int randomNum2 = arrayListP2.get(randomIndex);
        arrayListP2.remove(randomIndex);

        randomIndex = new Random().nextInt(8);
        int randomNum3 = arrayListP2.get(randomIndex);
        arrayListP2.remove(randomIndex);

        randomIndex = new Random().nextInt(7);
        int randomNum4 = arrayListP2.get(randomIndex);
        arrayListP2.remove(randomIndex);

        String CombineString = String.valueOf(randomNum1) + String.valueOf(randomNum2) + String.valueOf(randomNum3) + String.valueOf(randomNum4);
        player2SecretNum = CombineString;

        Message msg = Message.obtain();
        msg.what = 1;
        Bundle bundle = new Bundle();
        bundle.putString("SC", CombineString);
        msg.setData(bundle);

        player2Handler.sendMessage(msg);
    }

    //Pick a number to guess with based on a strategy (strategy uses function "strategy" and doesn't pick a number that was deemed missing)
    public String pickNum(){
        populateArray();
        //Use strategy to remove #s that were missing
        if(!p2FT){
            strategy();
        }

        int randomIndex = new Random().nextInt(arrayListP2.size());
        int randomNum1 = arrayListP2.get(randomIndex);
        arrayListP2.remove(randomIndex);

        randomIndex = new Random().nextInt(arrayListP2.size());
        int randomNum2 = arrayListP2.get(randomIndex);
        arrayListP2.remove(randomIndex);

        randomIndex = new Random().nextInt(arrayListP2.size());
        int randomNum3 = arrayListP2.get(randomIndex);
        arrayListP2.remove(randomIndex);

        randomIndex = new Random().nextInt(arrayListP2.size());
        int randomNum4 = arrayListP2.get(randomIndex);
        arrayListP2.remove(randomIndex);

        String CombineString = String.valueOf(randomNum1) + String.valueOf(randomNum2) + String.valueOf(randomNum3) + String.valueOf(randomNum4);

        return CombineString;
    }

    //Get the player 2 guess/response
    public void guess(boolean firstTurn,String player1Guess, int missingDigit){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //If it's not the first turn of player 2, get the missing # digit for the strategy
        p2FT = firstTurn;
        if(!p2FT){
            p2MD = missingDigit;
        }

        //Get player 2 guess
        String p2Guess = pickNum();

        P2numDigitsCP = 0;
        P2numDigitsIP = 0;
        P2missedDigits.clear();

        //Loop through to compare each # by # and keep track of things to return
        for(int i = 0; i < player1Guess.length(); i++){
            missNumFlag = true;

            for(int j = 0; j < player2SecretNum.length(); j++){
                if(player1Guess.charAt(i) == player2SecretNum.charAt(j)){
                    if(i == j){
                        P2numDigitsCP++;
                        missNumFlag = false;
                    }
                    if(i != j){
                        P2numDigitsIP++;
                        missNumFlag = false;
                    }
                }
            }
            if(missNumFlag){
                P2missedDigits.add( Character.getNumericValue(player1Guess.charAt(i)) );
            }
        }

        Bundle bundle = new Bundle();
        int randomIndex; //used to pick missing digit if there is one

        //If no missing digits then return -1 to signify nothing missing
        if(P2missedDigits.size() == 0){
            bundle.putString("p2MD", "-1");
        }
        else{ //Else pick a random digit to return
            randomIndex = new Random().nextInt(P2missedDigits.size());
            bundle.putString("p2MD", String.valueOf(P2missedDigits.get(randomIndex)));
        }

        //Store info in bundle and set it for message
        bundle.putString("p2CP", String.valueOf(P2numDigitsCP));
        bundle.putString("p2IP", String.valueOf(P2numDigitsIP));
        bundle.putString("p2G", p2Guess);

        //Send message to player 2 handler
        Message msg = Message.obtain();
        msg.what = 2;
        msg.setData(bundle);
        player2Handler.sendMessage(msg);
    }
}