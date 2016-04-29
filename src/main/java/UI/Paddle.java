package UI;

import integration.AbstractGameUI;

import java.awt.*;
import java.util.Random;

import static UI.Paddle.paddleType.HORIZONTAL;
import static UI.Paddle.playerType.AI;
import static UI.Paddle.playerType.OTHER;
import static UI.Constants.*;
import static java.awt.event.KeyEvent.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class Paddle {

    private int length;
    private int width;
    private int xpos;
    private int ypos;
    private Random rn;
    private int direction;
    private boolean setOutside;
    private boolean dead;// = false;
    private AbstractGameUI.PaddleMoveListener paddleMoveListener;



    public int getxchange() {
        return xchange;
    }

    public int getychange() {
        return ychange;
    }

    public enum paddleType { HORIZONTAL, VERTICAL}; // todo use {HORIZONTAL=0,VERTICAL=1}
    public enum playerType { AI,HUMAN,OTHER};
    private paddleType type;
    private playerType ptype;
    private int xchange;
    private int ychange;
    private PingPong runningapp;
    private int tage;

    public Paddle(PingPong app, int x, int y, paddleType type, playerType ptype,int tage){
        this.xpos = x;
        this.ypos = y;
        this.type = type;
        this.length = LEN;
        this.width = WD;
        this.runningapp = app;
        this.ptype = ptype;
        rn = new Random();
        this.setOutside = false;
        //System.out.println("Here");
        direction = rn.nextInt(2);
        this.dead = false;
        this.tage = tage;
        //
    }

    public void setdead(boolean dead){
        this.dead = dead;
        deadProc();
    }

    private void deadProc() {

    }

    public boolean getdead(){return this.dead;}

    public int getxpos(){
        return xpos;
    }

    public int getypos(){
        return ypos;
    }

    public boolean setxpos(int deltaxpos){
        if(this.xpos +deltaxpos + WD < SCREEN_WIDTH && this.xpos +deltaxpos >0){
            this.xpos += deltaxpos;
            this.setOutside = true;
            return true;
        }
        return false;
    }

    public boolean setypos(int deltaypos){
        if(this.ypos + deltaypos + WD < SCREEN_HEIGHT && this.ypos+deltaypos>0){
            this.ypos += deltaypos;
            this.setOutside = true;
            return true;
        }
        return false;
    }

    public void updateLocation() {
        this.paddleMoveListener = runningapp.getBoard().getPaddleMoveListener();
        if(this.ptype == AI) {
            findposAI();
        }else if(this.ptype == OTHER){
            // fetch from network
        }
        locUpdate();
    }

    private void locUpdate() {
        if(this.setOutside==false){
            if((xchange!=0) || (ychange!=0)){
                this.paddleMoveListener.handlePaddleMove(tage,xchange,ychange);
            }
        }
        if (this.type == HORIZONTAL) {
            if (xpos + xchange + width < runningapp.getWidth() && xpos + xchange > 0) xpos += xchange;
        } else {
            if (ypos + ychange + length< runningapp.getHeight() && ypos + ychange > 0) ypos += ychange;
        }
    }

    public void findposAI() {
        this.setOutside = false;
        switch (runningapp.difficulty) {
            case 0:
                if(this.type == HORIZONTAL){
                    if(xpos  < (SCREEN_WIDTH*3)/4 && xpos+WD>=SCREEN_WIDTH/2){
                        if(direction==1) {
                            xchange=1;
                        }
                        else {
                            xchange = -1;
                        }
                    } else if(xpos +WD >=(SCREEN_WIDTH)/4  && xpos+WD<SCREEN_WIDTH/2){
                        if(direction==1){
                            xchange=1;
                        }
                        else{
                            xchange = -1;
                        }
                    }else {
                        if(xpos + WD >= (SCREEN_WIDTH*3)/4){
                            xchange=-1;
                        }else{
                            xchange=1;
                        }
                        direction = 1-direction;
                    }
                }else{
                    if(ypos < (SCREEN_HEIGHT*3)/4 && ypos+WD>=SCREEN_HEIGHT/2){
                        if(direction==1){
                            ychange=1;
                        }
                        else{
                            ychange = -1;
                        }
                    } else if(ypos + WD>=(SCREEN_HEIGHT)/4  && ypos+WD<SCREEN_HEIGHT/2){
                        if(direction==1){
                            ychange=1;
                        }
                        else{
                            ychange = -1;
                        }
                    }else {
                        if(ypos + WD >= (SCREEN_HEIGHT*3)/4){
                            ychange=-1;
                        }else{
                            ychange=1;
                        }direction = 1-direction;
                    }
                }
                break;
            case 1:
                if(this.type == HORIZONTAL){
                    if(xpos+2*WD>SCREEN_WIDTH){
                        xchange=-1;
                    }else if(xpos-WD<0) {
                        xchange = 1;
                    }else if(runningapp.getBoard().getBall().getxpos() > xpos + WD/2){
                        if(rn.nextInt(50)==1){
                            xchange = 1;
                        }
                    } else if(runningapp.getBoard().getBall().getxpos()< xpos + WD/2){
                        if(rn.nextInt(50)==1){
                            xchange = -1;
                        }
                    }else {
                        xchange = 0;
                    }
                }else{
                    if(ypos+2*WD>SCREEN_HEIGHT){
                        ychange=-1;
                    }else if(ypos-WD<0) {
                        ychange = 1;
                    }else if(runningapp.getBoard().getBall().getypos()> ypos + WD/2){
                        if(rn.nextInt(50)==1){
                            ychange = 1;
                        }
                    } else if(runningapp.getBoard().getBall().getypos()< ypos + WD/2){
                        if(rn.nextInt(50)==1){
                            ychange = -1;
                        }
                    }else{
                        ychange = 0;
                    }
                }
                break;
            case 2:
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
                break;
        }
    }

    public void updateSize(){

    }

    public void draw(Graphics g){
        //System.out.println("Here");
        g.setColor(Color.WHITE);
        if(this.type == HORIZONTAL)
            g.fillRect(xpos,ypos,width,length);
        else
            g.fillRect(xpos,ypos,length,width);
        g.setColor(Color.RED);
        if(this.xchange == 1) {
            g.fillRect(xpos+width-INDICATOR,ypos,INDICATOR,length);
        }
        if(this.xchange == -1) {
            g.fillRect(xpos, ypos, INDICATOR, length);
        }
        if(this.ychange == 1) {
            g.fillRect(xpos,ypos+width-INDICATOR,length,INDICATOR);
        }
        if(this.ychange == -1) {
            g.fillRect(xpos, ypos, length,INDICATOR);
        }
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
        this.setOutside = false;
    }

    public void keypress(int keyCode) {
        if (type == paddleType.VERTICAL) {
            if (keyCode == VK_UP)
                ychange = 1;
            if (keyCode == VK_DOWN)
                ychange = -1;
            //
        } else {
            if (keyCode == VK_LEFT)
                xchange = -1;
            if (keyCode == VK_RIGHT)
                xchange = 1;
            //
        }
        this.setOutside = false;
    }

    public Utility.pair getxCollisionBounds(int pos){
        return Utility.pair.make_pair(xpos,xpos+WD);
    }

    public Utility.pair getyCollisionBounds(int pos){
        return Utility.pair.make_pair(ypos,ypos+WD);
    }

    public paddleType type(){
        return this.type;
    }
}
