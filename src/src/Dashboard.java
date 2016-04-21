package src;

import java.awt.*;

import static src.constants.scoreheight;
import static src.constants.scorewidth;

/**
 * Created by arnavkansal on 21/04/16.
 */
public class Dashboard {
    private int length;
    private int width;
    private int xpos;
    private int ypos;
    private int nplayers;
    private int Scores[];
    private PingPong runningapp;

    public Dashboard(int n_players){
        this.runningapp = runningapp;
        this.nplayers = n_players;
        this.Scores = new int[n_players];
        for(int i=0;i<n_players;++i) Scores[i]=0;
    }

    public void updateScore(int player, int change) {
        if(player!=-1) {
            if (!(Scores[player] == 0 && change == -1))
                Scores[player] += change;
        }
    }

    public void draw(Graphics g){
        //System.out.println("Here");
        for(int i=0;i<nplayers;++i){
            g.drawString(String.valueOf(Scores[i]), scorewidth[i],scoreheight[i]);
        }
    }
}

