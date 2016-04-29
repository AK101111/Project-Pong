package Networking;

import Utils.Utils;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

//import static java.lang.System.out;

/**
 * Created by akedia on 28/04/16.
 */
public class NetworkBase {
    private ConnectionServer server;
    private Map<String,Socket> peerSockets;

    public NetworkBase(int port, ReceiveListener receiveListener) {
        server = new ConnectionServer(port,receiveListener);
        (new Thread(server)).start();
        peerSockets = new HashMap<String, Socket>();
    }

    public void addPeer(final String name, final String ip, final int port, final long timeoutMillis, final PeerConnectionListener connectionListener) {
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
            this.failedToConnectPeers = new ArrayList<Integer>();
        }
    }
    public void addMultiplePeers (Map<Integer,String> peerList, long timeoutEachMillis, MultiplePeerConnectionListener handler) {
        final PeerConnectionStore connectionStore = new PeerConnectionStore(peerList.size(), handler);
//        myName = 0;
        for (final Integer i : peerList.keySet()) {
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

    public void sendToPeer (String peerName, String msg) {
        Socket peerSocket = peerSockets.get(peerName);
        try {
            OutputStream out = peerSocket.getOutputStream();
            out.write((msg+"\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendJSON (String peerName, JSONObject jsonObject) {
        sendToPeer(peerName,jsonObject.toString());
    }

    public void sendToAllPeers (String msg) {
        for (String peer : peerSockets.keySet()) {
            sendToPeer(peer,msg);
        }
    }

    public void sendJSONToAll (JSONObject jsonObject) {
        sendToAllPeers(jsonObject.toString());
    }

    public static String getIPAddress() {
        try {
            InetAddress localAdd = InetAddress.getLocalHost();
            String ip =  localAdd.getHostAddress();
            if (!isLocalIP(ip))
                return ip;
            else
                return getNonLocalIP();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isPreferredNetworkInterface (String name) {
        if (name.matches("^en|eth|wlan"))
            return true;
        else
            return false;
    }
    private static boolean isLocalIP (String ip) {
        return ip.matches("^127.");
    }

    private static String getNonLocalIP () {
        String preferredIP = null;
        String someIP = null;
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) e.nextElement();
                String name = networkInterface.getName();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        String thisIP = address.getHostAddress();
                        if (!isLocalIP(thisIP)) {
                            someIP = thisIP;
                            if (isPreferredNetworkInterface(name)) {
                                preferredIP = thisIP;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (preferredIP != null)
            return preferredIP;
        else
            return someIP;
    }


//    public static class TimeStamp implements Serializable {
//        public long time;
//        TimeStamp () {
//            time = System.currentTimeMillis();
//        }
//    }
//    public static void main(String[] args) {
//        //testing code
//        NetworkBase networkBase = new NetworkBase(8080, new ReceiveObjectListener() {
//            @Override
//            public void onReceive(Object obj) {
//                long currentTime = System.currentTimeMillis();
//                TimeStamp sendTime = (TimeStamp) obj;
//                System.out.println("Network-delay : " + (currentTime - sendTime.time));
//            }
//        });
//        System.out.println("Server started @ IP:" + getIPAddress());
//        networkBase.addPeer("kedia", "10.192.37.237", 8080, 10000, new PeerConnectionListener() {
//            @Override
//            public void onConnectionSuccess() {
//                System.out.println("Connected to kedia");
//                networkBase.sendObjectToPeer("kedia",new TimeStamp());
//            }
//
//            @Override
//            public void onConnectionFailure() {
//                System.out.println("Failed to connect to kedia");
//            }
//        });
//    }
}
