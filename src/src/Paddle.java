package src;

import java.awt.*;

import static java.awt.event.KeyEvent.*;
import static src.Paddle.paddleType.HORIZONTAL;
import static src.Paddle.playerType.AI;
import static src.Paddle.playerType.OTHER;
import static src.constants.LEN;
import static src.constants.WD;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class Paddle {

    private int length;
    private int width;
    private int xpos;
    private int ypos;

    public enum paddleType { HORIZONTAL, VERTICAL}; // todo use {HORIZONTAL=0,VERTICAL=1}
    public enum playerType { AI,HUMAN,OTHER};
    private paddleType type;
    private playerType ptype;
    private int xchange;
    private int ychange;
    private PingPong runningapp;

    public Paddle(PingPong app, int x, int y, paddleType type, playerType ptype){
        this.xpos = x;
        this.ypos = y;
        this.type = type;
        this.length = LEN;
        this.width = WD;
        this.runningapp = app;
        this.ptype = ptype;
        //System.out.println("Here");
    }

    public int getxpos(){
        return xpos;
    }

    public int getypos(){
        return ypos;
    }

    public void setxpos(int xpos){
        this.xpos = xpos;
    }

    public void setypos(int ypos){
        this.ypos = ypos;
    }

    public void updateLocation() {
        if(this.ptype == AI) {
            findposAI();
        }else if(this.ptype == OTHER){
            // fetch from network
        }
        locUpdate();
    }

    private void locUpdate() {
        if (this.type == HORIZONTAL) {
            if (xpos + xchange + width < runningapp.getWidth() && xpos + xchange > 0) xpos += xchange;
        } else {
            if (ypos + ychange + length< runningapp.getHeight() && ypos + ychange > 0) ypos += ychange;
        }
    }

    public void findposAI() {
        if(this.type == HORIZONTAL){
            if(runningapp.getBoard().getBall().getxpos() > xpos + WD/2){
                xchange = 1;
            } else if(runningapp.getBoard().getBall().getxpos()< xpos + WD/2){
                xchange = -1;
            }else {
                xchange = 0;
            }
        }else{
            if(runningapp.getBoard().getBall().getypos()> ypos + WD/2){
                ychange = 1;
            } else if(runningapp.getBoard().getBall().getypos()< ypos + WD/2){
                ychange = -1;
            }else{
                ychange = 0;
            }
        }
    }

    public void updateSize(){

    }

    public void draw(Graphics g){
        //System.out.println("Here");
        g.setColor(Color.BLACK);
        if(this.type == HORIZONTAL)
            g.fillRect(xpos,ypos,width,length);
        else
            g.fillRect(xpos,ypos,length,width);
    }

    public void keyrelease(int keyCode) {
        if(type == HORIZONTAL){
            if(keyCode == VK_RIGHT || keyCode == VK_LEFT)
                xchange = 0;
        }
        if(type == paddleType.VERTICAL){
            if(keyCode == VK_UP || keyCode == VK_DOWN)
                ychange = 0;
        }
    }

    public void keypress(int keyCode) {
        if (type == paddleType.VERTICAL) {
            if (keyCode == VK_UP)
                ychange = 1;
            if (keyCode == VK_DOWN)
                ychange = -1;
        } else {
            if (keyCode == VK_LEFT)
                xchange = -1;
            if (keyCode == VK_RIGHT)
                xchange = 1;
        }
    }

    public utility.pair getxCollisionBounds(int pos){
        return utility.pair.make_pair(xpos,xpos+WD);
    }

    public utility.pair getyCollisionBounds(int pos){
        return utility.pair.make_pair(ypos,ypos+WD);
    }

    public paddleType type(){
        return this.type;
    }
}
