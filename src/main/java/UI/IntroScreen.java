package UI;

import Networking.NetworkBase;
import Networking.PeerConnectionListener;
import Networking.ReceiveListener;
import Utils.FloatPair;
import integration.AbstractGameUI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.LayerUI;

import static UI.Constants.*;


// some of source taken from docs.oracle.com/javase/
// gradient idea copied
public class IntroScreen {
    static JFrame mainFrame;
    static JTextField ipTextField1,ipTextField2,ipTextField3;
    static JLabel ipLabel1,ipLabel2,ipLabel3,statusLabel;
    static JLabel myIPAddressText;
    static MouseAdapter onConnectListener;
    static Map<Integer,String> peersList;
    static JButton actionButton = new JButton();
    static int myName;
    static NetworkBase network;
    static ButtonGroup entreeGroup;
    static Map<String,Boolean> isPeerReady = new HashMap<>();
    static PingPong pingPong = new PingPong();

    static ArrayList<Integer> toBeConnectedIPs = new ArrayList<>();

    static FloatPair ballVelocity = new FloatPair();

    static ReceiveListener receiveListener = new ReceiveListener() {
        @Override
        public void onReceive(String str) {
//            System.out.println("[Rx]" + str);
            try {
                JSONObject jsonObject = new JSONObject(str);
                String type = jsonObject.getString("type");
                if (type.equals("connectionRequest")) {
                    final String senderIP = jsonObject.getString("senderIP");
                    int senderName = jsonObject.getInt("senderName");
                    int receiverName = jsonObject.getInt("receiverName");
                    myName = receiverName;
                    if (!network.isPeer(Integer.toString(senderName))) {
                        toBeConnectedIPs.add(senderName);
                        System.out.println("[connectionRequest start] got connection request. senderIP=" + senderIP + ", senderName=" + senderName);
                        network.addPeer(Integer.toString(senderName), senderIP, 8080, 100, new PeerConnectionListener() {
                            @Override
                            public void onConnectionSuccess() {
                                //connected
//                                hideTextField(getUnfilledConnectionLabel(),"Connected to : " + senderIP);
//                                connectedIPs.add(senderIP);
                                int labelId = getUnfilledConnectionLabel();
                                System.out.println("[print to label][connectionRequest] labelId=" + labelId + ", senderIP=" + senderIP + ", senderName="+senderName);
                                displayConnectedToPeer(labelId,senderIP,senderName,false);
                                System.out.println("[connectionRequest end] connected to (senderIP=" + senderIP + ", senderName=" + senderName + "). New peerNames :" + network.connectedPeersNames());
                            }
    //
                            @Override
                            public void onConnectionFailure() {
                                //Failed to connect
                            }
                        });
                    }
                } else if (type.equals("peerList")) {
                    Map<Integer,String> peersMap = new HashMap<>();
                    JSONObject peers = jsonObject.getJSONObject("peers");
                    Iterator<String> keys = peers.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        int name = Integer.valueOf(key);
                        String ip = peers.getString(key);
                        if (!network.isPeer(Integer.toString(name)) && !NetworkBase.getIPAddress().equals(ip)) {
                            peersMap.put(name, ip);
                            isPeerReady.put("ip",false);
                        }
                        if (peersList == null) {
                            peersList = new HashMap<>();
                        }
                        peersList.put(name,ip);
                    }
                    System.out.println("[PeerList] originalPeers="+peers+", peersMap="+peersMap);
                    network.addMultiplePeers(peersMap, 10, new NetworkBase.MultiplePeerConnectionListener() {
                        @Override
                        public void onAllConnectionsRes(boolean allSuccess, List<Integer> failedPeerList, List<Integer> connectedPeerList) {
                            System.out.println("PeerList Connection result : allSuccess="+allSuccess + ", failedPeerList="+failedPeerList + ", connectedPeerList="+connectedPeerList);
                            System.out.println("toBeConnectedIPs="+toBeConnectedIPs);
                            if (allSuccess) {
                                statusLabel.setText("Connected to all.");
                                myIPAddressText.setText(myIPAddressText.getText() +  " - Ready!");
                                disableButton(actionButton);
                                //connected to all
                                network.sendJSONToAll(getConnectedToAllJson());
                                for (Integer peerName : connectedPeerList) {
                                    if (!toBeConnectedIPs.contains(peerName)) {
                                        int labelId = getUnfilledConnectionLabel();
                                        System.out.println("[print to label][peerList] labelId=" + labelId + ", senderIP=" + peersMap.get(peerName) + ", senderName=" + peerName);
                                        displayConnectedToPeer(labelId, peersMap.get(peerName), peerName, false);
                                    }
                                }
                            }
                        }
                    });

                }
                else if(type.equals("connectedToAll")){
                    String senderIP = jsonObject.getString("senderIP");
                    int senderName = jsonObject.getInt("senderName");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("[Rx connectedToAll 'ready' start] senderName=" +senderName + ", peerNames="+network.connectedPeersNames());
                            while (!network.isPeer(Integer.toString(senderName))) {}
                            System.out.println("[Rx connectedToAll 'ready' end] peerNames="+network.connectedPeersNames());
                            while(!setConnectionReadyLabel(senderIP)) {}
                        }
                    }).start();
                    isPeerReady.replace(jsonObject.getString("senderIP"),true);
                    if(isAllConnectionReady()) {
                        if(myName == 0) {
                            actionButton.removeMouseListener(onConnectListener);
                            setActionButton(true, "Play", startGameListener);
                        }else
                            statusLabel.setText("Waiting for game to commence...");
                    }
                }else if(type.equals("ballVelocity") && myName != 0){
                    ballVelocity = new FloatPair((float) jsonObject.getDouble("xspeed"),(float)jsonObject.getDouble("yspeed"));
                    startGame();
                }else if(type.equals("paddleMove")){
                    pingPong.movePaddle(jsonObject.getInt("id"),jsonObject.getInt("delX"),jsonObject.getInt("delY"));
                }else if(type.equals("sync")) {
                    pingPong.syncState(jsonObject.getJSONObject("state"));
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
//
            //TODO
        }
    };



    static JSONObject getConnectedToAllJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","connectedToAll");
        jsonObject.put("senderIP",NetworkBase.getIPAddress());
        jsonObject.put("senderName",myName);
        return jsonObject;
    }

    static JSONObject getBallVelocityJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","ballVelocity");
        jsonObject.put("xspeed",ballVelocity.x);
        jsonObject.put("yspeed",ballVelocity.y);
        return jsonObject;
    }

    static MouseAdapter startGameListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(myName == 0) {
                        statusLabel.setText("Starting game...");
                        sendBallVelocity();
                        startGame();
                    }
                }
            }).start();
        }
    };

    static integration.AbstractGameUI.PaddleMoveListener paddleMoveListener = new AbstractGameUI.PaddleMoveListener() {
        @Override
        public void handlePaddleMove(int id, int delX, int delY) {
            network.sendJSONToAll(getPaddleMoveJson(id,delX,delY));
        }
    };

    private static JSONObject getPaddleMoveJson(int id, int delX, int delY){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","paddleMove");
        jsonObject.put("id",id);
        jsonObject.put("delX",delX);
        jsonObject.put("delY",delY);
        return jsonObject;
    }

    private static void startGame(){
        pingPong = pingPong.startGame(getSelectedButtonPosition(entreeGroup),ballVelocity,myName,paddleMoveListener,peersList);
        mainFrame.setVisible(false);
        pingPong.startTimer(network);
    }

    private static void sendBallVelocity(){
        ballVelocity.x = (float)Math.cos(2*Math.PI*Math.random())*SPEED_MAGNITUDE;
        ballVelocity.y = (float)Math.sin(2*Math.PI*Math.random())*SPEED_MAGNITUDE;
        network.sendJSONToAll(getBallVelocityJson());
    }



    private static void setActionButton(boolean enabled, String label, MouseAdapter mouseAdapter){
        actionButton.setText(label);
        actionButton.setPreferredSize(new Dimension(50, 30));
        actionButton.addMouseListener(mouseAdapter);
        actionButton.setEnabled(enabled);
    }

    private static boolean isAllConnectionReady(){
        if(isPeerReady == null || isPeerReady.isEmpty())
            return false;
        else{
            for(String key : isPeerReady.keySet()){
                if(!isPeerReady.get(key)){
                   return false;
                }
            }
        }
        return true;
    }

    private static boolean setConnectionReadyLabel(String ip){
        JLabel[] labels = {ipLabel1,ipLabel2,ipLabel3};
        for(JLabel label : labels){
            if(label.getText().contains(ip)){
                label.setText(label.getText() + " - Ready!");
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        network = new NetworkBase(8080,receiveListener);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createUI();
            }
        });
    }

    public static void createUI() {
        mainFrame = new JFrame ("Ping Pong");

//        LayerUI<JPanel> layerUI = new SpotlightLayerUI();
        JPanel panel = createPanel();
        JLayer<JPanel> jlayer = new JLayer<JPanel>(panel);

        mainFrame.add (jlayer);
//        mainFrame.pack();
        mainFrame.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        mainFrame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo (null);
        mainFrame.setVisible (true);
    }

    private static JPanel createPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel northPanel = new JPanel();
        JPanel southPanel = new JPanel();
        southPanel.setBorder(new EmptyBorder(20, 80, 0, 80));
        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        entreeGroup = new ButtonGroup();
        JRadioButton radioButton;
        northPanel.add(radioButton = new JRadioButton("Easy"));
        entreeGroup.add(radioButton);
        northPanel.add(radioButton = new JRadioButton("Hard"));
        entreeGroup.add(radioButton);
        northPanel.add(radioButton = new JRadioButton("Insane",true));
        entreeGroup.add(radioButton);

