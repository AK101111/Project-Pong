package UI;

import java.awt.*;

import static UI.Constants.*;


/**
 * Created by arnavkansal on 09/04/16.
 */
public class Ball {
    private float xpos;
    private float ypos;
    private int radius=RADIUS;
    private PingPong runningapp;
    private float xvel;
    private float yvel;

    public Ball(PingPong app){
        this.runningapp = app;
        xpos = app.getWidth()/2;
        ypos = app.getWorkingSize()/2;
        double angle = 2*Math.PI*Math.random();
        xvel = (float)Math.cos(angle)*SPEED_MAGNITUDE;
        yvel = (float)Math.sin(angle)*SPEED_MAGNITUDE;
    }

    public float getXpos(){
        return this.xpos;
    }

    public float getYpos(){
        return this.ypos;
    }

    public float getXspeed(){
        return this.xvel;
    }

    public float getYspeed(){
        return this.yvel;
    }

    public void setXpos(float xpos){
        this.xpos = xpos;
    }

    public void setYpos(float ypos){
        this.ypos = ypos;
    }

    public void setYvel(float yvel){
        this.yvel = yvel;
    }

    public void setXvel(float xvel){
        this.xvel = xvel;
    }


    public void updateLocation(){
        switch (paddleHit()) {
            case 0:
                yvel *= -1;
                break;
            case 1:
                xvel *= -1;
                break;
            case 2:
                yvel *= -1;
                break;
            case 3:
                xvel *= -1;
                break;
            default:
                break;
        }
        switch (wallHit()) {
            case 0:
                yvel *= -1;
                break;
            case 1:
                xvel *= -1;
                break;
            case 2:
                yvel *= -1;
                break;
            case 3:
                xvel *= -1;
                break;
            default:
                break;
        }
        xpos += xvel;
        ypos += yvel;
        double angletemp = 2*Math.PI*Math.random();
//        if(xpos<0 && xvel<=0){
//            xvel = (float)Math.abs(Math.cos(angletemp)*SPEED_MAGNITUDE);
//            yvel = (float)Math.abs(Math.sin(angletemp)*SPEED_MAGNITUDE);
//        }
//        if(xpos>runningapp.getWidth() && xvel>=0){
//            xvel = -(float)Math.abs(Math.cos(angletemp)*SPEED_MAGNITUDE);
//            yvel = (float)Math.abs(Math.sin(angletemp)*SPEED_MAGNITUDE);
//        }
//        if(ypos<0 && yvel<=0){
//            xvel = (float)Math.abs(Math.cos(angletemp)*SPEED_MAGNITUDE);
//            yvel = (float)Math.abs(Math.sin(angletemp)*SPEED_MAGNITUDE);
//        }
//        if(ypos>runningapp.getWorkingSize() && yvel>=0){
//            xvel = (float)Math.abs(Math.cos(angletemp)*SPEED_MAGNITUDE);
//            yvel = -(float)Math.abs(Math.sin(angletemp)*SPEED_MAGNITUDE);
//        }
    }

    public int paddleHit(){
        Paddle[] Players = runningapp.getBoard().getPlayers();
        for(int i=0; i< Players.length; ++i) {
            if (Players[i].paddleCollide(xpos,ypos,xvel,yvel,radius)) {
                return i;
            }
        }
        return -1;
    }

    public int wallHit(){
        if(ypos <= 0 ) {
            runningapp.getBoard().getPlayers()[0].scoreDec();
            return 0;
        }
        if((xpos + (2*radius)) >= runningapp.getWidth()){
            runningapp.getBoard().getPlayers()[1].scoreDec();
            return 1;
        }
        if((ypos + (2*radius)) >= runningapp.getWorkingSize()) {
            runningapp.getBoard().getPlayers()[2].scoreDec();
            return 2;
        }
        if(xpos <= 0){
            runningapp.getBoard().getPlayers()[3].scoreDec();
            return 3;
        }
        return -1;
    }

    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.fillOval((int)xpos,(int)ypos,2*radius,2*radius);
    }
}