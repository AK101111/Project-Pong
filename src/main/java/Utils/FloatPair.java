package Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by arnavkansal on 30/04/16.
 */
public class FloatPair {
    public float x;
    public float y;

    public FloatPair(float x, float y){
        this.x = x;
        this.y = y;
    }

    public FloatPair(){}

    public static FloatPair fromJSON (JSONObject asJson) {
        FloatPair fp = null;
        try {
            float x = (float)asJson.getDouble("x");
            float y = (float)asJson.getDouble("y");
            fp = new FloatPair(x,y);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return fp;
    }
    public JSONObject toJSON () {
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("x", x);
            json.put("y", y);
        }catch (JSONException ex) {
            ex.printStackTrace();
        }
        return json;
    }
}
