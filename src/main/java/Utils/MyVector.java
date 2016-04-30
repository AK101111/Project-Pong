package Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akedia on 30/04/16.
 */
public class MyVector {
    private int x;
    private int y;

    public MyVector(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public MyVector() {
        this(0,0);
    }

    public int getX () {
        return x;
    }
    public void setX (int x) {
        this.x = x;
    }
    public int getY () {
        return y;
    }
    public void setY (int y) {
        this.y = y;
    }

    public JSONObject toJSON () {
        return new JSONObject().put("x",x).put("y",y);
    }

    public static MyVector fromJSON (JSONObject asJson) {
        MyVector vector = null;
        try {
            int x = asJson.getInt("x");
            int y = asJson.getInt("y");
            vector = new MyVector(x, y);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return vector;

    }
}
