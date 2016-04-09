package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class pongBoard extends JPanel implements ActionListener, KeyListener{
    private static final int MAXPLAYERS = 4;
    private PingPong runningApp;
    private Ball ball;
    private Paddle[] Players;

    public pongBoard(PingPong app){
        this.runningApp = app;
        setBoard(app);
    }

    public void setBoard(PingPong app) {
        ball = new Ball(app);
        Players[0] = new Paddle(app,200,10, Paddle.paddleType.HORIZONTAL);
        Players[1] = new Paddle(app,390,200, Paddle.paddleType.VERTICAL);
        Players[2] = new Paddle(app,200,390, Paddle.paddleType.HORIZONTAL);
        Players[3] = new Paddle(app,10,200, Paddle.paddleType.VERTICAL);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        ball.draw(g);
        for(Paddle player: Players) {
            player.draw(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
