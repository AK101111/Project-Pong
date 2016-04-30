package UI;


import java.awt.*;
import java.util.Arrays;

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
    private OnDeadListener onDeadListener;

    public Dashboard(int n_players, PingPong runningapp,OnDeadListener onDead){
        this.runningapp = runningapp;
        this.nplayers = n_players;
        this.Scores = new int[n_players];
        for(int i=0;i<n_players;++i) Scores[i]=3;
        Players = new Paddle[4];
        this.onDeadListener = onDead;
//        Players = runningapp.getBoard().getPlayers();
    }

    public void setScoreDead(int tag) {
        Scores[tag]=0;
//        System.out.println(String.format("scores : %s", Arrays.toString(Scores)));
    }

    public static interface OnDeadListener{
         void onPlayerDead(int id);
    }

    public void updateScore(int player, int change) {
//        System.out.println("update score : player : "+player + " ,change : "+change);
        Players = runningapp.getBoard().getPlayers();
        for(int i = 0; i < Players.length; i++){
            System.out.print(Players[i].tage + ",");
        }
//        System.out.println(String.format("pre-scores : %s", Arrays.toString(Scores)));

        if(player!=-1) {
            if(Scores[player] == 1 && change == -1){
                Scores[player]=0;
//                System.out.println("player : "+player+":" + Players[player].getdead() +"score: "+Scores[player]);
                Players[player].setdead(true);
                onDeadListener.onPlayerDead(player);
            }
            if (!(Scores[player] == 0 && change == -1))
                Scores[player] += change;
        }
        System.out.println(String.format("post-scores of %d : %s",Players[player].tage, Arrays.toString(Scores)));

    }

    public void draw(Graphics g){
        //System.out.println("Here");
        g.setColor(Color.WHITE);
        for(int i=0;i<nplayers;++i){
            if(Scores[i]!=0) g.drawString(String.valueOf(Scores[i]), scorewidth[i],scoreheight[i]);
        }
    }
}