//        JPanel centerPanel = new AnimatedJPanel("fireball");

        GridLayout connectButtonLayout = new GridLayout(2,0);
        connectButtonLayout.setVgap(10);
        statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        System.out.println(String.format("ht %d, wd%d",mainPanel.getHeight(),mainPanel.getWidth()));
        //orderButton.setLocation(WINDOW_XSIZE/2,WINDOW_YSIZE/2);
        //orderButton.setBounds(WINDOW_XSIZE/2,WINDOW_YSIZE/2,50,50);


        myIPAddressText = new JLabel("Your IP Address : ");
        myIPAddressText.setHorizontalAlignment(SwingConstants.CENTER);
        myIPAddressText.setText(myIPAddressText.getText() + NetworkBase.getIPAddress());
        onConnectListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                String[] args = {getSelectedButtonText(entreeGroup)};
//                //centerPanel.pausePanel();
//                PingPong.main(args);

                //super.mouseClicked(e);

                statusLabel.setText("Connecting...");


                peersList = getListedIpAddresses();
                System.out.println(peersList.toString());



//                if(ipTextField1.isVisible()) {
//                    hideTextField(0);
//                    statusLabel.setText("Connecting...");
//                }else {
//                    showTextField(0);
//                    statusLabel.setText("");
//                }
                disableButton(actionButton);
                connectToPeerList(peersList,statusLabel, actionButton);
            }
        };

        setActionButton(true,"Connect",onConnectListener);

        JPanel textFieldPanel = new JPanel();
        GridLayout textFieldLayout = new GridLayout(3,0);
        GridLayout centerLayout = new GridLayout(2,0);
        ipTextField1 = new JTextField(10);
        ipTextField2 = new JTextField(10);
        ipTextField3 = new JTextField(10);
        ipLabel1 = new JLabel();
        ipLabel2 = new JLabel();
        ipLabel3 = new JLabel();

        textFieldPanel.add(getTextFieldLayout("Player 2 IP Address :",ipTextField1,ipLabel1),BorderLayout.NORTH);
        textFieldPanel.add(getTextFieldLayout("Player 3 IP Address :",ipTextField2,ipLabel2),BorderLayout.CENTER);
        textFieldPanel.add(getTextFieldLayout("Player 4 IP Address :",ipTextField3,ipLabel3),BorderLayout.SOUTH);

        centerPanel.add(myIPAddressText,BorderLayout.NORTH);
        centerPanel.add(textFieldPanel,BorderLayout.CENTER);
        centerPanel.setSize(10,10);
        textFieldPanel.setLayout(textFieldLayout);
        centerPanel.setLayout(centerLayout);


        southPanel.add(statusLabel,BorderLayout.NORTH);
        southPanel.add(actionButton,BorderLayout.SOUTH);
        southPanel.setLayout(connectButtonLayout);
        mainPanel.add(northPanel,BorderLayout.NORTH);
        mainPanel.add(centerPanel,BorderLayout.CENTER);
        mainPanel.add(southPanel,BorderLayout.SOUTH);
        //System.out.println(String.format("ht %d, wd%d",mainPanel.getHeight(),mainPanel.getWidth()));
        return mainPanel;
    }


    private static class PeerConnectionStore {
        interface AllConnectionsResListener {
            void onAllConnectionsRes(List<Integer> failedList);
        }

        AllConnectionsResListener listener;
        int numPeers;
        int limit;
        List<Integer> failedToConnectPeers;
        void incrementNumPeers() {
            numPeers++;
            if (numPeers == limit) {
                listener.onAllConnectionsRes(failedToConnectPeers);
            }
        }

        public void setConnected (Integer i) {
            incrementNumPeers();
        }
        public void setNotConnected (Integer i) {
            failedToConnectPeers.add(i);
            incrementNumPeers();
        }
        public PeerConnectionStore(int limit, AllConnectionsResListener listener){
            this.limit = limit;
            this.listener = listener;
            this.numPeers = 0;
            this.failedToConnectPeers = new ArrayList<Integer>();
        }
    }

    private static JSONObject getConnectionRequestObject(String senderIP, int senderName, int receiverName) {
        JSONObject obj = new JSONObject();
        obj.put("type","connectionRequest");
        obj.put("senderIP",senderIP);
        obj.put("senderName",senderName);
        obj.put("receiverName",receiverName);
        return obj;
    }

    private static JSONObject getPeerListRequestObject (Map<Integer,String> peersList, String senderIP, Integer senderName) {
        JSONObject obj = new JSONObject();
        peersList.put(senderName,senderIP);
        obj.put("type","peerList");
        obj.put("peers",peersList);
        return obj;
    }

    //0, 1, 2
    private static void connectToPeerList (final Map<Integer,String> peerList, final JLabel indicatorLabel, final JButton connectButton) {

        final PeerConnectionStore connectionStore = new PeerConnectionStore(peerList.size(), new PeerConnectionStore.AllConnectionsResListener() {
            @Override
            public void onAllConnectionsRes(List<Integer> failedList) {
                if (failedList.size() > 0) {
                    //Failed TODO check for errors
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed to connect to ");
                    for (Integer i : failedList) {
                        sb.append(i + ",");
                    }
                    sb.append("\nPlease remove.");
                    indicatorLabel.setText(sb.toString());
                    reEnableButton(connectButton);
                } else {
                    for(int key : peerList.keySet()){
                        isPeerReady.put(peerList.get(key),false);
                    }
                    //connected to all
                    indicatorLabel.setText("Connected to all.");
                    System.out.println("Move ahead");

                    myIPAddressText.setText(myIPAddressText.getText() +  " - Ready!");
                    if(isPeerReady != null && isPeerReady.containsKey(NetworkBase.getIPAddress()))
                        isPeerReady.replace(NetworkBase.getIPAddress(),true);
//                    network.sendToAllPeers(new PeerList(peerList,NetworkBase.getIPAddress(),myName));
                    network.sendJSONToAll(getPeerListRequestObject(peerList,NetworkBase.getIPAddress(),myName));
                    network.sendJSONToAll(getConnectedToAllJson());

                }
            }
        });

        myName = 0;
        for (final Integer i : peerList.keySet()) {
            if (!peerList.get(i).isEmpty()) {
                network.addPeer(Integer.toString(i), peerList.get(i), 8080, 1, new PeerConnectionListener() {
                    @Override
                    public void onConnectionSuccess() {
//                        hideTextField(i-1, "Connected to " + name + " (" + peerList.get(i) + ")");
                        displayConnectedToPeer(i-1,peerList.get(i),i,false);
//                        connectedPeers.add(i);
//                        network.sendObjectToPeer(Integer.toString(i),new ConnectionRequest(NetworkBase.getIPAddress(),myName));
                        network.sendJSON(Integer.toString(i),getConnectionRequestObject(NetworkBase.getIPAddress(),myName,i));
                        connectionStore.setConnected(i);
                    }

                    @Override
                    public void onConnectionFailure() {
//                        failedToConnectPeers.add(i);
                        connectionStore.setNotConnected(i);
                    }
                });
            }
        }
    }

    private static JLabel getLabel (int id) {
        switch (id) {
            case 0:
                return ipLabel1;
            case 1:
                return ipLabel2;
            case 2:
                return ipLabel3;
        }
        return null;
    }

    private static JTextField getTextField (int id) {
        switch (id) {
            case 0:
                return ipTextField1;
            case 1:
                return ipTextField2;
            case 2:
                return ipTextField3;
        }
        return null;
    }

