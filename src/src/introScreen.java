package src;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.plaf.LayerUI;

import static src.constants.*;

// some of source taken from docs.oracle.com/javase/
// gradient idea copied
public class introScreen {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createUI();
            }
        });
    }

    public static void createUI() {
        JFrame f = new JFrame ("Ping Pong");

        LayerUI<JPanel> layerUI = new SpotlightLayerUI();
        JPanel panel = createPanel();
        JLayer<JPanel> jlayer = new JLayer<JPanel>(panel, layerUI);

        f.add (jlayer);

        f.setSize(WINDOW_XSIZE,WINDOW_YSIZE);
        f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo (null);
        f.setVisible (true);
    }

    private static JPanel createPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel();
        JPanel southPanel = new JPanel();
        ButtonGroup entreeGroup = new ButtonGroup();
        JRadioButton radioButton;
        northPanel.add(radioButton = new JRadioButton("Easy"));
        entreeGroup.add(radioButton);
        northPanel.add(radioButton = new JRadioButton("Hard"));
        entreeGroup.add(radioButton);
        northPanel.add(radioButton = new JRadioButton("Insane",true));
        entreeGroup.add(radioButton);

        JPanel centerPanel = new AnimatedJPanel("fireball");

        JButton orderButton = new JButton("Start");
        System.out.println(String.format("ht %d, wd%d",mainPanel.getHeight(),mainPanel.getWidth()));
        //orderButton.setLocation(WINDOW_XSIZE/2,WINDOW_YSIZE/2);
        //orderButton.setBounds(WINDOW_XSIZE/2,WINDOW_YSIZE/2,50,50);
        orderButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String[] args = {getSelectedButtonText(entreeGroup)};
                //centerPanel.pausePanel();
                PingPong.main(args);
                //super.mouseClicked(e);
            }
        });
        southPanel.add(orderButton);
        mainPanel.add(northPanel,BorderLayout.NORTH);
        mainPanel.add(centerPanel,BorderLayout.CENTER);
        mainPanel.add(southPanel,BorderLayout.SOUTH);
        //System.out.println(String.format("ht %d, wd%d",mainPanel.getHeight(),mainPanel.getWidth()));
        return mainPanel;
    }

    public static String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }

        return null;
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