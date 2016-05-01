package UI;

import Networking.NetworkBase;
import Networking.PeerConnectionListener;
import Networking.ReceiveListener;
import Utils.FloatPair;
import integration.AbstractGameUI;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

import static UI.Constants.SCREEN_HEIGHT;
import static UI.Constants.SCREEN_WIDTH;
import static UI.Constants.SPEED_MAGNITUDE;

/**
 * Created by rashish on 01/05/16.
 */
public class GameOverScreen {
    JFrame mainFrame;
    int[] winCount;
    public GameOverScreen(){
        winCount = new int[]{0,0,0,0};
    }

    public int[] getWinCount(){
        return this.winCount;
    }

    public void setWinCount(int[] countArray){
        winCount = countArray;
    }

    public void showWindow(int winnerId){
        winCount[winnerId] += 1;
        IntroScreen.setWinCount(winCount);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createUI(winnerId);
            }
        });
    }

    public void createUI(int winnerId) {
        mainFrame = new JFrame ("Ping Pong");

        JPanel panel = createPanel(winnerId);
        JLayer<JPanel> jlayer = new JLayer<JPanel>(panel);

        mainFrame.add (jlayer);
//        mainFrame.pack();
        mainFrame.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        mainFrame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo (null);
        mainFrame.setVisible (true);
    }

    private JPanel createPanel(int winnerId) {
        int playerName = winnerId + 1;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel winNoticeLabel = new JLabel();
        winNoticeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winNoticeLabel.setText("Player " + playerName + " Win !");

        GridLayout gridLayout = new GridLayout(4,0);
        JPanel scorePanel = new JPanel();
        JLabel scoreTitleLabel = new JLabel("Scores :");
        scoreTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scorePanel.add(scoreTitleLabel);
        for(int i = 0; i < 4; i++)
            scorePanel.add(getScoreLabel(i));
        scorePanel.setLayout(gridLayout);

        JButton replayButton = new JButton();
        replayButton.setText("Replay");
        replayButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                onGameRestart();
            }
        });

        mainPanel.add(winNoticeLabel,BorderLayout.NORTH);
        mainPanel.add(scorePanel,BorderLayout.CENTER);
        mainPanel.add(replayButton,BorderLayout.SOUTH);

        return mainPanel;
    }

    private JLabel getScoreLabel(int id){
        int playerName = id + 1;

        JLabel scoreLabel = new JLabel();
        scoreLabel.setText("Player " + playerName + " score : " + winCount[id]);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        return scoreLabel;
    }

    private void onGameRestart(){
        IntroScreen.onPlayButtonClicked();
        mainFrame.setVisible(false);
    }

}