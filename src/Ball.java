import javax.swing.*;
import java.awt.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class Ball {
    private int xpos;
    private int ypos;
    private int xspeed;
    private int yspeed;
    private int radius;
    private PingPong runningapp;

    public Ball(PingPong app){
        this.runningapp = app;
        // put ball in center
    }

    public void updateLocation(){

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
