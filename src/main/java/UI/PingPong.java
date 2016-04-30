package UI;

import integration.AbstractGameUI;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

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
    Map<Integer,String> peersList;
    boolean[] isPaddleTypeSet = {false,false,false,false};

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

        setPaddleTypes();

        Board.startpongBoard(this);
        add(Board);
    }

    private void setPaddleTypes(){
        //setting my paddle type
        Board.setPaddleAsKeyboardControlled(myName,true);
        isPaddleTypeSet[myName] =true;

        //setting all other available player's paddle type
        for(int key : peersList.keySet()){
            if(key!=myName) {
                Board.setPaddleAsKeyboardControlled(key, false);
                isPaddleTypeSet[key] = true;
            }
        }

        //setting remaining paddle's type to AI
        for(int i = 0; i < 4; i++){
            if(!isPaddleTypeSet[i])
                Board.setPaddleAsAiControlled(i);
        }
    }

    public void movePaddle(int id, int delX, int delY){
        Board.movePaddle(id,delX,delY);
    }

    public void movePaddleAbsolute(int id, int x, int y){
        Board.movePaddleAbsolute(id,x,y);
    }


    public static PingPong startGame(int difficulty, Ball.BallVelocity velocity, int myName, AbstractGameUI.PaddleMoveListener paddleMoveListener, Map<Integer,String> peersList){
        PingPong app = new PingPong(difficulty, velocity);
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                app.myName = myName;
                app.paddleMoveListener = paddleMoveListener;
                app.peersList = peersList;
                app.renderDisplay();
                app.setVisible(true);
            }
        });
        return app;
    }
}