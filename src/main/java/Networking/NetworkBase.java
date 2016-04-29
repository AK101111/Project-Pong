package Networking;

import Utils.Utils;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.*;

//import static java.lang.System.out;

/**
 * Created by akedia on 28/04/16.
 */
public class NetworkBase {
    private ConnectionServer server;
    private Map<String,Socket> peerSockets;

    public NetworkBase(int port, ReceiveObjectListener receiveObjectListener) {
        server = new ConnectionServer(port,receiveObjectListener);
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

    public interface MultiplePeerConnectionListener {
        void onAllConnectionsRes (boolean allSuccess, List<Integer> failedPeerList);
    }
    private static class PeerConnectionStore {
        MultiplePeerConnectionListener listener;
        int numPeers;
        int limit;
        List<Integer> failedToConnectPeers;
        void incrementNumPeers() {
            numPeers++;
            if (numPeers == limit) {
                boolean success = failedToConnectPeers.isEmpty();
                listener.onAllConnectionsRes(success,failedToConnectPeers);
            }
        }

        public void setConnected (Integer i) {
            incrementNumPeers();
        }
        public void setNotConnected (Integer i) {
            failedToConnectPeers.add(i);
            incrementNumPeers();
        }
        public PeerConnectionStore(int limit, MultiplePeerConnectionListener listener){
            this.limit = limit;
            this.listener = listener;
            this.numPeers = 0;
            this.failedToConnectPeers = new ArrayList<>();
        }
    }
    public void addMultiplePeers (Map<Integer,String> peerList, long timeoutEachMillis, MultiplePeerConnectionListener handler) {
        PeerConnectionStore connectionStore = new PeerConnectionStore(peerList.size(), handler);
//        myName = 0;
        for (Integer i : peerList.keySet()) {
            if (!peerList.get(i).isEmpty()) {
                addPeer(Integer.toString(i), peerList.get(i), 8080, timeoutEachMillis, new PeerConnectionListener() {
                    @Override
                    public void onConnectionSuccess() {
//                        hideTextField(i-1, "Connected to : " + peerList.get(i));
//                        connectedPeers.add(i);
                        connectionStore.setConnected(i);
//                        network.sendObjectToPeer(Integer.toString(i),new ConnectionRequest(NetworkBase.getIPAddress(),myName));
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

    private class GetSocketIndefinitely implements Callable<Socket> {
        Socket socket = null;
        String ip;
        int port;

        GetSocketIndefinitely(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public Socket call() {
            while (socket != null) {
                try {
                    socket = new Socket(ip, port);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            return socket;
        }
    }

    public void addPeerImp (String name, String ip, int port, long timeoutMillis, PeerConnectionListener connectionListener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Socket> future = executor.submit(new GetSocketIndefinitely(ip,port));
        Socket socket = null;
        try {
            socket = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        if (socket != null) {
            peerSockets.put(name,socket);
            connectionListener.onConnectionSuccess();
        } else {
            connectionListener.onConnectionFailure();
        }
    }

    public boolean isPeer (String ip) {
        return peerSockets.containsValue(ip);
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

    public void sendToAllPeers (Object toSendObj) {
        for (String peer : peerSockets.keySet()) {
            sendObjectToPeer(peer,toSendObj);
        }
    }

    public static String getIPAddress() {
        try {
            InetAddress localAdd = InetAddress.getLocalHost();
            return localAdd.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class TimeStamp implements Serializable {
        public long time;
        TimeStamp () {
            time = System.currentTimeMillis();
        }
    }
    public static void main(String[] args) {
        //testing code
        NetworkBase networkBase = new NetworkBase(8080, new ReceiveObjectListener() {
            @Override
            public void onReceive(Object obj) {
                long currentTime = System.currentTimeMillis();
                TimeStamp sendTime = (TimeStamp) obj;
                System.out.println("Network-delay : " + (currentTime - sendTime.time));
            }
        });
        System.out.println("Server started @ IP:" + getIPAddress());
        networkBase.addPeer("kedia", "10.192.37.237", 8080, 10000, new PeerConnectionListener() {
            @Override
            public void onConnectionSuccess() {
                System.out.println("Connected to kedia");
                networkBase.sendObjectToPeer("kedia",new TimeStamp());
            }

            @Override
            public void onConnectionFailure() {
                System.out.println("Failed to connect to kedia");
            }
        });
    }
}
