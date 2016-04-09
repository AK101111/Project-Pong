import javax.swing.*;
import java.awt.*;

/**
 * Created by arnavkansal on 09/04/16.
 */
public class pongBoard extends JPanel {

    private PingPong runningApp;
    private Ball ball;

    public pongBoard(PingPong app){
        this.runningApp = app;
        setBoard(app);
    }

    public void setBoard(PingPong app) {
        ball = new Ball(app);

    }
}
