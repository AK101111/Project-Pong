package integration;

import UI.Ball;
import Utils.MyVector;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by akedia on 30/04/16.
 */
public class GameState {
    private MyVector ballPosition;
    private Ball.BallVelocity ballVelocity;
    private Map<Integer,MyVector> paddlePositions;

    public MyVector getBallPosition(){
        return this.ballPosition;
    }

    public Ball.BallVelocity getBallVelocity(){
        return this.ballVelocity;
    }

    public Map<Integer,MyVector> getPaddlePositions(){
        return this.paddlePositions;
    }

    public void setBallPosition(MyVector ballPosition){
        this.ballPosition = ballPosition;
    }

    public void setBallVelocity(Ball.BallVelocity ballVelocity){
        this.ballVelocity = ballVelocity;
    }

    public void setPaddlePositions(Map<Integer,MyVector> paddlePositions){
        this.paddlePositions = paddlePositions;
    }

    public GameState(){

    }

    public GameState (MyVector ballPosition, Ball.BallVelocity ballVelocity, Map<Integer,MyVector> paddlePositions) {
        this.ballPosition = ballPosition;
        this.ballVelocity = ballVelocity;
        this.paddlePositions = paddlePositions;
    }

    public JSONObject toJSON () {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ballPos",ballPosition.toJSON());
        jsonObject.put("ballVel",ballVelocity.toJSON());
//        jsonObject.put("paddlePos",paddlePositions);
        return jsonObject;
    }

    public static GameState fromJSON (JSONObject jsonObject) {
        GameState state = null;
        try {
//            System.out.println("gamestate json:\n" + jsonObject.toString(4));
            MyVector ballPosition = MyVector.fromJSON(jsonObject.getJSONObject("ballPos"));
            Ball.BallVelocity ballVelocity = Ball.BallVelocity.fromJSON(jsonObject.getJSONObject("ballVel"));
//            JSONObject paddlePositionsJSON = jsonObject.getJSONObject("paddlePos");
//            Map<Integer,MyVector> paddlePositions = new HashMap<>();
//            for (String key : paddlePositionsJSON.keySet()) {
//                int paddleId = Integer.parseInt(key);
//                MyVector pos = MyVector.fromJSON(paddlePositionsJSON.getJSONObject(key));
//                paddlePositions.put(paddleId,pos);
//            }
            state = new GameState(ballPosition,ballVelocity,null);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return state;
    }
}
