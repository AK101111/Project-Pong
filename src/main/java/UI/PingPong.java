package UI;

import Networking.NetworkBase;
import integration.AbstractGameUI;
import integration.GameState;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;
import static UI.Constants.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class PingPong extends JFrame{
    private PongBoard board;
    public int difficulty;
    Ball.BallVelocity ballVelocity;
    int myName;
    AbstractGameUI.PaddleMoveListener paddleMoveListener;
    Map<Integer,String> peersList;
    boolean[] isPaddleTypeSet = {false,false,false,false};

    public PingPong(int difficulty, Ball.BallVelocity velocity){
        this.difficulty = difficulty;
        this.ballVelocity = velocity;
//        renderDisplay();
    }
    public PingPong(){

    }

    public PongBoard getBoard(){
        return this.board;
    }

    private void renderDisplay(){
        setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        setResizable(false);
        //setTitle("Ping Pong");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setBackground(Color.BLACK);
        // center window on screen
        setLocationRelativeTo(null);
        // testcomp and testactive are to be set
        board = new PongBoard(this,testactive,testcomp,ballVelocity);//,testother);
        board.setOnInternalPaddleMoveListener(paddleMoveListener);

        setPaddleTypes();

        board.startpongBoard(this);
        add(board);
    }

    private void setPaddleTypes(){
        //setting my paddle type
        board.setPaddleAsKeyboardControlled(myName,true);
        isPaddleTypeSet[myName] =true;

        //setting all other available player's paddle type
        for(int key : peersList.keySet()){
            if(key!=myName) {
                board.setPaddleAsKeyboardControlled(key, false);
                isPaddleTypeSet[key] = true;
            }
        }

        //setting remaining paddle's type to AI
        for(int i = 0; i < 4; i++){
            if(!isPaddleTypeSet[i]) {
                if(myName == 0)
                    board.setPaddleAsAiControlled(i);
                else
                    board.setPaddleAsKeyboardControlled(i, false);
                isPaddleTypeSet[i] = true;
            }
        }
    }

    public void movePaddle(int id, int delX, int delY){
        if (board != null)
            board.movePaddle(id,delX,delY);
    }

    private JSONObject getGameStateObject (GameState state) {
        return new JSONObject().put("type","sync").put("state",state.toJSON());
    }

    public void syncState (JSONObject stateJSON) {
        GameState gameState = GameState.fromJSON(stateJSON);
        if (board != null) {
            board.setGameState(gameState);
        }
    }

    public void startTimer(NetworkBase network){
        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Trigger sync");
                if (board != null && myName == 0) {
                    GameState gameState = board.getGameState();
                    network.sendJSONToAll(getGameStateObject(gameState));
                }
            }
        },200,200);
    }

    public static PingPong startGame(int difficulty, Ball.BallVelocity velocity, int myName, AbstractGameUI.PaddleMoveListener paddleMoveListener, Map<Integer,String> peersList){
        PingPong app = new PingPong(difficulty, velocity);
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                app.myName = myName;
                app.paddleMoveListener = paddleMoveListener;
                app.peersList = peersList;
                app.renderDisplay();
                app.setVisible(true);

            }
        });
        return app;
    }
}