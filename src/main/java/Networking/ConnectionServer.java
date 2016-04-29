package Networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by akedia on 28/04/16.
 */
public class ConnectionServer implements Runnable {
    private int port = 8080;
    private ServerSocket serverSocket = null;
    private boolean isStopped = false;

    private ReceiveListener receiveListener = null;

    public ConnectionServer(int port, ReceiveListener receiveListener) {
        this.port = port;
        this.receiveListener = receiveListener;
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
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String receiveString = bufferedReader.readLine();
        if (receiveListener != null) {
            receiveListener.onReceive(receiveString);
        }
    }

}