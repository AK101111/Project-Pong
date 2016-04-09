package src;

import java.awt.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class Paddle {
    private static final int LEN=5,WD=10;
    private int length;
    private int width;
    private int xpos;
    private int ypos;
    public enum paddleType { HORIZONTAL, VERTICAL}
    private paddleType type;

    public Paddle(PingPong app, int x, int y, paddleType type){
        this.xpos = x;
        this.ypos = y;
        this.type = type;
        this.length = LEN;
        this.width = WD;
    }

    public void updateLocation(){

    }

    public void updateSize(){

    }

    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        if(this.type == paddleType.HORIZONTAL)
            g.fillRect(xpos,ypos,width,length);
        else
            g.fillRect(xpos,ypos,length,width);
    }
}
