package src;

import Networking.NetworkBase;
import Networking.PeerConnectionListener;
import Networking.ReceiveObjectListener;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.LayerUI;

import static src.constants.*;


// some of source taken from docs.oracle.com/javase/
// gradient idea copied
public class introScreen {
    static JTextField ipTextField1,ipTextField2,ipTextField3;
    static JLabel ipLabel1,ipLabel2,ipLabel3,statusLabel;
    static JLabel myIPAddressText;
    static MouseAdapter onConnectListener;
    static JButton actionButton;
    static int myName;
    static NetworkBase network;
    static ButtonGroup entreeGroup;

    public static class ConnectionRequest implements Serializable {
        public String senderIP;
        public int senderName;
        public ConnectionRequest (String ip, int name) {
            senderIP = ip;
            senderName = name;
        }
    }

    public static class MessageObject implements Serializable{
        public String messageType;
        public String message;
        public String senderIP;
        public int senderName;
        public MessageObject(String msgType, String msg){
            this.messageType = msgType;
            this.message = msg;
        }
        public void setSenderDetails(String ip, int name){
            this.senderIP = ip;
            this.senderName = name;
        }
    }

    static ReceiveObjectListener receiveObjectListener = new ReceiveObjectListener() {
        @Override
        public void onReceive(Object obj) {
            System.out.println("Received object " + obj);
            if (obj instanceof ConnectionRequest) {
                ConnectionRequest connectionRequest = (ConnectionRequest) obj;
                if (!network.isPeer(connectionRequest.senderIP)) {
                    network.addPeer(Integer.toString(connectionRequest.senderName), connectionRequest.senderIP, 8080, 100, new PeerConnectionListener() {
                        @Override
                        public void onConnectionSuccess() {
                            //connected
                            int name = connectionRequest.senderName + 1;
                            hideTextField(getUnfilledConnectionLabel(),"Connected to " + name + " (" + connectionRequest.senderIP + ")");
                        }

                        @Override
                        public void onConnectionFailure() {
                            //Failed to connect
                        }
                    });
                }
            }
            else if(obj instanceof MessageObject){
                MessageObject messageObject = (MessageObject) obj;
                if(messageObject.messageType.equals("connection") && messageObject.message.equals("connectedToAll")){
                    setConnectionReadyLabel(messageObject.senderIP);
                    if(isAllConnectionReady())
                        setActionButton(true,"Play",startGameListener);
                }
            }
            //TODO
        }
    };

    static MouseAdapter startGameListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            String[] args = {Integer.toString(getSelectedButtonPosition(entreeGroup))};
            PingPong.main(args);
        }
    };



    private static void setActionButton(boolean enabled, String label, MouseAdapter mouseAdapter){
        actionButton = new JButton(label);
        actionButton.setPreferredSize(new Dimension(50, 30));
        actionButton.addMouseListener(mouseAdapter);
        actionButton.setEnabled(enabled);
    }

    private static boolean isAllConnectionReady(){
        JLabel[] labels = {ipLabel1,ipLabel2,ipLabel3,myIPAddressText};
        for(JLabel label : labels){
            if(!label.getText().contains(" - Ready!")){
                return false;
            }
        }
        return true;
    }

    private static void setConnectionReadyLabel(String ip){
        JLabel[] labels = {ipLabel1,ipLabel2,ipLabel3};
        for(JLabel label : labels){
            if(label.getText().contains(ip)){
                label.setText(label.getText() + " - Ready!");
                break;
            }
        }
    }

    public static void main(String[] args) {
        network = new NetworkBase(8080,receiveObjectListener);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createUI();
            }
        });
    }

    public static void createUI() {
        JFrame f = new JFrame ("Ping Pong");

//        LayerUI<JPanel> layerUI = new SpotlightLayerUI();
        JPanel panel = createPanel();
        JLayer<JPanel> jlayer = new JLayer<JPanel>(panel);

        f.add (jlayer);
        f.pack();
        f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo (null);
        f.setVisible (true);
    }

    private static JPanel createPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel northPanel = new JPanel();
        JPanel southPanel = new JPanel();
        southPanel.setBorder(new EmptyBorder(20, 50, 0, 50));
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


                Map<Integer,String> filledIps = getListedIpAddresses();
                System.out.println(filledIps.toString());



