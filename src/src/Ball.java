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
        xspeed = 1;
        yspeed = 1;
        // put ball in center
    }

    public void updateLocation(){
        switch (paddleHit()) {
            case 0:
                yspeed = -yspeed;
                // Player0 update
                break;
            case 1:
                xspeed = -xspeed;
                // Player1 update
                break;
            case 2:
                yspeed = -yspeed;
                // Player2 update
                break;
            case 3:
                xspeed = -xspeed;
                // Player3 update
                break;
            default:
                break;
        }
        switch (wallHit()) {
            case 0:
                yspeed = -yspeed;
                // Player0 update
                break;
            case 1:
                xspeed = -xspeed;
                // Player1 update
                break;
            case 2:
                yspeed = -yspeed;
                // Player2 update
                break;
            case 3:
                xspeed = -xspeed;
                // Player3 update
                break;
            default:
                break;
        }
        xpos += xspeed;
        ypos += yspeed;
    }

    public void updateSpeed(){

    }

    public int paddleHit(){

        return -1;
    }

    public int wallHit(){
        if(ypos == 0 ) return 0;
        if(xpos == runningapp.getWidth()) return 1;
        if(ypos == runningapp.getHeight() - radius ) return 2;
        if(xpos == radius) return 3;
        return -1;
    }

    public void draw(Graphics g){
        g.setColor(Color.ORANGE);
        g.fillOval(xpos,ypos,radius,radius);
    }
}