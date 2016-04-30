package UI;

import java.awt.*;

import static UI.Constants.scoreheight;
import static UI.Constants.scorewidth;

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
    private Paddle[] Players;

    public Dashboard(int n_players, PingPong runningapp){
        this.runningapp = runningapp;
        this.nplayers = n_players;
        this.Scores = new int[n_players];
        for(int i=0;i<n_players;++i) Scores[i]=3;
        Players = new Paddle[4];
//        Players = runningapp.getBoard().getPlayers();
    }

    public void updateScore(int player, int change) {
        Players = runningapp.getBoard().getPlayers();
        if(player!=-1) {
            if(Scores[player] == 1 && change == -1){
                Scores[player]=0;
                //Players[player].setdead(true);
            }
            if (!(Scores[player] == 0 && change == -1))
                Scores[player] += change;
        }
    }

    public void draw(Graphics g){
        //System.out.println("Here");
        g.setColor(Color.WHITE);
        for(int i=0;i<nplayers;++i){
            if(Scores[i]!=0) g.drawString(String.valueOf(Scores[i]), scorewidth[i],scoreheight[i]);
        }
    }
}

