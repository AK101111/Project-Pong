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
    private int length=LEN;
    private int width=WD;
    private int xpos;
    private int ypos;
    private int score;
    private boolean setOutside;
    private AbstractGameUI.PaddleMoveListener paddleMoveListener;
    private int probdec;
    private boolean doupdate;
    public int id;


    public void scoreDec() {
        if(this.score>0)
            score--;
    }
    
    public int getScore(){
        return this.score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setDead() {
        this.score=0;
    }

    public enum paddleType { HORIZONTAL, VERTICAL}; // todo use {HORIZONTAL=0,VERTICAL=1}
    public enum playerType { AI,HUMAN,OTHER};
    private paddleType type;
    private playerType ptype;
    private int xchange;
    private int ychange;
    private PingPong runningapp;
    private Random rn;


    public playerType getPlayerType(){
        return this.ptype;
    }

    public Paddle(PingPong app, int x, int y, paddleType type, playerType ptype,int id, long seeder){
        this.xpos = x;
        this.ypos = y;
        this.type = type;
        this.runningapp = app;
        this.ptype = ptype;
        this.setOutside = false;
        this.id = id;
        this.doupdate = true;
        this.paddleMoveListener = runningapp.getBoard().getPaddleMoveListener();
        this.score = 3;
        rn = new Random();
        rn.setSeed(seeder);
    }

    public boolean getDead(){return (this.score==0);}

    public boolean setxpos(int deltaxpos){
        if(this.doupdate) {
            if ((this.xpos + deltaxpos + width <= runningapp.getWidth()) && (this.xpos + deltaxpos >= 0)) {
                this.xpos += deltaxpos;
                this.setOutside = true;
                return true;
            }
        }
        return false;
    }

    public boolean setypos(int deltaypos){
        if(this.doupdate){
            if((this.ypos + deltaypos + width <= runningapp.getWorkingSize()) && (this.ypos+deltaypos>=0)){
                this.ypos += deltaypos;
                this.setOutside = true;
                return true;
            }
        }
        return false;
    }

    public void updateLocation() {
        if(this.ptype == AI) {
            findposAI();
        }
        locUpdate();
    }

    private void locUpdate() {
        if (this.setOutside == false) {
            if ((xchange != 0) || (ychange != 0)) {
                this.paddleMoveListener.handlePaddleMove(id, xchange, ychange);
            }
        }
        if (this.doupdate) {
            if (this.type == HORIZONTAL) {
                if ((xpos + xchange + width <= runningapp.getWidth()) && (xpos + xchange >= 0)) xpos += xchange;
            } else {
                if ((ypos + ychange + width <= runningapp.getWorkingSize()) && (ypos + ychange >= 0)) ypos += ychange;
            }
        }
    }

    public void findposAI() {
        this.setOutside = false;
        switch (runningapp.difficulty) {
            case 0:
                probdec = rn.nextInt(100);
                if(probdec<50){
                    this.doupdate = true;
                    if(this.type == HORIZONTAL){
                        if(runningapp.getBoard().getBall().getXpos() > xpos + WD/2){
                            xchange = 1;
                        } else if(runningapp.getBoard().getBall().getXpos()< xpos + WD/2){
                            xchange = -1;
                        }else {
                            xchange = 0;
                        }
                    }else{
                        if(runningapp.getBoard().getBall().getYpos()> ypos + WD/2){
                            ychange = 1;
                        } else if(runningapp.getBoard().getBall().getYpos()< ypos + WD/2){
                            ychange = -1;
                        }else{
                            ychange = 0;
                        }
                    }
                }else{
                    this.doupdate = false;
                }
                break;
            case 1:
                probdec = rn.nextInt(100);
                if(probdec<80){
                    this.doupdate = true;
                    if(this.type == HORIZONTAL){
                        if(runningapp.getBoard().getBall().getXpos() > xpos + width/2){
                            xchange = 1;
                        } else if(runningapp.getBoard().getBall().getXpos()< xpos + width/2){
                            xchange = -1;
                        }else {
                            xchange = 0;
                        }
                    }else{
                        if(runningapp.getBoard().getBall().getYpos()> ypos + width/2){
                            ychange = 1;
                        } else if(runningapp.getBoard().getBall().getYpos()< ypos + width/2){
                            ychange = -1;
                        }else{
                            ychange = 0;
                        }
                    }
                }else{
                    this.doupdate = false;
                }
                break;
            case 2:
                if(this.type == HORIZONTAL){
                    if(runningapp.getBoard().getBall().getXpos() > xpos + width/2){
                        xchange = 1;
                    } else if(runningapp.getBoard().getBall().getXpos()< xpos + width/2){
                        xchange = -1;
                    }else {
                        xchange = 0;
                    }
                }else{
                    if(runningapp.getBoard().getBall().getYpos()> ypos + width/2){
                        ychange = 1;
                    } else if(runningapp.getBoard().getBall().getYpos()< ypos + width/2){
                        ychange = -1;
                    }else{
                        ychange = 0;
                    }
                }
                break;
            default:
                break;
        }
    }

    public void updateSize(){

    }

    public void draw(Graphics g){
        g.drawString(String.valueOf(score), scorewidth[id],scoreheight[id]);
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
                ychange = -1;
            if (keyCode == VK_DOWN)
                ychange = 1;
        } else {
            if (keyCode == VK_LEFT)
                xchange = -1;
            if (keyCode == VK_RIGHT)
                xchange = 1;
        }
        this.setOutside = false;
    }

    public boolean paddleCollide(float ballxpos, float ballypos, float xvel, float yvel,int radius) {
        switch (id){
            case 0:
                if(ballxpos+(2*radius)>=xpos && ballxpos<=xpos+width && ((int)ballypos==ypos-1 || (int)ballypos==ypos) && !(getDead()) && (yvel<0))
                    return true;
                break;
            case 1:
                if(ballypos+(2*radius)>=ypos && ballypos<=ypos+width && ((int)(ballxpos+(2*radius))==xpos-1 || (int)(ballxpos+(2*radius))==xpos) && !(getDead()) && (xvel>0))
                    return true;
                break;
            case 2:
                if(ballxpos+(2*radius)>=xpos && ballxpos<=xpos+width && ((int)(ballypos+(2*radius))==ypos-1 || (int)(ballypos+(2*radius))==ypos) && !(getDead()) && (yvel>0))
                    return true;
                break;
            case 3:
                if(ballypos+(2*radius)>=ypos && ballypos<=ypos+width && ((int)ballxpos==xpos+length || (int)ballxpos==xpos+length+1) && !(getDead()) && (xvel<0))
                    return true;
                break;
            default:
                return false;
        }
        return false;
    }
}
