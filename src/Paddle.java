/**
 * Created by arnavkansal on 09/04/16.
 */
public class Paddle {
    private static final int LEN=5,WD=10;
    private int length;
    private int width;
    private int xpos;
    private int ypos;
    private enum paddleType { HORIZONTAL, VERTICAL}
    private paddleType type;

    public Paddle(PingPong app, int x, int y, paddleType type){
        this.xpos = x;
        this.ypos = y;
        this.type = type;
        this.length = LEN;
        this.width = WD;
    }

}
