package com.example.dariandanciu_project4;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.Random;

public class Player1 extends Thread{
    private Handler UIHandler;

    public Handler player1Handler;

    ArrayList<Integer> arrayListP1 = new ArrayList<Integer>();

    String player1SecretNum;

    int P1numDigitsCP = 0;
    int P1numDigitsIP = 0;

    ArrayList<Integer> P1missedDigits = new ArrayList<Integer>();

    boolean missNumFlag = true;

    Player1(Handler UIHandlerPassed){
        UIHandler = UIHandlerPassed;
    }

    @Override
    public void run() {
        Looper.prepare();

        player1Handler = new Handler(Looper.myLooper()) {
            public void handleMessage(Message msg) {
                int what = msg.what ;
                Message msg2 = Message.obtain();
                switch (what) {
                    case 1: //Start of game, display player 1 secret #
                        msg2.what = 1;
                        msg2.setData(msg.getData());
                        UIHandler.sendMessage(msg2);
                        break;
                    case 2: //Send player 1 guess/response
                        msg2.what = 3;
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

    //Repopulate the array with numbers 0-9
    public void populateArray(){
        arrayListP1.clear();
        arrayListP1.add(0,0);
        arrayListP1.add(1,1);
        arrayListP1.add(2,2);
        arrayListP1.add(3,3);
        arrayListP1.add(4,4);
        arrayListP1.add(5,5);
        arrayListP1.add(6,6);
        arrayListP1.add(7,7);
        arrayListP1.add(8,8);
        arrayListP1.add(9,9);
    }

    //Pick a 4 digit non-repeating # for the player 1's secret number
    public void pickSecretNum(){
        populateArray();

        int randomIndex = new Random().nextInt(10);
        int randomNum1 = arrayListP1.get(randomIndex);
        arrayListP1.remove(randomIndex);

        randomIndex = new Random().nextInt(9);
        int randomNum2 = arrayListP1.get(randomIndex);
        arrayListP1.remove(randomIndex);

        randomIndex = new Random().nextInt(8);
        int randomNum3 = arrayListP1.get(randomIndex);
        arrayListP1.remove(randomIndex);

        randomIndex = new Random().nextInt(7);
        int randomNum4 = arrayListP1.get(randomIndex);
        arrayListP1.remove(randomIndex);

        String CombineString = String.valueOf(randomNum1) + String.valueOf(randomNum2) + String.valueOf(randomNum3) + String.valueOf(randomNum4);
        player1SecretNum = CombineString;

        Message msg = Message.obtain();
        msg.what = 1;
        Bundle bundle = new Bundle();
        bundle.putString("SC", CombineString);
        msg.setData(bundle);

        player1Handler.sendMessage(msg);
    }

    //Pick a number to guess based on a strategy (strategy is just random #)
    public String pickNum(){
        populateArray();

        int randomIndex = new Random().nextInt(10);
        int randomNum1 = arrayListP1.get(randomIndex);
        arrayListP1.remove(randomIndex);

        randomIndex = new Random().nextInt(9);
        int randomNum2 = arrayListP1.get(randomIndex);
        arrayListP1.remove(randomIndex);

        randomIndex = new Random().nextInt(8);
        int randomNum3 = arrayListP1.get(randomIndex);
        arrayListP1.remove(randomIndex);

        randomIndex = new Random().nextInt(7);
        int randomNum4 = arrayListP1.get(randomIndex);
        arrayListP1.remove(randomIndex);

        String CombineString = String.valueOf(randomNum1) + String.valueOf(randomNum2) + String.valueOf(randomNum3) + String.valueOf(randomNum4);

        return CombineString;
    }

    //Get the player 1 guess/response
    public void guess(boolean firstTurn, String player2Guess){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Get player 1 guess
        String p1Guess = pickNum();

        P1numDigitsCP = 0;
        P1numDigitsIP = 0;
        P1missedDigits.clear();

        //Bundle to store/pass info
        Bundle bundle = new Bundle();
        bundle.putString("p1G", p1Guess);

        //First turn for p1 has no response
        if(firstTurn){
            bundle.putString("p1MD", "-1");
            bundle.putString("p1CP", "-1");
            bundle.putString("p1IP", "-1");
        }
        else{ //create response to player 2 guess
            //Loop through to compare each # by # and keep track of things to return
            for(int i = 0; i < player2Guess.length(); i++) {
                missNumFlag = true;

                for (int j = 0; j < player1SecretNum.length(); j++) {
                    if (player2Guess.charAt(i) == player1SecretNum.charAt(j)) {
                        if (i == j) {
                            P1numDigitsCP++; //Correct position #
                            missNumFlag = false;
                        }
                        if (i != j) {
                            P1numDigitsIP++; //Incorrect position #
                            missNumFlag = false;
                        }
                    }
                }

                //Check if # is missing
                if (missNumFlag) {
                    P1missedDigits.add(Character.getNumericValue(player2Guess.charAt(i)));
                }
            }
            int randomIndex;

            //If no missing digits then return -1 to signify nothing missing
            if(P1missedDigits.size() == 0){
                bundle.putString("p1MD", "-1");
            }
            else{ //Else pick a random digit to return
                randomIndex = new Random().nextInt(P1missedDigits.size());
                bundle.putString("p1MD", String.valueOf(P1missedDigits.get(randomIndex)));
            }
            //Store other info in bundle
            bundle.putString("p1CP", String.valueOf(P1numDigitsCP));
            bundle.putString("p1IP", String.valueOf(P1numDigitsIP));
        }

        //Send message to player 1 handler
        Message msg = Message.obtain();
        msg.what = 2;
        msg.setData(bundle);
        player1Handler.sendMessage(msg);
    }
}