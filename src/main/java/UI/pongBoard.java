package UI;

import integration.AbstractGameUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static UI.Paddle.playerType.AI;
import static UI.Paddle.playerType.HUMAN;
import static UI.Paddle.playerType.OTHER;
import static UI.constants.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class PongBoard extends JPanel implements ActionListener, KeyListener, AbstractGameUI {

    private PingPong runningApp;
    private Ball ball;
    private Paddle[] Players;
    private int activePlayer;
    private Dashboard dashboard;
    //private int otherPlayers[];
    private ArrayList<Integer> computerPlayers;
    private ArrayList<Integer> otherPlayers;
    private ArrayList<Integer> keyboardPlayers;
    //private  int computerPlayers[];
    //private int otherPlayers[];
    private int speed;//=INIT_SPEED;
    private BufferedImage img;
    private PaddleMoveListener paddleMoveListener;

    private Ball.BallVelocity ballVelocity;

    public Paddle[] getPlayers(){
        return this.Players;
    }

    public Ball getBall(){
        return this.ball;
    }

    public PongBoard(PingPong app, int activePlayer, int[] computerPlayers, Ball.BallVelocity velocity){//, int[] otherPlayers)
        this.runningApp = app;
        Players = new Paddle[MAXPLAYERS];
        //this.activePlayer = activePlayer;
        this.speed = INIT_SPEED[runningApp.difficulty];
        //this.otherPlayers = otherPlayers;
        //this.computerPlayers = computerPlayers;
        try {
            img = ImageIO.read(new File(IMAGES_PATH+"2"+".JPG"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.ballVelocity = velocity;
        this.computerPlayers = new ArrayList<>();
        this.otherPlayers = new ArrayList<>();
        this.keyboardPlayers = new ArrayList<>();
    }

    public void startpongBoard(PingPong app){
        Timer timer = new Timer(speed, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);
        setBoard(app);
        this.dashboard = new Dashboard(4,app);
    }

    public Dashboard getDashboard() {
        return this.dashboard;
    }

    public void setBoard(PingPong app) {
        // set Ball
        ball = new Ball(app,ballVelocity);
        // set AI players
//        for(int index: computerPlayers){
//            Players[index] = new Paddle(app, xinit[index], yinit[index], Paddle.paddleType.values()[index%2], AI,index);
//        }
        // set other network players
//        for(int index: otherPlayers){
//            Players[index] = new Paddle(app, xinit[index], yinit[index], Paddle.paddleType.values()[index%2], OTHER,index);
//        }
        // set game player
        //Players[activePlayer] = new Paddle(app, xinit[activePlayer], yinit[activePlayer], Paddle.paddleType.values()[activePlayer%2], HUMAN,activePlayer);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        ball.draw(g);
        for(Paddle player: Players) {
            if(!(player.getdead())){
                player.draw(g);
            }
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

    @Override
    public boolean movePaddle(int id, int delX, int delY) {
        return (Players[id].setxpos(delX) && Players[id].setypos(delY));
    }

    @Override
    public void setOnInternalPaddleMoveListener(PaddleMoveListener paddleMoveListener) {
        this.paddleMoveListener = paddleMoveListener;
        // line
    }

    @Override
    public void setPaddleAsKeyboardControlled(int paddleId, boolean owner) {
        if(owner){
            this.activePlayer = paddleId;
            this.Players[paddleId] = new Paddle(this.runningApp, xinit[activePlayer], yinit[activePlayer], Paddle.paddleType.values()[activePlayer%2], HUMAN,activePlayer);
        }
        else{
            this.keyboardPlayers.add(paddleId);
            this.Players[paddleId] = new Paddle(this.runningApp, xinit[paddleId], yinit[paddleId], Paddle.paddleType.values()[paddleId%2], OTHER,paddleId);
        }
    }

    @Override
    public void setPaddleAsAiControlled(int paddleId) {
        this.computerPlayers.add(paddleId);
        this.Players[paddleId] = new Paddle(this.runningApp, xinit[paddleId], yinit[paddleId], Paddle.paddleType.values()[paddleId%2], AI,paddleId);
    }

    public PaddleMoveListener getPaddleMoveListener(){
        return this.paddleMoveListener;
    }
}