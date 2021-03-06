package edu.apsu.csci.Assignment3_4020.classes;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import edu.apsu.csci.Assignment3_4020.R;
import edu.apsu.csci.Assignment3_4020.db.DbDataSource;

public abstract class GameLogic extends AppCompatActivity {
    //for db
    protected DbDataSource dataSource;

    protected GameBoard board;
    protected int simonMove = 0;

    protected boolean userPlaying;
    protected int userMove = 0;

    private Alert alert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_board);

        dataSource = new DbDataSource(this);

        board = new GameBoard(this) {
            @Override
            public void onPush(View v) {
                makeMove(v);
            }
        };

        board.initBoard();
        board.enableBoard(false);

        startGame();
    }

    @Override
    public void onStart() {
        super.onStart();
        board.loadSoundPool();
    }

    @Override
    public void onStop() {
        super.onStop();
        board.releaseSoundPool();
    }

    public void startGame() {
        alert = new Alert(this);
        alert.setPositiveButton("Play!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initializeSimon();
                            playSimon();
                        }
                    }, 1000);
                }
            }
        });

        alert.showInstructions();
    }

    public void endGame(int whichGame, int score) {
        alert = new Alert(this);
        alert.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    initializeSimon();
                                }
                            }, 1000);
                        }
                    }, 1000);
                }
            }
        });

        alert.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == DialogInterface.BUTTON_NEGATIVE) {
                    finish();
                }
            }
        });


        //get certain game highscores
        DbDataSource dataSource = new DbDataSource(this);

        // Pass DB scores here
        alert.showScores(score, dataSource.getHighscore(whichGame));
    }

    protected void initializeSimon() {
        board.initMoves();
        incrementSimon();
    }

    public void incrementSimon() {
        board.addMove(board.getBoardAt((int) (Math.random() * board.size())));
        playSimon();
    }

    protected void playSimon() {
        board.enableBoard(false);

        board.setIndicator("" + (simonMove + 1));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                board.getMove(simonMove).on();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        board.getMove(simonMove).off();

                        simonMove++;
                        if(simonMove < board.sizeOfMoves()) {
                            playSimon();
                        } else {
                            board.setIndicator("?");
                            board.enableBoard(true);

                            simonMove = 0;
                            userPlaying = true;
                        }
                    }
                }, 300);
            }
        }, 750);
    }

    protected abstract void makeMove(View v);
}
