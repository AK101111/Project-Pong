package main.java.UI;

import main.java.integration.AbstractGameUI;

import javax.swing.*;
import java.awt.*;

import static java.lang.Integer.parseInt;
import static main.java.UI.Constants.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class PingPong extends JFrame{
    private PongBoard Board;
    public int difficulty;
    Ball.BallVelocity ballVelocity;

    public PingPong(int difficulty, Ball.BallVelocity velocity){
        this.difficulty = difficulty;
        this.ballVelocity = velocity;
        renderDisplay();
    }

    public PongBoard getBoard(){
        return this.Board;
    }

    private void renderDisplay(){
        setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        setResizable(false);
        //setTitle("Ping Pong");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setBackground(Color.BLACK);
        // center window on screen
        setLocationRelativeTo(null);
        // testcomp and testactive are to be set
        Board = new PongBoard(this,testactive,testcomp,ballVelocity);//,testother);
        Board.setOnInternalPaddleMoveListener(new AbstractGameUI.PaddleMoveListener() {
            @Override
            public void handlePaddleMove(int id, int delX, int delY) {
               // System.out.println(String.format("id =%d, delx =%d, dely = %d",id,delX,delY));
//                if (id == 0) {
//                    Board.movePaddle(2, -delX, delY);
////                    Board.movePaddle(2,-delX,-delY);
//                }
//                if (id == 1) {
//                    Board.movePaddle(1,-delX,-delY);
//                }
            }
        });
//        for(int i=0;i<1000;++i){
//            Board.movePaddle(0,1,0);
//        }
        Board.setPaddleAsKeyboardControlled(0,false);
        Board.setPaddleAsKeyboardControlled(1,false);
        Board.setPaddleAsKeyboardControlled(2,true);
        Board.setPaddleAsKeyboardControlled(3,false);

//        Board.setPaddleAsAiControlled(1);
//        Board.setPaddleAsAiControlled(2);
//        Board.setPaddleAsAiControlled(3);
        Board.startpongBoard(this);
        add(Board);
    }



    public static void startGame(int difficulty, Ball.BallVelocity velocity){
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                PingPong app = new PingPong(difficulty, velocity);
                app.setVisible(true);
            }
        });
    }
}