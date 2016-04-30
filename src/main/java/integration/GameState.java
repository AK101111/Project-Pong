package integration;

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
    private MyVector ballVelocity;
    private Map<Integer,MyVector> paddlePositions;

    public GameState (MyVector ballPosition, MyVector ballVelocity, Map<Integer,MyVector> paddlePositions) {
        this.ballPosition = ballPosition;
        this.ballVelocity = ballVelocity;
        this.paddlePositions = paddlePositions;
    }

    public JSONObject toJSON () {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ballPos",ballPosition);
        jsonObject.put("ballVel",ballVelocity);
        jsonObject.put("paddlePos",paddlePositions);
        return jsonObject;
    }

    public static GameState fromJSON (JSONObject jsonObject) {
        GameState state = null;
        try {
            MyVector ballPosition = MyVector.fromJSON(jsonObject.getJSONObject("ballPos"));
            MyVector ballVelocity = MyVector.fromJSON(jsonObject.getJSONObject("ballVel"));
            JSONObject paddlePositionsJSON = jsonObject.getJSONObject("paddlePos");
            Map<Integer,MyVector> paddlePositions = new HashMap<>();
            for (String key : paddlePositionsJSON.keySet()) {
                int paddleId = Integer.parseInt(key);
                MyVector pos = MyVector.fromJSON(paddlePositionsJSON.getJSONObject(key));
                paddlePositions.put(paddleId,pos);
            }
            state = new GameState(ballPosition,ballVelocity,paddlePositions);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return state;
    }
}
