package src;

import integration.AbstractGameUI;

import javax.swing.*;
import java.awt.*;

import static java.lang.Integer.parseInt;
import static src.constants.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class PingPong extends JFrame{
    private pongBoard Board;
    public int difficulty;

    public PingPong(String difficulty){
        switch (difficulty) {
            case "Easy":
                this.difficulty = 0;
                break;
            case "Hard":
                this.difficulty = 1;
                break;
            case "Insane":
                this.difficulty = 2;
                break;
        }
        renderDisplay();
    }

    public pongBoard getBoard(){
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
        Board = new src.pongBoard(this,testactive,testcomp);//,testother);
        Board.setOnInternalPaddleMoveListener(new AbstractGameUI.PaddleMoveListener() {
            @Override
            public void handlePaddleMove(int id, int delX, int delY) {
                System.out.println(String.format("id =%d, delx =%d, dely = %d",id,delX,delY));
                if (id == 0) {
                    Board.movePaddle(2, -delX, delY);
//                    Board.movePaddle(2,-delX,-delY);
                }
                if (id == 1) {
                    Board.movePaddle(1,-delX,-delY);
                }
            }
        });
//        for(int i=0;i<1000;++i){
//            Board.movePaddle(0,1,0);
//        }
        add(Board);
    }
    public static void main(String args[]){
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                PingPong app = new PingPong(args[0]);
                app.setVisible(true);
            }
        });
    }
}