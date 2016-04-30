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
    private Map<Integer,Integer> scores;

    public FloatPair getBallPosition(){
        return this.ballPosition;
    }

    public FloatPair getBallVelocity(){
        return this.ballVelocity;
    }

    public Map<Integer,Integer> getScores() {
        return scores;
    }

    public void setScores (Map<Integer,Integer> scores) {
        this.scores = scores;
    }

    public void setBallPosition(FloatPair ballPosition){
        this.ballPosition = ballPosition;
    }

    public void setBallVelocity(FloatPair ballVelocity){
        this.ballVelocity = ballVelocity;
    }

    public GameState(){

    }

    public GameState (FloatPair ballPosition, FloatPair ballVelocity, Map<Integer,Integer> scores) {
        this.ballPosition = ballPosition;
        this.ballVelocity = ballVelocity;
        this.scores = scores;
    }

    public JSONObject toJSON () {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ballPos",ballPosition.toJSON());
        jsonObject.put("ballVel",ballVelocity.toJSON());
        jsonObject.put("scores",scores);
        return jsonObject;
    }

    public static GameState fromJSON (JSONObject jsonObject) {
        GameState state = null;
        try {
//            System.out.println("gamestate json:\n" + jsonObject.toString(4));
            FloatPair ballPosition = FloatPair.fromJSON(jsonObject.getJSONObject("ballPos"));
            FloatPair ballVelocity = FloatPair.fromJSON(jsonObject.getJSONObject("ballVel"));
            JSONObject scoresJSON = jsonObject.getJSONObject("scores");
            Map<Integer,Integer> scores = new HashMap<>();
            for (String key : scoresJSON.keySet()) {
                int id = Integer.valueOf(key);
                int score = scoresJSON.getInt(key);
                scores.put(id,score);
            }
            state = new GameState(ballPosition,ballVelocity,scores);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return state;
    }
}
