package com.example.dariandanciu_project4;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

    int PlayerTurn = 1;

    int NumberTurnsTakenP1 = 0;
    int NumberTurnsTakenP2 = 0;

    Boolean newGame = true;

    String SNP1;
    String SNP2;

    Button StartGame;

    TextView p1SN;
    TextView p2SN;

    boolean firstTurnP1 = true;
    boolean firstTurnP2 = true;

    LinearLayout p1LL;
    LinearLayout p2LL;

    Player1 p1;
    Player2 p2;

    String player1Guess = "0000";
    String P1numDigitsCP;
    String P1numDigitsIP;
    String P1numDigitsMissing;

    String player2Guess = "0000";
    String P2numDigitsCP;
    String P2numDigitsIP;
    String P2numDigitsMissing;

    public Handler UIHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            int what = msg.what ;
            switch (what) {
                case 1: //P1, display secret #
                    p1LL.removeAllViews();
                    SNP1 = msg.getData().getString("SC");
                    p1SN.setText("Player 1's #: " + SNP1);
                    break;
                case 2: //P2, display secret #
                    p2LL.removeAllViews();
                    SNP2 = msg.getData().getString("SC");
                    p2SN.setText("Player 2's #: " + SNP2);
                    break;
                case 3: // Get guess/response from player 1
                    player1Guess = msg.getData().getString("p1G");
                    P1numDigitsCP = msg.getData().getString("p1CP");
                    P1numDigitsIP = msg.getData().getString("p1IP");
                    P1numDigitsMissing = msg.getData().getString("p1MD");
                    TextView TVp1 = new TextView(MainActivity.this);

                    if(Integer.parseInt(P1numDigitsCP) == 4){ //Player 2 has won, guessed all #s in correct spots
                        TVp1.setText("Player 2 Won!");
                        TextView TVp2 = new TextView(MainActivity.this);
                        TVp2.setText("Player 2 Won!");
                        p1LL.addView(TVp1);
                        p2LL.addView(TVp2);
                    }
                    else if(NumberTurnsTakenP1!=21){ //Display guess/response as it is not the 20th turn yet
                        TVp1.setText("Turn #: " + NumberTurnsTakenP1 + '\n' + "Player 1 Guess: " + player1Guess + "\n" + "Response: \n" + "Correct Place #: " + P1numDigitsCP + "\n" + "Incorrect Place #: " + P1numDigitsIP + "\n"+ "Missing Number: " + P1numDigitsMissing + "\n");
                        p1LL.addView(TVp1);
                        runGame();
                    }
                    else{ //20th turn reached
                        TVp1.setText("20th turn reached");
                        p1LL.addView(TVp1);
                        p1.player1Handler.getLooper().quitSafely();
                        runGame();
                    }
                    break;
                case 4: // Get guess/response from player 2

                    player2Guess = msg.getData().getString("p2G");
                    P2numDigitsCP = msg.getData().getString("p2CP");
                    P2numDigitsIP = msg.getData().getString("p2IP");
                    P2numDigitsMissing = msg.getData().getString("p2MD");
                    TextView TVp2 = new TextView(MainActivity.this);

                    if(Integer.parseInt(P2numDigitsCP) == 4){ //Player 1 has won, guessed all #s in correct spots
                        TVp2.setText("Player 1 Won!");
                        TextView TVp1_ = new TextView(MainActivity.this);
                        TVp1_.setText("Player 1 Won!");
                        p1LL.addView(TVp1_);
                        p2LL.addView(TVp2);
                    }
                    if(NumberTurnsTakenP2!=21){ //Display guess/response as it is not the 20th turn yet
                        TVp2.setText("Turn #: " + NumberTurnsTakenP2 + '\n' + "Player 2 Guess: " + player2Guess + "\n" + "Response: \n" + "Correct Place #: " + P2numDigitsCP + "\n" + "Incorrect Place #: " + P2numDigitsIP + "\n"+ "Missing Number: " + P2numDigitsMissing + "\n");
                        p2LL.addView(TVp2);
                        runGame();
                    }
                    else{ //20th turn reached
                        TVp2.setText("20th turn reached");
                        p2LL.addView(TVp2);
                        p2.player2Handler.getLooper().quitSafely();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartGame = findViewById(R.id.StartGame);

        p1SN = findViewById(R.id.Player1SecretNumber);
        p2SN = findViewById(R.id.Player2SecretNumber);

        p1LL = findViewById(R.id.LLP1);
        p2LL = findViewById(R.id.LLP2);

        StartGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                UIHandler.removeCallbacksAndMessages(null);

                //Stop threads for when button gets clicked again
                if(p1 != null && p2 != null){
                    if(p1.isAlive() && p2.isAlive()) {
                        p1.player1Handler.getLooper().quitSafely();
                        p2.player2Handler.getLooper().quitSafely();
                    }
                }

                //Clear linearlayout
                p1LL.removeAllViews();
                p2LL.removeAllViews();

                //Create new player threads
                p1 = new Player1(UIHandler);
                p2 = new Player2(UIHandler);

                //Start threads
                while(!p1.isAlive()){
                    p1.start();
                }
                while(!p2.isAlive()){
                    p2.start();
                }

                try {
                    p1.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                newGame = true;
                firstTurnP1 = true;
                firstTurnP2 = true;
                NumberTurnsTakenP1 = 0;
                NumberTurnsTakenP2 = 0;
                PlayerTurn = 1;
                runGame();
            }
        });

    }

    //Running base game logic
    public void runGame() {
        if (newGame) { //Setup game and choose secret number
            p1.pickSecretNum();
            p2.pickSecretNum();
            newGame = false;
        }

        //Player 1 turn
        if (PlayerTurn == 1) {
            //Increment/change turn and then run function to get guess/response
            NumberTurnsTakenP1++;
            PlayerTurn = 2;
            p1.player1Handler.post(new Runnable() {
                @Override
                public void run() {
                    p1.guess(firstTurnP1, player2Guess);
                    firstTurnP1 = false;
                }
            });
        }
        else { //Player 2 turn
            //Increment/change turn and then run function to get guess/response
            PlayerTurn = 1;
            NumberTurnsTakenP2++;
            p2.player2Handler.post(new Runnable() {
                @Override
                public void run() {
                    p2.guess(firstTurnP2, player1Guess, Integer.parseInt(P1numDigitsMissing) );
                    firstTurnP2 = false;
                }
            });
        }
    }
}