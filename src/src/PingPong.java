package src;

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
        Board = new src.pongBoard(this,testactive,testcomp);//,testother);
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