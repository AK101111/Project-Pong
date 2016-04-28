package Networking;

import Utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by akedia on 28/04/16.
 */
public class ConnectionServer implements Runnable {
    private int port = 8080;
    private ServerSocket serverSocket = null;
    private boolean isStopped = false;

    private ReceiveObjectListener receiveObjectListener = null;

    public ConnectionServer(int port, ReceiveObjectListener receiveObjectListener) {
        this.port = port;
        this.receiveObjectListener = receiveObjectListener;
    }

    public ConnectionServer(int port) {
        this(port,null);
    }

    public ConnectionServer() {
        this(8080);
    }

    public void run() {
        openServerSocket();
        while (!isStopped) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                processClientRequest(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void processClientRequest(Socket clientSocket) throws IOException{
        InputStream in = clientSocket.getInputStream();
//        OutputStream out = clientSocket.getOutputStream();
//        String dataFromInStream = Utils.getStringFromInputStream(in);
//        System.out.println("Data read from in stream:\n"+dataFromInStream);
//        out.write("ACK".getBytes());

        ObjectInputStream objectInputStream = new ObjectInputStream(in);
        Object receivedObject = null;
        try {
            receivedObject = objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (receiveObjectListener != null) {
            receiveObjectListener.onReceive(receivedObject);
        }
    }

}