//                if(ipTextField1.isVisible()) {
//                    hideTextField(0);
//                    statusLabel.setText("Connecting...");
//                }else {
//                    showTextField(0);
//                    statusLabel.setText("");
//                }
                disableButton(actionButton);
                connectToPeerList(filledIps,statusLabel, actionButton);
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
            this.failedToConnectPeers = new ArrayList<>();
        }
    }

    //0, 1, 2
    private static void connectToPeerList (Map<Integer,String> peerList, JLabel indicatorLabel,JButton connectButton) {

        PeerConnectionStore connectionStore = new PeerConnectionStore(peerList.size(), new PeerConnectionStore.AllConnectionsResListener() {
            @Override
            public void onAllConnectionsRes(List<Integer> failedList) {
                if (failedList.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed to connect to ");
                    for (Integer i : failedList) {
                        sb.append(i + ",");
                    }
                    sb.append(".Please remove.");
                    indicatorLabel.setText(sb.toString());
                    reEnableButton(connectButton);
                } else {
                    indicatorLabel.setText("Connected to all.");
                    System.out.println("Move ahead");
                }
            }
        });

        myName = 0;
        for (Integer i : peerList.keySet()) {
            if (!peerList.get(i).isEmpty()) {
                network.addPeer(Integer.toString(i), peerList.get(i), 8080, 1, new PeerConnectionListener() {
                    @Override
                    public void onConnectionSuccess() {
                        int name = i + 1;
                        hideTextField(i-1, "Connected to " + name + " (" + peerList.get(i) + ")");
//                        connectedPeers.add(i);
                        connectionStore.setConnected(i);
                        network.sendObjectToPeer(Integer.toString(i),new ConnectionRequest(NetworkBase.getIPAddress(),myName));
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


    private static void hideTextField(int textFieldId,String statusText){
        switch (textFieldId){
            case 0:
//                ipLabel1.setText("Connected to "+ ipTextField1.getText());
                ipLabel1.setText(statusText);
                ipTextField1.setVisible(false);
                break;
            case 1:
//                ipLabel2.setText("Connected to "+ ipTextField2.getText());
                ipLabel2.setText(statusText);
                ipTextField2.setVisible(false);
                break;
            case 2:
//                ipLabel3.setText("Connected to "+ ipTextField3.getText());
                ipLabel3.setText(statusText);
                ipTextField3.setVisible(false);
                break;

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
        Map<Integer,String> ipList = new HashMap<>();
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
            i ++;
        }

        return i;
    }
}
class AnimatedJPanel extends JPanel implements ActionListener{
    private Timer clock;
    private ImageIcon imageArray[];
    private int delay=PHOTO_DELAY, totalFrames=N_PHOTOS, currentFrame=0;

    public AnimatedJPanel(String photo){
        imageArray = new ImageIcon[totalFrames];
        for(int i=1;i<=imageArray.length; ++i){
            imageArray[i-1] = new ImageIcon(IMAGES_PATH+photo+i+".png");
        }
        clock = new Timer(delay,this);
        clock.start();
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(currentFrame==imageArray.length) currentFrame = 0;
        imageArray[currentFrame++].paintIcon(this,g,this.getWidth()/2-25,this.getHeight()/2-25);
    }
    @Override
    public void actionPerformed(ActionEvent e){
       // System.out.println("Here");
        repaint();
    }
    public static void pausePanel(){
        //clock.stop();
    }
    public void resumePanel(){
        //clock.start();
    }
}

class SpotlightLayerUI extends LayerUI<JPanel> {
    private boolean mActive;
    private int mX, mY;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JLayer jlayer = (JLayer)c;
        jlayer.setLayerEventMask(
                AWTEvent.MOUSE_EVENT_MASK |
                        AWTEvent.MOUSE_MOTION_EVENT_MASK
        );
    }

    @Override
    public void uninstallUI(JComponent c) {
        JLayer jlayer = (JLayer)c;
        jlayer.setLayerEventMask(0);
        super.uninstallUI(c);
    }

    @Override
    public void paint (Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D)g.create();

        // Paint the view.
        super.paint (g2, c);

        if (mActive) {
            // Create a radial gradient, transparent in the middle.
            java.awt.geom.Point2D center = new java.awt.geom.Point2D.Float(mX, mY);
            float radius = 150;
            float[] dist = {0.3f, 1.0f};
            Color[] colors = {new Color(0.0f, 0.0f, 0.0f, 0.0f), Color.BLACK};
            RadialGradientPaint p =
                    new RadialGradientPaint(center, radius, dist, colors);
            g2.setPaint(p);
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0f));
            g2.fillRect(0, 0, c.getWidth(), c.getHeight());
        }

        g2.dispose();
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer l) {
        if (e.getID() == MouseEvent.MOUSE_ENTERED) mActive = true;
        if (e.getID() == MouseEvent.MOUSE_EXITED) mActive = false;
        l.repaint();
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer l) {
        Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);
        mX = p.x;
        mY = p.y;
        l.repaint();
    }

}