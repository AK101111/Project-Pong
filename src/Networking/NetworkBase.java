package Networking;

import Utils.Utils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import static java.lang.System.out;

/**
 * Created by akedia on 28/04/16.
 */
public class NetworkBase {
    private ConnectionServer server;
    private Map<String,Socket> peerSockets;

    public NetworkBase(int port, ReceiveObjectListener receiveObjectListener) {
        server = new ConnectionServer(8080,receiveObjectListener);
        (new Thread(server)).start();
        peerSockets = new HashMap<>();
    }

    public void addPeer(String name, String ip, int port, long timeoutMillis, PeerConnectionListener connectionListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                long startTime = System.currentTimeMillis();
                while (socket == null && (System.currentTimeMillis() - startTime < timeoutMillis)) {
                    try {
                        socket = new Socket(ip, port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    peerSockets.put(name,socket);
                    connectionListener.onConnectionSuccess();
                } else {
                    connectionListener.onConnectionFailure();
                }
            }
        }).start();
    }

    public void sendObjectToPeer (String peerName, Object toSendObj) {
        Socket peerSocket = peerSockets.get(peerName);
        try {
            OutputStream out = peerSocket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            objectOutputStream.writeObject(toSendObj);
//            InputStream in = peerSocket.getInputStream();
//            out.write(msg.getBytes());
//            String responseFromServer = Utils.getStringFromInputStream(in);
//            System.out.println("Response from server:\n"+responseFromServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class TestClass implements Serializable {
        String msg;
        int i;
        AbstractList<String> msgs;

        TestClass(String m, int in) {
            msg = m;
            i = in;
            msgs = new ArrayList<>();
            msgs.add("Test 1");
            msgs.add("Test 2");
        }

        public String toString () {
            return String.format("TestClass{msg=%s,i=%d,msgs=%s}",msg,i,msgs.toString());
        }
    }
    public static void main(String[] args) {
        //testing code
        NetworkBase networkBase = new NetworkBase(8080, new ReceiveObjectListener() {
            @Override
            public void onReceive(Object obj) {
                TestClass asTobj = (TestClass) obj;
                System.out.println(asTobj);
            }
        });
        System.out.println("Server started");
        networkBase.addPeer("ashish", "10.192.38.55", 8080, 1, new PeerConnectionListener() {
            @Override
            public void onConnectionSuccess() {
                System.out.println("Connected to Ashish");
                networkBase.sendObjectToPeer("ashish",new TestClass("Hello from kd",10));
            }

            @Override
            public void onConnectionFailure() {
                System.out.println("Failed to connect to Ashish");
            }
        });

//
//        try {
//            Thread.sleep(2000);
//            System.out.println("Recovered from sleep");
//            networkBase.addPeer("ashish","10.192.38.55",8080);
//            networkBase.sendMsgToPeer("ashish","[kd] Hello from kedia");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        try {
////            Socket s = new Socket("10.192.38.55", 8080);
////            System.out.println("Connected to ashish : "+s);
//            Socket s = new Socket();
//            InetSocketAddress ashAdd = new InetSocketAddress("10.192.38.55", 8080);
//            s.connect(ashAdd);
//            OutputStream o = s.getOutputStream();
//            ObjectOutputStream os = new ObjectOutputStream(o);
//
//            TestClass obj = new TestClass("gandu",10000);
//            os.writeObject(obj);
//
//
////            o.write("hi from kedia\n".getBytes());
//            System.out.println(s);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
