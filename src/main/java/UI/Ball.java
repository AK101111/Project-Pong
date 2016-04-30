package UI;

import Utils.MyVector;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.Random;

import static UI.Constants.*;
import static java.lang.Math.abs;
import static javax.swing.UIManager.getInt;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class Ball {
    private int xpos;
    private int ypos;
    private int radius=RADIUS;
    private int widthBound = SCREEN_WIDTH;
    private int heightBound;
    private PingPong runningapp;
    private int lasthit;
    private Random rn;
    BallVelocity ballVelocity;

    public void setPos(MyVector pos) {
        this.xpos = pos.getX();
        this.ypos = pos.getY();
    }

    public MyVector getPos(){
        MyVector vector = new MyVector(this.xpos,this.ypos);
        return vector;
    }

    public void setVel(BallVelocity vel) {
        this.ballVelocity = vel;
    }

    public static class BallVelocity{
        public float xspeed;
        public float yspeed;

        public BallVelocity() {

        }

//        public BallVelocity(float x, float y){
//            this.yspeed = y;
//            this.xspeed = x;
//        }
        public static BallVelocity fromJSON (JSONObject asJson) {
            BallVelocity vector = null;
            try {
                float x = (float)asJson.getDouble("x");
                float y = (float)asJson.getDouble("y");
                vector = new BallVelocity();
                vector.xspeed = x;
                vector.yspeed = y;
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return vector;
        }
    }
    
    public Ball(PingPong app, BallVelocity velocity){
        this.runningapp = app;
        xpos = XCENTER;
        ypos = YCENTER;
        rn = new Random();
        ballVelocity = velocity;
        // put ball in center
        getWorkingScreenSize();
        lasthit = -1;
    }

    public void updateLocation(){
        Paddle[] Players = runningapp.getBoard().getPlayers();
        switch (paddleHit()) {
            case 0:
                ballVelocity.yspeed = -ballVelocity.yspeed;
                //if(Players[0].getxchange()+ballVelocity.xspeed>=-1 && Players[0].getxchange()+ballVelocity.xspeed<=1) ballVelocity.xspeed += Players[0].getxchange();
                // Player0 update
                lasthit = 0;
                break;
            case 1:
                ballVelocity.xspeed = -ballVelocity.xspeed;
                //if(Players[1].getychange()+ballVelocity.yspeed>=-1 && Players[1].getychange()+ballVelocity.yspeed<=1) ballVelocity.yspeed += Players[1].getychange();
                // Player1 update
                lasthit = 1;
                break;
            case 2:
                ballVelocity.yspeed = -ballVelocity.yspeed;
                //if(Players[2].getxchange()+ballVelocity.xspeed>=-1 && Players[2].getxchange()+ballVelocity.xspeed<=1) ballVelocity.xspeed += Players[2].getxchange();
                // Player2 update
                lasthit = 2;
                break;
            case 3:
                ballVelocity.xspeed = -ballVelocity.xspeed;
                //if(Players[3].getychange()+ballVelocity.yspeed>=-1 && Players[3].getychange()+ballVelocity.yspeed<=1) ballVelocity.yspeed += Players[3].getychange();
                // Player3 update
                lasthit = 3;
                break;
            default:
                break;
        }
        switch (wallHit()) {
            case 0:
                ballVelocity.yspeed = -ballVelocity.yspeed;
                // Player0 update
                runningapp.getBoard().getDashboard().updateScore(0,-1);
                break;
            case 1:
                ballVelocity.xspeed = -ballVelocity.xspeed;
                // Player1 update
                runningapp.getBoard().getDashboard().updateScore(1,-1);
                break;
            case 2:
                ballVelocity.yspeed = -ballVelocity.yspeed;
                // Player2 update
                runningapp.getBoard().getDashboard().updateScore(2,-1);
                break;
            case 3:
                ballVelocity.xspeed = -ballVelocity.xspeed;
                // Player3 update
                runningapp.getBoard().getDashboard().updateScore(3,-1);
                break;
            default:
                break;
        }
        xpos += ballVelocity.xspeed;
        ypos += ballVelocity.yspeed;
    }

    public void updateSpeed(){

    }
//    Player0 = new Paddle(app,200,5, HORIZONTAL, HUMAN);
//    Player1 = new Paddle(app,390,182, VERTICAL, AI);
//    Player2 = new Paddle(app,200,370, HORIZONTAL, AI);
//    Player3 = new Paddle(app,5,182, VERTICAL, AI);

    public int paddleHit(){
        Paddle[] Players = runningapp.getBoard().getPlayers();
        for(int i=0; i< Players.length; ++i) {
            switch (i){
                case 0:
                    if(Players[i].getxCollisionBounds(1).left<= xpos && xpos <= Players[i].getxCollisionBounds(1).right && ypos-LEN==Players[i].getypos() && !(Players[i].getdead())){
                        //System.out.println(String.format("Collide with %d Paddle",i+1));
                        return i;
                    }
                    break;
                case 1:
                    //System.out.println(String.format("up %d down %d ball %d",Players[i].getyCollisionBounds().left,Players[i].getyCollisionBounds().right,ypos));
                    if(Players[i].getyCollisionBounds(1).left<= ypos && ypos <= Players[i].getyCollisionBounds(1).right && xpos+(2*radius)>=Players[i].getxpos() && !(Players[i].getdead())){
                        //System.out.println(String.format("Collide with %d Paddle %d %d",i+1,xpos,ypos));
                        return i;
                    }
                    break;
                case 2:
                    if(Players[i].getxCollisionBounds(-1).left<= xpos && xpos <= Players[i].getxCollisionBounds(-1).right && ypos+(2*radius)==Players[i].getypos() && !(Players[i].getdead())){
                        //System.out.println(String.format("Collide with %d Paddle",i+1));
                        return i;
                    }
                    break;
                case 3:
                    if(Players[i].getyCollisionBounds(-1).left<= ypos && ypos <= Players[i].getyCollisionBounds(-1).right && xpos-LEN<=Players[i].getxpos() && !(Players[i].getdead())){
                        //System.out.println(String.format("Collide with %d Paddle",i+1));
                        return i;
                    }
                    break;
            }
        }
        return -1;
    }

    public int wallHit(){
        if(ypos < 0 ) {
            //System.out.println(String.format("case = %d, xpos = %d, ypos = %d,ball pos %d %d",0,xpos,ypos,this.runningapp.getBoard().getPlayers()[0].getxpos(),this.runningapp.getBoard().getPlayers()[0].getypos()));
            return 0;
        }
        if(xpos + 2*radius >= SCREEN_WIDTH){
            //System.out.println(String.format("case = %d, xpos = %d, ypos = %d,ball pos %d %d",1,xpos,ypos,this.runningapp.getBoard().getPlayers()[1].getxpos(),this.runningapp.getBoard().getPlayers()[1].getypos()));
            return 1;
        }
        if(ypos + 2*radius >= heightBound) {
            //System.out.println(String.format("case = %d, xpos = %d, ypos = %d,ball pos %d %d",2,xpos,ypos,this.runningapp.getBoard().getPlayers()[2].getxpos(),this.runningapp.getBoard().getPlayers()[2].getypos()));
            return 2;
        }
        if(xpos < 0){
            //System.out.println(String.format("case = %d, xpos = %d, ypos = %d,ball pos %d %d",3,xpos,ypos,this.runningapp.getBoard().getPlayers()[3].getxpos(),this.runningapp.getBoard().getPlayers()[3].getypos()));
            return 3;
        }
        return -1;
    }

    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.fillOval(xpos,ypos,2*radius,2*radius);
    }

    public void getWorkingScreenSize() {
        heightBound = SCREEN_HEIGHT - 21;
    }


    public int getxpos() {
        return xpos;
    }

    public int getypos() {
        return ypos;
    }
}