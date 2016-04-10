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
import static src.constants.INIT_SPEED;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class pongBoard extends JPanel implements ActionListener, KeyListener{

    private PingPong runningApp;
    private Ball ball;
    //private Paddle[] Players;
    private Paddle Player0;
    private Paddle Player1;
    private Paddle Player2;
    private Paddle Player3;
    private int speed=INIT_SPEED;
    public ballInfo ballinfo;
    public paddleInfo paddleinfo0;
    public paddleInfo paddleinfo1;
    public paddleInfo paddleinfo2;
    public paddleInfo paddleinfo3;


    public pongBoard(PingPong app){
        this.runningApp = app;
        setBoard(app);
        Timer timer = new Timer(speed, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);
        ballinfo = new ballInfo();
        ballinfo.setXpos(ball.getxpos());
        ballinfo.setYpos(ball.getypos());
        paddleinfo0 = new paddleInfo();
        paddleinfo1 = new paddleInfo();
        paddleinfo2 = new paddleInfo();
        paddleinfo3 = new paddleInfo();
        paddleinfo1.setXpos(Player1.getxpos());
        paddleinfo1.setYpos(Player1.getypos());
        paddleinfo2.setXpos(Player2.getxpos());
        paddleinfo2.setYpos(Player2.getypos());
        paddleinfo3.setXpos(Player3.getxpos());
        paddleinfo3.setYpos(Player3.getypos());
    }

    public void setBoard(PingPong app) {
        ball = new Ball(app);
        //Players[0] = new Paddle(app,200,10, Paddle.paddleType.HORIZONTAL);
        //Players[1] = new Paddle(app,390,200, Paddle.paddleType.VERTICAL);
        //Players[2] = new Paddle(app,200,390, Paddle.paddleType.HORIZONTAL);
        //Players[3] = new Paddle(app,10,200, Paddle.paddleType.VERTICAL);
        Player0 = new Paddle(app,200,5, HORIZONTAL, HUMAN);
        Player1 = new Paddle(app,390,182, VERTICAL, AI);
        Player2 = new Paddle(app,200,370, HORIZONTAL, AI);
        Player3 = new Paddle(app,5,182, VERTICAL, AI);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        ball.draw(g);
//        for(Paddle player: Players) {
//            player.draw(g);
//        }
        Player0.draw(g);
        Player1.draw(g);
        Player2.draw(g);
        Player3.draw(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateBoard();
        repaint();
    }

    private void updateBoard() {
        ball.updateLocation();
        ballinfo.setXpos(ball.getxpos());
        ballinfo.setYpos(ball.getypos());
        Player0.updateLocation();
        Player1.updateLocation();
        paddleinfo1.setXpos(Player1.getxpos());
        paddleinfo1.setYpos(Player1.getypos());
        Player2.updateLocation();
        paddleinfo2.setXpos(Player2.getxpos());
        paddleinfo2.setYpos(Player2.getypos());
        Player3.updateLocation();
        paddleinfo3.setXpos(Player3.getxpos());
        paddleinfo3.setYpos(Player3.getypos());
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Player0.keypress(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Player0.keyrelease(e.getKeyCode());
    }
}