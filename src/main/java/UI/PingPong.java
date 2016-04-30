package UI;

import integration.AbstractGameUI;

import javax.swing.*;
import java.awt.*;

import static java.lang.Integer.parseInt;
import static UI.Constants.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class PingPong extends JFrame{
    private PongBoard Board;
    public int difficulty;
    Ball.BallVelocity ballVelocity;
    int myName;
    AbstractGameUI.PaddleMoveListener paddleMoveListener;

    public PingPong(int difficulty, Ball.BallVelocity velocity){
        this.difficulty = difficulty;
        this.ballVelocity = velocity;
//        renderDisplay();
    }
    public PingPong(){

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
        Board.setOnInternalPaddleMoveListener(paddleMoveListener);

        for(int i = 0; i < 4; i++){
            Board.setPaddleAsKeyboardControlled(i,(i==myName));
        }

        Board.startpongBoard(this);
        add(Board);
    }

    public void movePaddle(int id, int delX, int delY){
        Board.movePaddle(id,delX,delY);
    }



    public static PingPong startGame(int difficulty, Ball.BallVelocity velocity, int myName, AbstractGameUI.PaddleMoveListener paddleMoveListener){
        PingPong app = new PingPong(difficulty, velocity);
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                app.myName = myName;
                app.paddleMoveListener = paddleMoveListener;
                app.renderDisplay();
                app.setVisible(true);
            }
        });
        return app;
    }
}