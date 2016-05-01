package UI;

import Utils.FloatPair;
import Utils.MyVector;
import integration.AbstractGameUI;
import integration.GameState;

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
import java.util.HashMap;
import java.util.Map;

import static UI.Paddle.playerType.AI;
import static UI.Paddle.playerType.HUMAN;
import static UI.Paddle.playerType.OTHER;
import static UI.Constants.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class PongBoard extends JPanel implements ActionListener, KeyListener, AbstractGameUI {

    private PingPong runningApp;
    private Ball ball;
    private Paddle[] players;
    private int activePlayer;
    private int speed;
    private BufferedImage img;
    private PaddleMoveListener paddleMoveListener;
    private GameState gameState;

    public Paddle[] getPlayers(){
        return this.players;
    }

    public Ball getBall(){
        return this.ball;
    }

    public PongBoard(PingPong app){
        this.runningApp = app;
        players = new Paddle[MAXPLAYERS];
        this.speed = INIT_SPEED[runningApp.difficulty];
        try {
            img = ImageIO.read(new File(IMAGES_PATH+"2"+".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.gameState = new GameState();
    }

    public void startpongBoard(PingPong app){
        Timer timer = new Timer(speed, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);
        setBoard(app);
    }

    public void setBoard(PingPong app) {
        // set Ball
        ball = new Ball(app);
        gameState.setBallPosition(new FloatPair(ball.getXpos(),ball.getYpos()));
        gameState.setBallVelocity(new FloatPair(ball.getXspeed(),ball.getYspeed()));
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        ball.draw(g);
        for(Paddle player: players) {
            if(!(player.getDead())){
                player.draw(g);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        updateBoard();
    }

    private void updateBoard() {
        ball.updateLocation();
        for(Paddle player: players) {
            player.updateLocation();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        players[activePlayer].keypress(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        players[activePlayer].keyrelease(e.getKeyCode());
    }

    @Override
    public boolean movePaddle(int id, int delX, int delY) {
        if (players == null) {
            return false;
        }
        Paddle paddle = players[id];
        if (paddle == null) {
            return false;
        }
        return (paddle.setxpos(delX) & paddle.setypos(delY));
    }

    @Override
    public void setOnInternalPaddleMoveListener(PaddleMoveListener paddleMoveListener) {
        this.paddleMoveListener = paddleMoveListener;
    }

    @Override
    public void setPaddleAsKeyboardControlled(int paddleId, boolean owner) {
        if(owner){
            this.activePlayer = paddleId;
            this.players[paddleId] = new Paddle(this.runningApp,
                                                xinit[activePlayer],
                                                yinit[activePlayer],
                                                Paddle.paddleType.values()[activePlayer%2],
                                                HUMAN,activePlayer,this.runningApp.getSeeder());
        }
        else{
            this.players[paddleId] = new Paddle(this.runningApp,
                                                xinit[paddleId],
                                                yinit[paddleId],
                                                Paddle.paddleType.values()[paddleId%2],
                                                OTHER,paddleId,this.runningApp.getSeeder());
        }
    }

    @Override
    public void setPaddleAsAiControlled(int paddleId) {
        this.players[paddleId] = new Paddle(this.runningApp,
                                            xinit[paddleId],
                                            yinit[paddleId],
                                            Paddle.paddleType.values()[paddleId%2],
                                            AI,paddleId,this.runningApp.getSeeder());
    }

    @Override
    public GameState getGameState() {
        gameState.setBallPosition(new FloatPair(ball.getXpos(),ball.getYpos()));
        gameState.setBallVelocity(new FloatPair(ball.getXspeed(),ball.getYspeed()));
        //TODO
        Map<Integer,Integer>temp = new HashMap<>();
        for(int i=0;i<players.length;++i){
            temp.put(i,players[i].getScore());
        }
        gameState.setScores(temp);
        return gameState;
    }

    @Override
    public void setGameState(GameState gameState) {
        try {
            this.gameState = gameState;
            this.ball.setXpos(gameState.getBallPosition().x);
            this.ball.setYpos(gameState.getBallPosition().y);
            this.ball.setXvel(gameState.getBallVelocity().x);
            this.ball.setYvel(gameState.getBallVelocity().y);
            //TODO
            Map<Integer,Integer> tempscores = gameState.getScores();
            for(Integer key : tempscores.keySet()){
                players[key].setScore(tempscores.get(key));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePaddleFromScreen(int paddleId) {
        players[paddleId].setDead();
    }


    public PaddleMoveListener getPaddleMoveListener(){
        return this.paddleMoveListener;
    }
}