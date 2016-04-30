package integration;

import UI.Ball;
import Utils.FloatPair;
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
    private FloatPair ballPosition;
    private FloatPair ballVelocity;
    private Map<Integer,MyVector> paddlePositions;

    public FloatPair getBallPosition(){
        return this.ballPosition;
    }

    public FloatPair getBallVelocity(){
        return this.ballVelocity;
    }

    public Map<Integer,MyVector> getPaddlePositions(){
        return this.paddlePositions;
    }

    public void setBallPosition(FloatPair ballPosition){
        this.ballPosition = ballPosition;
    }

    public void setBallVelocity(FloatPair ballVelocity){
        this.ballVelocity = ballVelocity;
    }

    public void setPaddlePositions(Map<Integer,MyVector> paddlePositions){
        this.paddlePositions = paddlePositions;
    }

    public GameState(){

    }

    public GameState (FloatPair ballPosition, FloatPair ballVelocity, Map<Integer,MyVector> paddlePositions) {
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
            FloatPair ballPosition = FloatPair.fromJSON(jsonObject.getJSONObject("ballPos"));
            FloatPair ballVelocity = FloatPair.fromJSON(jsonObject.getJSONObject("ballVel"));
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
