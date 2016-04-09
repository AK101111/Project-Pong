package src;

import java.awt.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class Ball {
    private int xpos;
    private int ypos;
    private int xspeed;
    private int yspeed;
    private int radius=5;
    private PingPong runningapp;

    public Ball(PingPong app){
        this.runningapp = app;
        xpos = 200;
        ypos = 200;
        // put ball in center
    }

    public void updateLocation(){

    }

    public void updateSpeed(){

    }

    public boolean paddleHit(){
        return false;
    }

    public boolean wallHit(){
        return false;
    }

    public void draw(Graphics g){
        g.setColor(Color.ORANGE);
        g.fillOval(xpos,ypos,radius,radius);
    }
}
