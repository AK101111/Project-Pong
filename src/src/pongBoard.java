package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static src.Paddle.paddleType.HORIZONTAL;
import static src.Paddle.paddleType.VERTICAL;
import static src.Paddle.playerType.AI;
import static src.Paddle.playerType.HUMAN;
import static src.Paddle.playerType.OTHER;
import static src.constants.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class pongBoard extends JPanel implements ActionListener, KeyListener{

    private PingPong runningApp;
    private Ball ball;
    private Paddle[] Players;
    private int activePlayer;
    private Dashboard dashboard;
    //private int otherPlayers[];
    private int computerPlayers[];
    private int speed;//=INIT_SPEED;

    public Paddle[] getPlayers(){
        return this.Players;
    }

    public Ball getBall(){
        return this.ball;
    }

    public pongBoard(PingPong app, int activePlayer, int[] computerPlayers){//, int[] otherPlayers)
        this.runningApp = app;
        Players = new Paddle[MAXPLAYERS];
        this.activePlayer = activePlayer;
        this.dashboard = new Dashboard(4);
        this.speed = INIT_SPEED[runningApp.difficulty];
        //this.otherPlayers = otherPlayers;
        this.computerPlayers = computerPlayers;
        setBoard(app);
        Timer timer = new Timer(speed, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);
    }

    public Dashboard getDashboard() {
        return this.dashboard;
    }

    public void setBoard(PingPong app) {
        // set Ball
        ball = new Ball(app);
        // set AI players
        for(int index: computerPlayers){
            Players[index] = new Paddle(app, xinit[index], yinit[index], Paddle.paddleType.values()[index%2], AI);
        }
        // set other network players
//        for(int index: otherPlayers){
//            Players[index] = new Paddle(app, xinit[index], yinit[index], Paddle.paddleType.values()[index%2], OTHER);
//        }
        // set game player
        Players[activePlayer] = new Paddle(app, xinit[activePlayer], yinit[activePlayer], Paddle.paddleType.values()[activePlayer%2], HUMAN);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        ball.draw(g);
        for(Paddle player: Players) {
            player.draw(g);
        }
        dashboard.draw(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateBoard();
        repaint();
    }

    private void updateBoard() {
        ball.updateLocation();
        for(Paddle player: Players) {
            player.updateLocation();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Players[activePlayer].keypress(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Players[activePlayer].keyrelease(e.getKeyCode());
    }
}