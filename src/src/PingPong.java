package src;

import javax.swing.*;
import java.awt.*;

import static src.constants.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class PingPong extends JFrame{
    private pongBoard Board;

    public PingPong(){
        renderDisplay();
    }

    public pongBoard getBoard(){
        return this.Board;
    }

    private void renderDisplay(){
        setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        setResizable(true);
        setTitle("Ping Pong");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setBackground(Color.BLACK);
        Container c = this.getContentPane();
        c.setBackground(Color.BLACK);
        // center window on screen
        setLocationRelativeTo(null);
        Board = new src.pongBoard(this,testactive,testcomp);//,testother);
        add(Board);
    }
    public static void main(String args[]){
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                PingPong app = new PingPong();
                app.setVisible(true);
            }
        });
    }
}