package Networking;

import Utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

//import static java.lang.System.out;

/**
 * Created by akedia on 28/04/16.
 */
public class NetworkBase {
    public class MyServer implements Runnable {
        private int port = 8080;
        private ServerSocket serverSocket = null;
        private boolean isStopped = false;

        public MyServer(int port) {
            this.port = port;
        }

        public void run() {
            openServerSocket();
            while (!isStopped) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                processClientRequest(clientSocket);
            }
        }

        public void openServerSocket() {
            try{
                this.serverSocket = new ServerSocket(port);
                System.out.println("Opened server socket. InetAddress="+serverSocket.getInetAddress().toString());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Cannot open port : " + port);
            }
        }

        private void processClientRequest(Socket clientSocket) {
            try {
                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();

                String dataFromInStream = Utils.getStringFromInputStream(in);
                System.out.println("Data read from in stream:\n"+dataFromInStream);
                out.write("ACK".getBytes());

                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private MyServer myServer;
    private Map<String,Socket> peerSockets;

    public NetworkBase() {
        myServer = new MyServer(8080);
        (new Thread(myServer)).run();
        peerSockets = new HashMap<>();
    }

    public void addPeer(String name,String ip, int port) {
        try {
            Socket peerSocket = new Socket(ip, port);
            peerSockets.put(name,peerSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsgToPeer (String peerName, String msg) {
        Socket peerSocket = peerSockets.get(peerName);
        try {
            OutputStream out = peerSocket.getOutputStream();
            InputStream in = peerSocket.getInputStream();
            out.write(msg.getBytes());
            String responseFromServer = Utils.getStringFromInputStream(in);
            System.out.println("Response from server:\n"+responseFromServer);
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NetworkBase networkBase = new NetworkBase();
    }
}
