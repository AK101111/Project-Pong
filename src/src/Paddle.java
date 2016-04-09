package src;

import java.awt.*;

import static java.awt.event.KeyEvent.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class Paddle {
    private static final int LEN=5,WD=50;
    private int length;
    private int width;
    private int xpos;
    private int ypos;

    public enum paddleType { HORIZONTAL, VERTICAL}
    private paddleType type;
    private int xchange;
    private int ychange;
    private PingPong runningapp;

    public Paddle(PingPong app, int x, int y, paddleType type){
        this.xpos = x;
        this.ypos = y;
        this.type = type;
        this.length = LEN;
        this.width = WD;
        this.runningapp = app;
        //System.out.println("Here");
    }

    public void updateLocation() {
        if (this.type == paddleType.HORIZONTAL) {
            if (xpos + xchange + width < runningapp.getWidth() && xpos + xchange > 0) xpos += xchange;
        } else {
            if (ypos + ychange + length< runningapp.getHeight() && ypos + ychange > 0) ypos += ychange;
        }
    }

    public void updateSize(){

    }

    public void draw(Graphics g){
        //System.out.println("Here");
        g.setColor(Color.BLACK);
        if(this.type == paddleType.HORIZONTAL)
            g.fillRect(xpos,ypos,width,length);
        else
            g.fillRect(xpos,ypos,length,width);
    }

    public void keyrelease(int keyCode) {
        if(type == paddleType.HORIZONTAL){
            if(keyCode == VK_RIGHT || keyCode == VK_LEFT)
                xchange = 0;
        }
        if(type == paddleType.VERTICAL){
            if(keyCode == VK_UP || keyCode == VK_DOWN)
                ychange = 0;
        }
    }

    public void keypress(int keyCode) {
        if(type == paddleType.VERTICAL) {
            if (keyCode == VK_UP)
                ychange = 1;
            if (keyCode == VK_DOWN)
                ychange = -1;
        }else{
            if (keyCode == VK_LEFT)
                xchange = -1;
            if (keyCode == VK_RIGHT)
                xchange = 1;
        }
    }
}