//    private static void hideTextField(int textFieldId,String statusText){
    private static void displayConnectedToPeer(int textFieldId, String ip, int name,boolean toShowReady){
        JLabel label = getLabel(textFieldId);
        JTextField textField = getTextField(textFieldId);
        if (!toShowReady) {
            if (label.getText().contains("Connected to"))
                return;
        }
        int nameToShow = name + 1;
        String statusText = String.format("Connected to %s (%s)%s", " Player " + nameToShow,ip,toShowReady?" - Ready!":"");
        label.setText(statusText);
        textField.setVisible(false);
        if (textField.getText().isEmpty()) {
            textField.setText(ip);
        }
    }
    private static void showTextField(int textFieldId){
        switch (textFieldId){
            case 0:
                ipLabel1.setText("Player 2 IP Address :");
                ipTextField1.setVisible(true);
                break;
            case 1:
                ipLabel2.setText("Player 3 IP Address :");
                ipTextField2.setVisible(true);
                break;
            case 2:
                ipLabel3.setText("Player 4 IP Address :");
                ipTextField3.setVisible(true);
                break;

        }
    }

    private static void disableButton(JButton button){
        button.setEnabled(false);
        button.removeMouseListener(onConnectListener);
    }

    private static void reEnableButton(JButton button){
        button.setEnabled(true);
        button.addMouseListener(onConnectListener);
    }

    private static JPanel getTextFieldLayout(String hintText, JTextField textField,JLabel label){
        JPanel textFieldPanel = new JPanel();
        label.setText(hintText);
        textFieldPanel.add(label,BorderLayout.WEST);
        textFieldPanel.add(textField,BorderLayout.EAST);
        return textFieldPanel;
    }

    public static Map<Integer, String> getListedIpAddresses(){
        Map<Integer,String> ipList = new HashMap<Integer, String>();
        if (!ipTextField1.getText().isEmpty())
            ipList.put(1,ipTextField1.getText());
        if (!ipTextField2.getText().isEmpty())
            ipList.put(2,ipTextField2.getText());
        if (!ipTextField3.getText().isEmpty())
            ipList.put(3,ipTextField3.getText());
        return ipList;
    }

    private static int getUnfilledConnectionLabel () {
        if (ipTextField1.getText().isEmpty())
            return 0;
        else if (ipTextField2.getText().isEmpty())
            return 1;
        else if (ipTextField3.getText().isEmpty())
            return 2;
        else
            return -1;
    }

    public static int getSelectedButtonPosition(ButtonGroup buttonGroup) {
        int i = 0;
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return i;
            }
            i++;
        }

        return i;
    }
    public static void pausePanel(){
        //clock.stop();
    }
    public void resumePanel(){
        //clock.start();
    }
}